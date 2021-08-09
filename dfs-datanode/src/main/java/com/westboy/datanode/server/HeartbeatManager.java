package com.westboy.datanode.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.westboy.common.Command;
import com.westboy.common.RemoveReplicaTask;
import com.westboy.common.ReplicateReplicaTask;
import com.westboy.common.entity.StorageInfo;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.datanode.rpc.NameNodeRpcClient;
import com.westboy.namenode.rpc.model.HeartbeatResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.westboy.common.Command.REMOVE_REPLICA;
import static com.westboy.common.Command.REPLICATE_REPLICA;
import static com.westboy.common.constant.NameNodeResponseStatus.STATUS_FAILURE;
import static com.westboy.common.constant.NameNodeResponseStatus.STATUS_SUCCESS;

@Slf4j
public class HeartbeatManager {

    private final NameNodeRpcClient nameNodeRpcClient;
    private final StorageManager storageManager;
    private final ReplicateManager replicateManager;

    private final long heartbeatInterval;
    private final String dataDir;

    public HeartbeatManager(DataNodeConfig config, NameNodeRpcClient nameNodeRpcClient, StorageManager storageManager, ReplicateManager replicateManager) {
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.storageManager = storageManager;
        this.replicateManager = replicateManager;

        this.heartbeatInterval = config.getHeartbeatInterval();
        this.dataDir = config.getDataDir();
    }

    public void start() {
        HeartbeatThread thread = new HeartbeatThread();
        thread.setName("heartbeat");
        thread.start();
    }

    class HeartbeatThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(heartbeatInterval);

                    HeartbeatResponse response = nameNodeRpcClient.heartbeat();

                    if (STATUS_SUCCESS.equals(response.getStatus())) {
                        List<Command> commands = JSONUtil.toList(JSONUtil.parseArray(response.getCommands()), Command.class);
                        for (Command command : commands) {
                            // 复制副本
                            if (REPLICATE_REPLICA.equals(command.getType())) {
                                List<ReplicateReplicaTask> replicaTasks = JSONUtil.toList(JSONUtil.parseArray(command.getContent()), ReplicateReplicaTask.class);
                                replicateManager.addReplicateTasks(replicaTasks);
                                // 移除副本
                            } else if (REMOVE_REPLICA.equals(command.getType())) {
                                List<RemoveReplicaTask> replicaTasks = JSONUtil.toList(JSONUtil.parseArray(command.getContent()), RemoveReplicaTask.class);
                                for (RemoveReplicaTask task : replicaTasks) {
                                    FileUtil.del(dataDir + task.getFilename());
                                }
                            }
                        }
                    }

                    if (STATUS_FAILURE.equals(response.getStatus())) {
                        log.info("重新注册");
                        nameNodeRpcClient.register();

                        StorageInfo storageInfo = storageManager.getStorageInfo();
                        if (storageInfo.getStoredDataSize() > 0L) {
                            log.info("重新上报全量存储信息");
                            nameNodeRpcClient.reportStorageInfo(storageInfo);
                        }
                    }
                } catch (Exception e) {
                    log.error("心跳失败", e);
                }
            }
        }
    }
}
