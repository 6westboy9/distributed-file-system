package com.westboy.datanode.rpc;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONUtil;
import com.westboy.common.entity.Node;
import com.westboy.common.entity.FileInfo;
import com.westboy.common.entity.StorageInfo;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.namenode.rpc.model.*;
import com.westboy.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 负责跟一组 NameNode 中的某一个进行通信的线程组件
 *
 * @author pengbo
 * @since 2021/1/29
 */
@Slf4j
public class NameNodeRpcClientImpl implements NameNodeRpcClient {

    private final NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNodeStub;
    private final ExecutorService executor;

    private final Node node;
    private final String namenodeServerIp;
    private final int namenodeServerPort;


    public NameNodeRpcClientImpl(DataNodeConfig config) {
        node = new Node(config.getIp(), config.getHostname(), config.getPort());
        namenodeServerIp = config.getNamenodeServerIp();
        namenodeServerPort = config.getNamenodeServerPort();
        // TODO 貌似没有生效
        executor = Executors.newFixedThreadPool(1, ThreadFactoryBuilder.create().setNamePrefix("namenode-rpc-client-").build());

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(namenodeServerIp, namenodeServerPort)
                .executor(executor)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        nameNodeStub = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    public RegisterResponse register() {
        log.info("发送请求到 NameNode 进行注册");
        RegisterRequest request = RegisterRequest.newBuilder()
                .setDatanodeInfo(JSONUtil.toJsonStr(node))
                .build();

        RegisterResponse response = nameNodeStub.register(request);
        log.info("接收到 NameNode 返回的注册响应：{}", response.getStatus());
        return response;
    }

    public HeartbeatResponse heartbeat() {
        log.info("发送请求到 NameNode 进行心跳");
        HeartbeatRequest request = HeartbeatRequest.newBuilder()
                .setDatanodeInfo(JSONUtil.toJsonStr(node))
                .build();
        HeartbeatResponse response = nameNodeStub.heartbeat(request);
        log.info("接收到 NameNode 返回的心跳响应：{}", response.getStatus());
        return response;
    }

    @Override
    public ReportStorageInfoResponse reportStorageInfo(StorageInfo storageInfo) {
        log.info("发送请求到 NameNode 上报文件存储信息，storageInfo={}", JSONUtil.toJsonStr(storageInfo));
        ReportStorageInfoRequest request = ReportStorageInfoRequest.newBuilder()
                .setDatanodeInfo(JSONUtil.toJsonStr(node))
                .setStorageInfo(JSONUtil.toJsonStr(storageInfo))
                .build();
        ReportStorageInfoResponse response = nameNodeStub.reportStorageInfo(request);
        log.info("接收到 NameNode 上报文件存储信息响应：{}", response.getStatus());
        return response;
    }

    @Override
    public ReportFileInfoResponse reportFileInfo(FileInfo fileInfo) {
        ReportFileInfoRequest request = ReportFileInfoRequest.newBuilder()
                .setDatanodeInfo(JSONUtil.toJsonStr(node))
                .setFileInfo(JSONUtil.toJsonStr(fileInfo))
                .build();
        ReportFileInfoResponse response = nameNodeStub.reportFileInfo(request);
        log.info("接收到 NameNode 增量上报收到的文件副本响应：{}", response.getStatus());
        return response;
    }

}
