package com.westboy.namenode.rpcserver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.westboy.common.entity.Node;
import com.westboy.common.FSEditlogTxidRange;
import com.westboy.common.RemoveReplicaTask;
import com.westboy.common.ReplicateReplicaTask;
import com.westboy.common.entity.FileInfo;
import com.westboy.common.entity.StorageInfo;
import com.westboy.namenode.conf.NameNodeConfig;
import com.westboy.namenode.rpc.model.*;
import com.westboy.namenode.rpc.service.NameNodeServiceGrpc;
import com.westboy.namenode.server.Command;
import com.westboy.namenode.server.DataNodeInfo;
import com.westboy.namenode.server.DataNodeManager;
import com.westboy.namenode.server.FSNameSystem;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.westboy.common.constant.NameNodeResponseStatus.*;
import static com.westboy.namenode.server.Command.*;

/**
 * @author pengbo
 * @since 2021/1/30
 */
@Slf4j
public class NameNodeServiceImpl extends NameNodeServiceGrpc.NameNodeServiceImplBase {

    /**
     * 负责管理元数据的核心组件
     */
    private final FSNameSystem nameSystem;
    /**
     * 负责管理集群中的所有 DataNode 组件
     */
    private final DataNodeManager dataNodeManager;
    /**
     * 是否还在运行
     */
    private volatile boolean isRunning = true;
    /**
     * 缓存的 editlog 磁盘上对应文件的数据
     */
    private final JSONArray currentBufferedEditLogs;
    /**
     * 缓存的 editlog 磁盘上对应文件的名称表示的 txid 的范围
     */
    private FSEditlogTxidRange bufferedEditLogTxidRange;

    private final String editLogTemplate;
    private final int fetchSize;

    public NameNodeServiceImpl(NameNodeConfig config, FSNameSystem nameSystem, DataNodeManager dataNodeManager) {
        this.nameSystem = nameSystem;
        this.dataNodeManager = dataNodeManager;
        this.currentBufferedEditLogs = new JSONArray();
        this.editLogTemplate = config.getEditLogTemplate();
        this.fetchSize = config.getFetchSize();
    }

    private boolean existInTxidRange(FSEditlogTxidRange txidRange, long syncTxid) {
        if (ObjectUtil.isNull(txidRange)) {
            return false;
        }
        long start = txidRange.getStartTxid();
        long end = txidRange.getEndTxid();
        long fetchTxid = syncTxid + 1;
        return start <= fetchTxid && fetchTxid <= end;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        Node node = JSONUtil.toBean(request.getDatanodeInfo(), Node.class);
        boolean result = dataNodeManager.register(node);
        RegisterResponse response = RegisterResponse.newBuilder()
                .setStatus(result ? STATUS_SUCCESS : STATUS_FAILURE)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartbeat(HeartbeatRequest request, StreamObserver<HeartbeatResponse> responseObserver) {
        Node node = JSONUtil.toBean(request.getDatanodeInfo(), Node.class);
        boolean result = dataNodeManager.heartbeat(node);

        HeartbeatResponse response;
        List<Command> commands = CollUtil.newArrayList();
        if (result) {
            DataNodeInfo dataNode = dataNodeManager.getDataNode(node.getId());
            List<ReplicateReplicaTask> replicateReplicaTasks = dataNode.listReplicateTasks();
            List<RemoveReplicaTask> removeReplicaTasks = dataNode.listRemoveTasks();
            commands.add(new Command(REPLICATE_REPLICA, JSONUtil.toJsonStr(replicateReplicaTasks)));
            commands.add(new Command(REMOVE_REPLICA, JSONUtil.toJsonStr(removeReplicaTasks)));
            response = HeartbeatResponse.newBuilder()
                    .setStatus(STATUS_SUCCESS)
                    .setCommands(JSONUtil.toJsonStr(commands))
                    .build();
        } else {
            commands.add(new Command(REGISTER));
            commands.add(new Command(REPORT_STORAGE_INFO));
            response = HeartbeatResponse.newBuilder()
                    .setStatus(STATUS_FAILURE)
                    .setCommands(JSONUtil.toJsonStr(commands))
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        MkdirResponse response;
        if (!isRunning) {
            response = MkdirResponse.newBuilder().setStatus(STATUS_SHUTDOWN).build();
        } else {
            log.info("接收到创建文件目录树请求，需要创建的目录：{}", request.getPath());
            boolean result = nameSystem.mkdir(request.getPath());
            response = MkdirResponse.newBuilder().setStatus(result ? STATUS_SUCCESS : STATUS_FAILURE).build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void shutdown(ShutdownRequest request, StreamObserver<ShutdownResponse> responseObserver) {
        // 优雅关闭
        isRunning = false;
        nameSystem.flush();

        ShutdownResponse response = ShutdownResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void fetchEditlog(FetchEditlogRequest request, StreamObserver<FetchEditlogResponse> responseObserver) {
        FetchEditlogResponse response;

        long syncTxid = request.getSyncTxid();
        log.info("处理 BackNode 发送的拉取请求，需要同步的起始 syncTxid={}", syncTxid);

        JSONArray fetchedEditsLog = fetch(syncTxid);
        response = FetchEditlogResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .setEditsLog(fetchedEditsLog.toString()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 获取需要同步的数据
     */
    // 进行过刷盘操作，且磁盘中还有未同步的数据，同步进度使用 syncTxid 表示
    // 处理磁盘上未同步的数据时，先将整个文件缓存到内存中来，使用 currentBufferedEditsLog 存储，切记只能存储一个 editlog 文件数据
    private JSONArray fetch(long syncTxid) {
        if (!existInTxidRange(bufferedEditLogTxidRange, syncTxid)) {
            // 先检查系统当前 DoubleBuffer 中缓存的刷入磁盘数据，判断要拉取的日志是否已经刷入磁盘
            // 如果已经刷入磁盘，则需要从磁盘中拉取，如果没有在 Double Buffer 中查询到要拉取的日志
            // 也不能立即判断直接从 DoubleBuffer 的内存缓存直接拉取，而是要先去查一下磁盘文件，因为
            // 仅仅检查系统当前 DoubleBuffer 中缓存的刷入磁盘数据，是不足矣判断当前要拉取的数据就没在磁盘
            // 因为如果宕机之后，就会清空 DoubleBuffer 中缓存的刷入磁盘数据，没有进行持久化，重启后就会为空

            // 仅表示当前缓存的已经刷入磁盘的 txid 集合，如果宕机之后，就会清空，没有进行持久化
            List<FSEditlogTxidRange> flushedTxidRanges = nameSystem.getFsEditlog().getFlushedTxids();

            if (CollUtil.isNotEmpty(flushedTxidRanges)) {
                for (FSEditlogTxidRange range : flushedTxidRanges) {
                    if (existInTxidRange(range, syncTxid)) {
                        cacheFromFlushedFile(range);
                        break;
                    }
                }
            }

            if (!existInTxidRange(bufferedEditLogTxidRange, syncTxid)) {
                cacheFromCurrentDoubleBuffer();
            }
        }

        return fetchFromBufferedLog(syncTxid);
    }


    /**
     * 添加到 currentBufferedEditLog 缓冲区中
     */
    private void clearAndCache(String[] editLogs) {
        currentBufferedEditLogs.clear();
        if (ArrayUtil.isNotEmpty(editLogs)) {
            JSONObject firstLog = null;
            JSONObject lastLog = null;
            int count = 1;
            for (String editsLog : editLogs) {
                if (StrUtil.isEmpty(editsLog)) {
                    continue;
                }

                JSONObject editLog = JSONUtil.parseObj(editsLog);
                if (count == 1) {
                    firstLog = editLog;
                }
                if (count == editLogs.length) {
                    lastLog = editLog;
                }
                currentBufferedEditLogs.add(editLog);
                count++;
            }

            if (ObjectUtil.isNotNull(firstLog) && ObjectUtil.isNotNull(lastLog)) {
                bufferedEditLogTxidRange = new FSEditlogTxidRange(firstLog.getLong("txid"), lastLog.getLong("txid"));
            }
        }
    }

    /**
     * 从 DoubleBuffer 加载并缓存只
     */
    private void cacheFromCurrentDoubleBuffer() {
        // 还未刷入磁盘的 editlog 日志
        String[] bufferedEditLogs = nameSystem.getFsEditlog().getBufferedEditLogs();
        clearAndCache(bufferedEditLogs);
    }

    /**
     * 从磁盘中加载并缓存
     */
    private void cacheFromFlushedFile(FSEditlogTxidRange flushedTxidRange) {
        // 先从磁盘中加载到缓存中
        long startTxid = flushedTxidRange.getStartTxid();
        long endTxid = flushedTxidRange.getEndTxid();
        String editLogFileName = String.format(editLogTemplate, startTxid, endTxid);
        try {
            List<String> editsLogs = Files.readAllLines(Paths.get(editLogFileName));
            clearAndCache(ArrayUtil.toArray(editsLogs, String.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分批次添加至 fetchedEditsLog 数组中
     */
    private JSONArray fetchFromBufferedLog(long syncTxid) {
        JSONArray fetchedEditsLog = new JSONArray();

        if (existInTxidRange(bufferedEditLogTxidRange, syncTxid)) {
            int fetchCount = 0;
            long fetchTxid = syncTxid == 0 ? 0 : syncTxid + 1;
            for (int i = 0; i < currentBufferedEditLogs.size(); i++) {
                if (fetchTxid == 0) {
                    fetchTxid = currentBufferedEditLogs.getJSONObject(i).getLong("txid");
                }

                if (currentBufferedEditLogs.getJSONObject(i).getLong("txid") == fetchTxid) {
                    fetchedEditsLog.add(currentBufferedEditLogs.getJSONObject(i));
                    fetchTxid = currentBufferedEditLogs.getJSONObject(i).getLong("txid") + 1;
                    fetchCount++;
                }
                if (fetchCount == fetchSize) {
                    break;
                }
            }
        }
        return fetchedEditsLog;
    }

    @Override
    public void updateCheckpointTxid(UpdateCheckpointTxidRequest request, StreamObserver<UpdateCheckpointTxidResponse> responseObserver) {
        long txid = request.getTxid();
        nameSystem.setSyncedTxid(txid, false, true);
        log.info("接收来自 BackNode 的 checkpoint txid 成功，txid={}", txid);
        UpdateCheckpointTxidResponse response = UpdateCheckpointTxidResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
        CreateFileResponse response;
        if (!isRunning) {
            response = CreateFileResponse.newBuilder().setStatus(STATUS_SHUTDOWN).build();
        } else {
            log.info("接收到创建文件目录树请求，需要创建的文件：{}", request.getFilename());
            boolean result = nameSystem.create(request.getFilename());
            response = CreateFileResponse.newBuilder()
                    .setStatus(result ? STATUS_SUCCESS : STATUS_FAILURE)
                    .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void allocateDataNode(AllocateDataNodesRequest request, StreamObserver<AllocateDataNodesResponse> responseObserver) {
        FileInfo fileInfo = JSONUtil.toBean(request.getFileInfo(), FileInfo.class);
        log.info("接收到来自客户端的分配 DataNode 副本请求，fileInfo={}", fileInfo);
        List<DataNodeInfo> nodeInfos = dataNodeManager.allocateDataNodes(fileInfo);
        AllocateDataNodesResponse response = AllocateDataNodesResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .setDatanodeInfo(JSONUtil.toJsonStr(nodeInfos))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 上传时分配节点
     */
    @Override
    public void reallocateDataNode(ReallocateDataNodeRequest request, StreamObserver<ReallocateDataNodeResponse> responseObserver) {
        FileInfo fileInfo = JSONUtil.toBean(request.getFileInfo(), FileInfo.class);
        Node excludedNode = JSONUtil.toBean(request.getExcludedDatanodeInfo(), Node.class);

        DataNodeInfo datanode = dataNodeManager.reallocateDataNode(fileInfo, excludedNode);
        Node node = null;
        if (ObjectUtil.isNotNull(datanode)) {
            node = BeanUtil.copyProperties(datanode, Node.class);
        }

        ReallocateDataNodeResponse response = ReallocateDataNodeResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .setDatanodeInfo(JSONUtil.toJsonStr(node))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reportStorageInfo(ReportStorageInfoRequest request, StreamObserver<ReportStorageInfoResponse> responseObserver) {
        log.info("接收到 DataNode 全量上报存储信息：[node={}，storageInfo={}]", request.getDatanodeInfo(), request.getStorageInfo());

        Node node = JSONUtil.toBean(request.getDatanodeInfo(), Node.class);
        StorageInfo storageInfo = JSONUtil.toBean(request.getStorageInfo(), StorageInfo.class);

        dataNodeManager.setStoredDataSize(node, storageInfo.getStoredDataSize());
        DataNodeInfo dataNode = dataNodeManager.getDataNode(node.getId());

        List<FileInfo> fileInfos = storageInfo.getFiles();
        if (CollUtil.isNotEmpty(fileInfos)) {
            fileInfos.forEach(fileInfo -> nameSystem.addReceivedReplica(dataNode, fileInfo));
        }

        ReportStorageInfoResponse response = ReportStorageInfoResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reportFileInfo(ReportFileInfoRequest request, StreamObserver<ReportFileInfoResponse> responseObserver) {
        log.info("接收到 DataNode 增上报存储信息：[node={}，fileInfo={}]", request.getDatanodeInfo(), request.getFileInfo());

        Node node = JSONUtil.toBean(request.getDatanodeInfo(), Node.class);
        FileInfo fileInfo = JSONUtil.toBean(request.getFileInfo(), FileInfo.class);
        DataNodeInfo dataNode = dataNodeManager.getDataNode(node.getId());

        nameSystem.addReceivedReplica(dataNode, fileInfo);
        ReportFileInfoResponse response = ReportFileInfoResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 下载时分配节点
     */
    @Override
    public void chooseDataNode(ChooseDataNodeRequest request, StreamObserver<ChooseDataNodeResponse> responseObserver) {
        String filename = request.getFilename();
        Node excludedNode;
        String excludedNodeId = null;
        if (StrUtil.isNotEmpty(request.getExcludedDatanodeInfo())) {
            excludedNode = JSONUtil.toBean(request.getExcludedDatanodeInfo(), Node.class);
            excludedNodeId = excludedNode.getId();
        }

        DataNodeInfo datanode = nameSystem.chooseDataNode(filename, excludedNodeId);
        Node node = null;
        if (ObjectUtil.isNotNull(datanode)) {
            node = BeanUtil.copyProperties(datanode, Node.class);
            log.info("接收到客户端读取文件查找 DataNode 请求：filename={}，分配节点信息：datanode=[ip={}，port={}]", filename, node.getIp(), node.getPort());
        } else {
            log.warn("接收到客户端读取文件查找 DataNode 请求：filename={}，没有满足条件的节点", filename);
        }

        ChooseDataNodeResponse response = ChooseDataNodeResponse.newBuilder()
                .setDatanodeInfo(ObjectUtil.isNull(node) ? "" : JSONUtil.toJsonStr(node))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 暴露接口执行重平衡
     */
    @Override
    public void rebalance(RebalanceRequest request, StreamObserver<RebalanceResponse> responseObserver) {
        dataNodeManager.createRebalanceTasks();

        RebalanceResponse response = RebalanceResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
