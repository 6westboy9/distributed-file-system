package com.westboy.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.westboy.client.network.newversion.NewNioClient;
import com.westboy.client.network.newversion.ResponseCallback;
import com.westboy.client.network.oldversion.OldNioClient;
import com.westboy.common.entity.Node;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.common.constant.NameNodeResponseStatus;
import com.westboy.common.entity.FileInfo;
import com.westboy.common.entity.UploadFileInfo;
import com.westboy.namenode.rpc.model.*;
import com.westboy.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * 文件系统客户端的实现类
 *
 * @author pengbo
 * @since 2021/1/30
 */
@Slf4j
public class FileSystemImpl implements FileSystem {

    // private static final String NAMENODE_HOSTNAME = "116.85.40.109";
    private static final String NAMENODE_HOSTNAME = "127.0.0.1";
    private static final Integer NAMENODE_PORT = 50070;

    private final NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNodeStub;
    private final OldNioClient oldNioClient;
    private final NewNioClient newNioClient;

    public FileSystemImpl() {
        ManagedChannel channel = NettyChannelBuilder
                .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        nameNodeStub = NameNodeServiceGrpc.newBlockingStub(channel);
        oldNioClient = new OldNioClient();
        newNioClient = new NewNioClient();
        log.info("初始化...");
    }

    @Override
    public boolean mkdir(String path) {
        MkdirRequest request = MkdirRequest.newBuilder()
                .setPath(path)
                .build();
        MkdirResponse response = nameNodeStub.mkdir(request);
        log.info("创建文件响应：{}", response.getStatus());
        return response.getStatus() == NameNodeResponseStatus.STATUS_SUCCESS;
    }

    public boolean createFile(String filename) {
        log.info("发送创建文件目录请求，filename={}", filename);
        CreateFileRequest request = CreateFileRequest.newBuilder()
                .setFilename(filename)
                .build();
        CreateFileResponse response = nameNodeStub.create(request);
        log.info("接收到创建文件目录响应，status={}", response.getStatus());
        return NameNodeResponseStatus.STATUS_SUCCESS.equals(response.getStatus());
    }

    @Override
    public void shutdown() {
        ShutdownRequest request = ShutdownRequest.newBuilder()
                .setCode(1)
                .build();
        ShutdownResponse response = nameNodeStub.shutdown(request);
        log.info("关闭响应：{}", response.getStatus());
    }

    @Override
    public void oldUpload(String localFilepath, String remoteFilepath) throws Exception {
        if (!FileUtil.exist(localFilepath)) {
            throw new FileNotFoundException(localFilepath);
        }

        RandomAccessFile accessFile = new RandomAccessFile(localFilepath, GlobalConstant.RW_FILE_MODE);
        log.info("准备上传文件大小为 {} 字节", accessFile.length());

        ByteBuffer buffer = ByteBuffer.allocate((int) accessFile.length());
        FileChannel channel = accessFile.getChannel();
        int read = channel.read(buffer);
        log.info("从磁盘中读取 {} 字节数据到缓存", read);

        buffer.flip();
        byte[] bytes = buffer.array();
        oldUpload(bytes, remoteFilepath);
    }

    @Override
    public void oldUpload(byte[] file, String remoteFilepath) throws Exception {
        // 必须先用 filename 发送一个 RPC 接口调用到 Master 节点，去尝试在文件目录树里创建一个文件
        // 此时还需要进行查重，如果这个文件已经存在，就不让你上传了
        if (!createFile(remoteFilepath)) {
            throw new RuntimeException("在文件目录树中创建该文件失败");
        }

        log.info("在文件目录树中创建该文件成功，文件名称：{}", remoteFilepath);

        // 就是找 master 节点去要多个数据节点的地址
        // 就是你要考虑自己上传几个副本，找对应副本数量的数据节点的地址
        // 尽可能在分配数据节点的时候，保证每个数据节点放的数据量是比较均衡的
        // 保证集群里各个机器上放的数据比较均衡
        int fileSize = file.length;
        FileInfo fileInfo = new FileInfo(remoteFilepath, fileSize);
        List<Node> nodes = allocateDataNodeInfo(fileInfo);
        UploadFileInfo uploadFileInfo = new UploadFileInfo(fileInfo, remoteFilepath, file);

        // 依次把文件的副本上传到各个数据节点上去，还要考虑到，如果上传的过程中，某个数据节点他上传失败，此时你需要有一个容错机制的考量
        for (Node node : nodes) {
            try {
                oldNioClient.sendFile(node.getIp(), node.getPort(), uploadFileInfo);
            } catch (Exception e) {
                Node newNode = reallocateDataNode(uploadFileInfo.getFileInfo(), node);
                if (ObjectUtil.isNotNull(newNode)) {
                    oldNioClient.sendFile(newNode.getIp(), newNode.getPort(), uploadFileInfo);
                }
            }
        }
    }

    @Override
    public void oldDownload(String localFilepath, String remoteFilepath) throws Exception {
        byte[] bytes = oldDownload(remoteFilepath);

        try (RandomAccessFile accessFile = new RandomAccessFile(localFilepath, GlobalConstant.RW_FILE_MODE);
             FileChannel channel = accessFile.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            channel.write(buffer);
        }
    }

    @Override
    public byte[] oldDownload(String remoteFilepath) throws Exception {
        Node datanode = getDataNodeForFile(remoteFilepath, null);

        byte[] bytes;
        try {
            bytes = oldNioClient.readFile(datanode.getIp(), datanode.getPort(), remoteFilepath);
        } catch (Exception e) {
            datanode = getDataNodeForFile(remoteFilepath, datanode);
            bytes = oldNioClient.readFile(datanode.getIp(), datanode.getPort(), remoteFilepath);
        }
        return bytes;
    }

    @Override
    public void newAsyncUpload(String localFilepath, String remoteFilepath, ResponseCallback callback) throws Exception {
        if (!FileUtil.exist(localFilepath)) {
            throw new FileNotFoundException(localFilepath);
        }

        RandomAccessFile accessFile = new RandomAccessFile(localFilepath, GlobalConstant.RW_FILE_MODE);
        log.info("准备上传文件大小为 {} 字节", accessFile.length());

        ByteBuffer buffer = ByteBuffer.allocate((int) accessFile.length());
        FileChannel channel = accessFile.getChannel();
        int read = channel.read(buffer);
        log.info("从磁盘中读取 {} 字节数据到缓存", read);

        buffer.flip();
        byte[] bytes = buffer.array();
        newAsyncUpload(bytes, remoteFilepath, callback);
    }

    @Override
    public void newAsyncUpload(byte[] file, String remoteFilepath, ResponseCallback callback) {
        // 必须先用 filename 发送一个 RPC 接口调用到 Master 节点，去尝试在文件目录树里创建一个文件
        // 此时还需要进行查重，如果这个文件已经存在，就不让你上传了
        if (!createFile(remoteFilepath)) {
            throw new RuntimeException("在文件目录树中创建该文件失败");
        }

        log.info("在文件目录树中创建该文件成功，文件名称：{}", remoteFilepath);

        // 就是找 master 节点去要多个数据节点的地址
        // 就是你要考虑自己上传几个副本，找对应副本数量的数据节点的地址
        // 尽可能在分配数据节点的时候，保证每个数据节点放的数据量是比较均衡的
        // 保证集群里各个机器上放的数据比较均衡
        int fileSize = file.length;
        FileInfo fileInfo = new FileInfo(remoteFilepath, fileSize);
        List<Node> nodes = allocateDataNodeInfo(fileInfo);
        UploadFileInfo uploadFileInfo = new UploadFileInfo(fileInfo, remoteFilepath, file);

        // 依次把文件的副本上传到各个数据节点上去，还要考虑到，如果上传的过程中，某个数据节点他上传失败，此时你需要有一个容错机制的考量
        for (Node node : nodes) {
            try {
                Node host = BeanUtil.copyProperties(node, Node.class);
                newNioClient.sendFile(uploadFileInfo, host, callback);
                // oldNioClient.sendFile(nodeInfo.getIp(), nodeInfo.getPort(), uploadFileInfo);
            } catch (Exception e) {
                Node newNode = reallocateDataNode(uploadFileInfo.getFileInfo(), node);
                if (ObjectUtil.isNotNull(newNode)) {
                    Node host = BeanUtil.copyProperties(newNode, Node.class);
                    newNioClient.sendFile(uploadFileInfo, host, callback);
                    // oldNioClient.sendFile(newNode.getIp(), newNode.getPort(), uploadFileInfo);
                }
            }
        }
    }

    @Override
    public void newAsyncDownload(String localFilepath, String remoteFilepath, ResponseCallback callback) throws Exception {
        byte[] bytes = newAsyncDownload(remoteFilepath, callback);

        try (RandomAccessFile accessFile = new RandomAccessFile(localFilepath, GlobalConstant.RW_FILE_MODE);
             FileChannel channel = accessFile.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            channel.write(buffer);
        }
    }

    @Override
    public byte[] newAsyncDownload(String remoteFilepath, ResponseCallback callback) throws Exception {
        Node datanode = getDataNodeForFile(remoteFilepath, null);
        byte[] bytes;
        try {
            Node host = BeanUtil.copyProperties(datanode, Node.class);
            bytes = newNioClient.readFile(host, remoteFilepath, false, callback);
            // bytes = oldNioClient.readFile(datanode.getIp(), datanode.getPort(), remoteFilepath);
        } catch (Exception e) {
            datanode = getDataNodeForFile(remoteFilepath, datanode);
            Node host = BeanUtil.copyProperties(datanode, Node.class);
            bytes = newNioClient.readFile(host, remoteFilepath, false, callback);
            // bytes = oldNioClient.readFile(datanode.getIp(), datanode.getPort(), remoteFilepath);
        }
        return bytes;
    }

    private Node getDataNodeForFile(String filename, Node excludeNode) {
        ChooseDataNodeRequest request = ChooseDataNodeRequest.newBuilder()
                .setFilename(filename)
                .setExcludedDatanodeInfo(ObjectUtil.isNull(excludeNode) ? "" : JSONUtil.toJsonStr(excludeNode))
                .build();
        ChooseDataNodeResponse response = nameNodeStub.chooseDataNode(request);
        return JSONUtil.toBean(response.getDatanodeInfo(), Node.class);
    }

    private List<Node> allocateDataNodeInfo(FileInfo fileInfo) {
        log.info("发送申请分配 DataNode 节点请求，fileInfo={}", fileInfo);
        AllocateDataNodesRequest request = AllocateDataNodesRequest.newBuilder()
                .setFileInfo(JSONUtil.toJsonStr(fileInfo))
                .build();

        AllocateDataNodesResponse response = nameNodeStub.allocateDataNode(request);
        log.info("接收到申请分配 DataNode 节点响应 fileInfo={}，response={}", fileInfo, response.getDatanodeInfo());
        return JSONUtil.toList(JSONUtil.parseArray(response.getDatanodeInfo()), Node.class);
    }

    private Node reallocateDataNode(FileInfo fileInfo, Node excludedNode) {
        log.info("发送重新申请分配 DataNode 节点请求，fileInfo={}", fileInfo);
        ReallocateDataNodeRequest request = ReallocateDataNodeRequest.newBuilder()
                .setFileInfo(JSONUtil.toJsonStr(fileInfo))
                .setExcludedDatanodeInfo(JSONUtil.toJsonStr(excludedNode))
                .build();

        ReallocateDataNodeResponse response = nameNodeStub.reallocateDataNode(request);
        log.info("接收到重新申请分配 DataNode 节点响应 fileInfo={}，response={}", fileInfo, response.getDatanodeInfo());
        return JSONUtil.toBean(response.getDatanodeInfo(), Node.class);
    }

}
