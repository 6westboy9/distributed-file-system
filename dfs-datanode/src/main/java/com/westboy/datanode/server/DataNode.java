package com.westboy.datanode.server;

import com.westboy.common.constant.NameNodeResponseStatus;
import com.westboy.common.entity.StorageInfo;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.datanode.network.NioServer;
import com.westboy.datanode.network.newversion.NewNioServer;
import com.westboy.datanode.network.oldversion.OldNioServer;
import com.westboy.datanode.rpc.NameNodeRpcClient;
import com.westboy.datanode.rpc.NameNodeRpcClientImpl;
import com.westboy.namenode.rpc.model.RegisterResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * DataNode 启动类
 *
 * @author pengbo
 * @since 2021/1/28
 */
@Slf4j
public class DataNode {

    private volatile boolean shouldRun;
    private final DataNodeConfig config;
    private final StorageManager storageManager;
    private final ReplicateManager replicateManager;
    private final HeartbeatManager heartbeatManager;
    private final NioServer nioServer;
    private final NameNodeRpcClient nameNodeRpcClient;

    public DataNode() {
        shouldRun = true;
        config = new DataNodeConfig();
        nameNodeRpcClient = new NameNodeRpcClientImpl(config);
        RegisterResponse response = nameNodeRpcClient.register();

        storageManager = new StorageManager(config);
        if (NameNodeResponseStatus.STATUS_SUCCESS.equals(response.getStatus())) {
            log.info("注册成功，需要全量上报本地存储信息");
            StorageInfo storageInfo = storageManager.getStorageInfo();
            if (storageInfo.getStoredDataSize() > 0L) {
                log.info("重新上报全量存储信息");
                nameNodeRpcClient.reportStorageInfo(storageInfo);
            }
        } else {
            log.warn("注册重连，不需要全量上报本地存储信息");
        }

        replicateManager = new ReplicateManager(config, nameNodeRpcClient);
        replicateManager.start();

        heartbeatManager = new HeartbeatManager(config, nameNodeRpcClient, storageManager, replicateManager);
        heartbeatManager.start();

        // nioServer = new OldNioServer(config, nameNodeRpcClient);
        nioServer = new NewNioServer(config, nameNodeRpcClient);
        nioServer.start();
    }

    public static void main(String[] args) {
        DataNode dataNode = new DataNode();
        dataNode.run();
    }

    private void run() {
        try {
            while (shouldRun) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
