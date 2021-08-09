package com.westboy.datanode.server;

import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.entity.Node;
import com.westboy.common.ReplicateReplicaTask;
import com.westboy.common.entity.FileInfo;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.datanode.network.oldversion.OldClient;
import com.westboy.datanode.rpc.NameNodeRpcClient;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReplicateManager {

    private final OldClient oldClient;
    private final NameNodeRpcClient nameNodeRpcClient;
    /**
     * 副本复制任务队列
     */
    private final ConcurrentLinkedQueue<ReplicateReplicaTask> replicateTaskQueue;

    private final String dataDir;
    private final int replicateThreadNum;

    public ReplicateManager(DataNodeConfig config, NameNodeRpcClient nameNodeRpcClient) {
        this.oldClient = new OldClient();
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.replicateTaskQueue = new ConcurrentLinkedQueue<>();

        this.dataDir = config.getDataDir();
        this.replicateThreadNum = config.getReplicateThreadNum();
    }

    public void addReplicateTasks(List<ReplicateReplicaTask> replicateTasks) {
        replicateTasks.forEach(replicateTaskQueue::offer);
    }

    public void start() {
        for (int i = 0; i < replicateThreadNum; i++) {
            new ReplicateWorker().start();
        }
    }

    class ReplicateWorker extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    ReplicateReplicaTask replicaTask = replicateTaskQueue.poll();
                    if (ObjectUtil.isNull(replicaTask)) {
                        Thread.sleep(1000);
                        continue;
                    }

                    FileInfo fileInfo = replicaTask.getFileInfo();
                    String filename = fileInfo.getFilename();

                    Node sourceNode = replicaTask.getSourceNode();
                    String ip = sourceNode.getIp();
                    int port = sourceNode.getPort();

                    byte[] bytes = oldClient.readFile(ip, port, filename);

                    ByteBuffer buffer = ByteBuffer.wrap(bytes);

                    FileOutputStream outputStream = new FileOutputStream(dataDir + filename);
                    FileChannel channel = outputStream.getChannel();
                    channel.write(buffer);

                    nameNodeRpcClient.reportFileInfo(fileInfo);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
