package com.westboy.backupnode.server;

import com.westboy.backupnode.config.BackupNodeConfig;

/**
 * 负责同步 editlog 的进程
 *
 * @author pengbo
 * @since 2021/2/1
 */
public class BackupNode {

    private volatile boolean isRunning = true;

    private final BackupNodeConfig config;
    private final FSNameSystem nameSystem;
    private final NameNodeRpcClient nameNodeRpcClient;
    private final EditLogFetcher editLogFetcher;
    private final FSImageCheckpointer fsImageCheckpointer;

    public BackupNode() {
        config = new BackupNodeConfig();
        nameSystem = new FSNameSystemImpl(config);
        nameSystem.recover();

        nameNodeRpcClient = new NameNodeRpcClientImpl(config);
        editLogFetcher = new EditLogFetcher(config, this, nameSystem, nameNodeRpcClient);
        fsImageCheckpointer = new FSImageCheckpointer(config, this, nameSystem, nameNodeRpcClient);
    }

    public static void main(String[] args) {
        BackupNode backupNode = new BackupNode();
        backupNode.start();
    }


    private void start() {
        editLogFetcher.start();
        fsImageCheckpointer.start();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
