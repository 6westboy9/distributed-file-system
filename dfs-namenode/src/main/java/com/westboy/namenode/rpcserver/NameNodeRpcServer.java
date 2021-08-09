package com.westboy.namenode.rpcserver;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.namenode.conf.NameNodeConfig;
import com.westboy.namenode.server.DataNodeManager;
import com.westboy.namenode.server.FSNameSystem;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NameNode 的 RPC 服务的接口
 *
 * @author pengbo
 * @since 2021/1/30
 */
@Slf4j
public class NameNodeRpcServer {

    private Server rpcServer;
    private final NameNodeServiceImpl nodeService;
    private final ExecutorService executor;

    private final int rpcServerPort;

    public NameNodeRpcServer(NameNodeConfig config, FSNameSystem nameSystem, DataNodeManager dataNodeManager) {
        this.rpcServerPort = config.getRpcServerPort();
        this.nodeService = new NameNodeServiceImpl(config, nameSystem, dataNodeManager);
        this.executor = Executors.newFixedThreadPool(1, ThreadFactoryBuilder.create().setNamePrefix("namenode-rpc-server-").build());
    }

    public void start() throws IOException {
        rpcServer = ServerBuilder
                .forPort(rpcServerPort)
                .executor(executor)
                .addService(nodeService)
                .build()
                .start();

        log.info("启动 NameNodeRpcServer 服务，并监听端口号：{}", rpcServerPort);
    }

    public void shutdown() {
        if (ObjectUtil.isNotNull(rpcServer)) {
            log.info("执行 NameNodeRpcServer 关闭");
            rpcServer.shutdown();
            log.info("NameNodeRpcServer 已关闭");
        }
    }

}
