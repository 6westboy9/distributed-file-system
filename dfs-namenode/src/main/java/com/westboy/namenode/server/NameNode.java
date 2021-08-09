package com.westboy.namenode.server;

import com.westboy.namenode.conf.NameNodeConfig;
import com.westboy.namenode.rpcserver.NameNodeRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * NameNode 启动类
 * <p>
 * 职责：负责分布式文件存储系统的元数据管理，比如文件目录树、权限的设置、副本数的设置等等。
 *
 * @author pengbo
 * @since 2021/1/28
 */
@Slf4j
public class NameNode {

    private final NameNodeConfig config;
    /**
     * 负责管理元数据的核心组件
     */
    private final FSNameSystem nameSystem;
    /**
     * 负责管理集群中所有 DataNode 的组件
     */
    private final DataNodeManager dataNodeManager;
    /**
     * NameNode 对外提供 RPC 接口的 Server，可以响应请求
     */
    private final NameNodeRpcServer rpcServer;
    /**
     * 接收 BackupNode 上传的 fsimage 文件的服务器
     */
    private final FSImageUploadServer fsImageUploadServer;

    private final Object lock = new Object();
    private volatile boolean terminated = false;

    public NameNode() {
        config = new NameNodeConfig();
        dataNodeManager = new DataNodeManager(config);
        nameSystem = new FSNameSystemImpl(config, dataNodeManager);
        dataNodeManager.setNameSystem(nameSystem);

        nameSystem.start();

        rpcServer = new NameNodeRpcServer(config, nameSystem, dataNodeManager);
        fsImageUploadServer = new FSImageUploadServer(config);

        dataNodeManager.start();
    }

    public static void main(String[] args) {
        try {
            log.info("NameNode 开始启动");
            NameNode nameNode = new NameNode();
            nameNode.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("正在执行关闭 NameNode 钩子函数");
                nameNode.stop();
                log.info("NameNode 已关闭");
            }, "namenode-shutdown-hook"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start() throws Exception {
        fsImageUploadServer.start();
        rpcServer.start();
    }

    private void stop() {
        try {
            fsImageUploadServer.shutdown();
            rpcServer.shutdown();
            nameSystem.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
