package com.westboy.namenode.server;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.westboy.common.entity.Node;
import com.westboy.common.FSEditlogTxidRange;
import com.westboy.common.RemoveReplicaTask;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.common.entity.FileInfo;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 负责管理元数据的核心组件
 *
 * @author pengbo
 * @since 2021/1/28
 */
@Slf4j
public class FSNameSystemImpl implements FSNameSystem {

    public static final String EDIT_LOG_FILE_NAME_DELIMITER = "-";

    /**
     * 负责管理内存文件目录树的组件
     */
    private final FSDirectory directory;
    /**
     * 负责管理 editlog 写入磁盘的组件
     */
    private final FSEditlog fsEditlog;
    /**
     * 最近一次 checkpoint 更新到的 txid
     */
    private volatile long syncedTxid;
    /**
     * 每个文件对应的副本所在 DataNode 信息
     */
    private final Map<String /* filename */ , Set<DataNodeInfo>> replicasByFilename;
    /**
     * 每个 DataNode 对应的所有的文件副本
     */
    private final Map<String /* datanodeId */, Set<FileInfo>> filesByDatanode;

    private final ReentrantReadWriteLock replicasLock;
    private final ReentrantReadWriteLock.WriteLock replicasWriteLock;
    private final ReentrantReadWriteLock.ReadLock replicasReadLock;
    private final DataNodeManager dataNodeManager;

    private final String editlogPath;
    private final String editLogPrefix;

    private final String editlogTemplate;
    private final String checkPointMetaFilePath;
    private final String fsimageMetaFilePath;
    private final long editlogCleanInterval;
    private final int dataNodeReplicas;

    private volatile boolean finishedRecover = false;

    public FSNameSystemImpl(NameNodeConfig config, DataNodeManager dataNodeManager) {
        this.replicasByFilename = new HashMap<>();
        this.filesByDatanode = new HashMap<>();
        this.replicasLock = new ReentrantReadWriteLock();
        this.replicasWriteLock = replicasLock.writeLock();
        this.replicasReadLock = replicasLock.readLock();
        this.editlogPath = config.getEditLogPath();
        this.editLogPrefix = config.getEditLogPrefix();
        this.editlogTemplate = config.getEditLogTemplate();
        this.checkPointMetaFilePath = config.getCheckPointTxidFilePath();
        this.fsimageMetaFilePath = config.getFsimageFilePath();
        this.editlogCleanInterval = config.getEditlogCleanerInterval();
        this.dataNodeReplicas = config.getDataNodeReplicas();
        this.dataNodeManager = dataNodeManager;
        this.directory = new FSDirectory();
        this.fsEditlog = new FSEditlog(config);
    }

    public void start() {
        recover();
        new EditlogCleaner().start();
    }

    /**
     * 创建目录
     */
    @Override
    public boolean mkdir(String path) {
        directory.mkdir(path);
        fsEditlog.logEdit(EditlogFactory.mkdir(path));
        return true;
    }

    /**
     * 创建文件
     *
     * @param filepath 文件名称，包含所在的绝对路径，比如 /products/img001.jpg
     */
    @Override
    public boolean create(String filepath) {
        boolean res = directory.create(filepath);
        fsEditlog.logEdit(EditlogFactory.create(filepath));
        return res;
    }

    /**
     * 强制将把内存未被同步的 editlog 刷入磁盘中
     */
    @Override
    public void flush() {
        fsEditlog.flush();
    }

    /**
     * 获取一个 FSEditLog 组件
     */
    @Override
    public FSEditlog getFsEditlog() {
        return fsEditlog;
    }

    @Override
    public void setSyncedTxid(long checkpointTxid, boolean recover, boolean flush) {
        syncedTxid = checkpointTxid;
        fsEditlog.setSyncedTxid(checkpointTxid, recover);
        if (flush) {
            writeCheckpointTxid();
        }
    }

    @Override
    public long getSyncedTxid() {
        return syncedTxid;
    }

    @Override
    public void addReceivedReplica(DataNodeInfo dataNode, FileInfo fileInfo) {
        replicasWriteLock.lock();
        try {
            String filename = fileInfo.getFilename();
            Set<DataNodeInfo> replicas = replicasByFilename.getOrDefault(filename, new HashSet<>());


            // 保存文件对应的节点
            // 上报的文件在 DataNode 节点中不存在
            if (!replicas.contains(dataNode)) {
                // 应对宕机重连后，其他节点同步完成后，再上报就会产生多余数据
                if (replicas.size() == dataNodeReplicas) {
                    dataNode.addStoredDataSize(-fileInfo.getFileLength());
                    Node node = BeanUtil.copyProperties(dataNode, Node.class);
                    dataNode.addRemoveTask(new RemoveReplicaTask(filename, node));
                    return;
                }

                replicas.add(dataNode);
                replicasByFilename.put(filename, replicas);
            }

            String nodeId = dataNode.getId();
            Set<FileInfo> fileInfos = filesByDatanode.getOrDefault(nodeId, new HashSet<>());

            // 保存节点对应的文件
            // 上报的 DataNode 节点从来没有上报过该文件
            if (!fileInfos.contains(fileInfo)) {
                fileInfos.add(fileInfo);
                filesByDatanode.put(nodeId, fileInfos);
            }

            log.info("接收到来自 {} 副本上报的文件：{}，对应副本数量：{}", nodeId, filename, replicas.size());
        } finally {
            replicasWriteLock.unlock();
        }
    }

    @Override
    public DataNodeInfo chooseDataNode(String filename, String excludedNodeId) {
        replicasWriteLock.lock();
        try {
            Set<DataNodeInfo> nodeInfos = replicasByFilename.get(filename);

            if (StrUtil.isNotEmpty(excludedNodeId)) {
                boolean result = nodeInfos.removeIf(nodeInfo -> nodeInfo.getId().equals(excludedNodeId));
                if (result) {
                    log.warn("移除节点，nodeId={}", excludedNodeId);
                }
            }

            if (CollUtil.isNotEmpty(nodeInfos)) {
                Random random = new Random();
                int index = random.nextInt(nodeInfos.size());
                return CollUtil.newArrayList(nodeInfos).get(index);
            }
            return null;
        } finally {
            replicasWriteLock.unlock();
        }
    }

    @Override
    public void removeDataNode(String nodeId) {
        replicasWriteLock.lock();
        try {
            Set<FileInfo> fileInfos = filesByDatanode.get(nodeId);
            if (CollUtil.isNotEmpty(fileInfos)) {
                for (FileInfo fileInfo : fileInfos) {
                    Set<DataNodeInfo> dataNodeInfos = replicasByFilename.get(fileInfo.getFilename());
                    boolean result = dataNodeInfos.removeIf(nodeInfo -> nodeId.equals(nodeInfo.getId()));
                    if (result) {
                        log.warn("移除节点，nodeId={}", nodeId);
                    }
                }
                filesByDatanode.remove(nodeId);
            }
        } finally {
            replicasWriteLock.unlock();
        }
    }

    @Override
    public void removeTheFileOnTheDataNode(String nodeId, FileInfo file) {
        replicasWriteLock.lock();
        try {
            Set<FileInfo> fileInfos = filesByDatanode.get(nodeId);
            fileInfos.removeIf(fileInfo -> file.getFilename().equals(fileInfo.getFilename()));

            Set<DataNodeInfo> dataNodeInfos = replicasByFilename.get(file.getFilename());
            boolean result = dataNodeInfos.removeIf(nodeInfo -> nodeId.equals(nodeInfo.getId()));
            if (result) {
                log.warn("移除节点，nodeId={}", nodeId);
            }
        } finally {
            replicasWriteLock.unlock();
        }
    }

    @Override
    public List<FileInfo> getAllFilesOnTheDatanode(String nodeId) {
        replicasReadLock.lock();
        try {
            Set<FileInfo> fileInfos = filesByDatanode.get(nodeId);
            return CollUtil.isEmpty(fileInfos) ? Collections.emptyList() : CollUtil.newArrayList(fileInfos);
        } finally {
            replicasReadLock.unlock();
        }
    }

    private void writeCheckpointTxid() {
        log.info("保存 checkpointTxid={} 至 {} 中", syncedTxid, checkPointMetaFilePath);

        // 将 txid 更新到 checkpoint-txid.meta 文件
        ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(syncedTxid).getBytes());
        try (RandomAccessFile raf = new RandomAccessFile(checkPointMetaFilePath, GlobalConstant.RW_FILE_MODE);
             FileOutputStream out = new FileOutputStream(raf.getFD());
             FileChannel channel = out.getChannel()) {

            // 直接覆盖写
            channel.position(0);
            channel.write(buffer);
            channel.force(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复元数据
     */
    @Override
    public void recover() {
        loadCheckpointTxid();
        loadFSImage();
        loadEditLog();
        finishedRecover = true;
    }

    private void loadFSImage() {
        if (!FileUtil.exist(fsimageMetaFilePath)) {
            log.info("需要恢复的 {} 文件不存在", fsimageMetaFilePath);
            return;
        }

        try (FileInputStream in = new FileInputStream(fsimageMetaFilePath);
             FileChannel channel = in.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);

            buffer.flip();
            String fsimageJson = new String(buffer.array(), 0, count);

            FSDirectory.INodeDirectory dirTree = JSONUtil.toBean(fsimageJson, FSDirectory.INodeDirectory.class);
            log.info("恢复 {} 文件中的数据", fsimageMetaFilePath);
            directory.setDirTree(dirTree);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCheckpointTxid() {
        if (!FileUtil.exist(checkPointMetaFilePath)) {
            log.info("暂无 {} 文件需要恢复", checkPointMetaFilePath);
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try (RandomAccessFile raf = new RandomAccessFile(checkPointMetaFilePath, GlobalConstant.RW_FILE_MODE);
             FileInputStream out = new FileInputStream(raf.getFD());
             FileChannel channel = out.getChannel()) {

            int count = channel.read(buffer);
            buffer.flip();
            long checkpointTxid = Long.parseLong(new String(buffer.array(), 0, count));
            log.info("恢复 {} 文件出来的数据：checkpointTxid={}", checkPointMetaFilePath, checkpointTxid);
            setSyncedTxid(checkpointTxid, true, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEditLog() {
        try {
            List<FSEditlogTxidRange> allRanges = listAllEditlogRanges();
            List<FSEditlogTxidRange> needRanges = new ArrayList<>();
            for (FSEditlogTxidRange range : allRanges) {
                long startTxid = range.getStartTxid();
                long endTxid = range.getEndTxid();

                // 如果是 checkpointTxid 之后的那些 editLog 都要加载出来
                if (endTxid > syncedTxid) {
                    FSEditlogTxidRange needRange = new FSEditlogTxidRange(startTxid, endTxid);
                    needRanges.add(needRange);
                    String editlogFileName = String.format(editlogTemplate, startTxid, endTxid);

                    List<String> editlogStrList = Files.readAllLines(Paths.get(editlogFileName), StandardCharsets.UTF_8);
                    for (String editlogStr : editlogStrList) {
                        JSONObject editlog = JSONUtil.parseObj(editlogStr);
                        long txid = editlog.getLong("txid");
                        if (txid > syncedTxid) {
                            log.info("准备回放的 editlog={}", editlog);
                            // 回放到内存里去
                            String op = editlog.getStr("OP");
                            String path = editlog.getStr("PATH");
                            if (op.equals("MKDIR")) {
                                directory.mkdir(path);
                            } else if (op.equals("CREATE")) {
                                directory.create(path);
                            }
                        }
                    }
                }
            }

            if (CollUtil.isNotEmpty(needRanges)) {
                log.info("更新已刷入磁盘 editlog 文件列表");
                fsEditlog.addFlushedTxids(needRanges);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<FSEditlogTxidRange> listAllEditlogRanges() {
        List<File> files = listAllEditlogs();
        return files.stream().map(file -> {
            String[] splitName = file.getName().split(EDIT_LOG_FILE_NAME_DELIMITER);
            long startTxid = Long.parseLong(splitName[1]);
            long endTxid = Long.parseLong(splitName[2].split("[.]")[0]);
            return new FSEditlogTxidRange(startTxid, endTxid);
        }).collect(Collectors.toList());
    }


    private List<File> listAllEditlogs() {
        List<File> editLogFiles = FileUtil.loopFiles(editlogPath, file -> StrUtil.startWith(file.getName(), editLogPrefix));
        if (CollUtil.isEmpty(editLogFiles)) {
            return Collections.emptyList();
        }

        editLogFiles.sort((o1, o2) -> {
            Integer o1StartTxid = Integer.valueOf(o1.getName().split(EDIT_LOG_FILE_NAME_DELIMITER)[1]);
            Integer o2StartTxid = Integer.valueOf(o2.getName().split(EDIT_LOG_FILE_NAME_DELIMITER)[1]);
            return o1StartTxid - o2StartTxid;
        });
        return editLogFiles;
    }


    class EditlogCleaner extends Thread {
        public EditlogCleaner() {
            super("edit-log-cleaner");
        }

        @Override
        public void run() {
            log.info("启动 EditlogCleaner 日志文件后台清理线程");
            while (true) {
                try {
                    if (!finishedRecover) {
                        Thread.sleep(100);
                        continue;
                    }

                    List<FSEditlogTxidRange> flushedRanges = fsEditlog.getFlushedTxids();
                    List<FSEditlogTxidRange> allRanges = listAllEditlogRanges();

                    log.debug("当前 flushedRanges 大小：{}", flushedRanges.size());
                    log.debug("当前 allRanges 大小：{}", allRanges.size());

                    List<FSEditlogTxidRange> deleteRanges = CollUtil.intersection(flushedRanges, allRanges).stream()
                            .filter(range -> syncedTxid >= range.getEndTxid()).collect(Collectors.toList());

                    log.debug("当前 deleteRanges 大小：{}", deleteRanges.size());

                    if (CollUtil.isNotEmpty(deleteRanges)) {
                        for (FSEditlogTxidRange range : deleteRanges) {
                            String editLogFile = String.format(editlogTemplate, range.getStartTxid(), range.getEndTxid());
                            if (FileUtil.del(editLogFile)) {
                                log.info("删除已经同步完成的 editlog 日志文件：{}", editLogFile);
                            }
                        }
                    }

                    Thread.sleep(editlogCleanInterval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
