package com.westboy.backupnode.server;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.westboy.backupnode.config.BackupNodeConfig;
import com.westboy.namenode.rpc.model.*;
import com.westboy.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author pengbo
 * @since 2021/2/1
 */
@Slf4j
public class NameNodeRpcClientImpl implements NameNodeRpcClient {

    private boolean isRunning = true;

    private final String namenodeIp;
    private final int namenodePort;
    private final NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNodeStub;
    private final ExecutorService executor;


    public NameNodeRpcClientImpl(BackupNodeConfig config) {
        this.namenodeIp = config.getNamenodeServerIp();
        this.namenodePort = config.getNamenodeServerPort();
        executor = Executors.newFixedThreadPool(1, ThreadFactoryBuilder.create().setNamePrefix("namenode-rpc-client-").build());

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(namenodeIp, namenodePort)
                .executor(executor)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.nameNodeStub = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public FetchEditlogResponse fetchEditsLog(long syncTxid) {
        FetchEditlogRequest request = FetchEditlogRequest.newBuilder()
                .setSyncTxid(syncTxid)
                .build();
        log.info("BackNode 发送的拉取请求，需要同步的起始 syncTxid={}", syncTxid);
        return nameNodeStub.fetchEditlog(request);
    }

    @Override
    public UpdateCheckpointTxidResponse updateCheckpointTxid(long txid) {
        UpdateCheckpointTxidRequest request = UpdateCheckpointTxidRequest.newBuilder()
                .setTxid(txid)
                .build();
        UpdateCheckpointTxidResponse response = nameNodeStub.updateCheckpointTxid(request);
        log.info("上传最新的 checkpoint 的 txid={} 至 NameNode 服务器，响应结果：{}", txid, response.getStatus());
        return response;
    }

    @Override
    public boolean isNameNodeRunning() {
        return isRunning;
    }

    @Override
    public void setNameNodeRunning(boolean running) {
        isRunning = running;
    }
}
