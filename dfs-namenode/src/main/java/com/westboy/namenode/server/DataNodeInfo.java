package com.westboy.namenode.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.RemoveReplicaTask;
import com.westboy.common.ReplicateReplicaTask;
import com.westboy.common.entity.Node;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 用来描述 DataNode 的信息
 *
 * @author pengbo
 * @since 2021/1/30
 */

public class DataNodeInfo extends Node implements Comparable<DataNodeInfo> {

    private long latestHeartbeatTime;
    private long storedDataSize;
    /**
     * 副本复制任务队列
     */
    private final ConcurrentLinkedQueue<ReplicateReplicaTask> replicateReplicaTaskQueue;
    /**
     * 副本移除任务队列
     */
    private final ConcurrentLinkedQueue<RemoveReplicaTask> removeReplicaTaskQueue;

    public DataNodeInfo(String ip, String hostname, int port) {
        super(ip, hostname, port);
        this.latestHeartbeatTime = System.currentTimeMillis();
        this.storedDataSize = 0L;
        this.replicateReplicaTaskQueue = new ConcurrentLinkedQueue<>();
        this.removeReplicaTaskQueue = new ConcurrentLinkedQueue<>();
    }

    public long getLatestHeartbeatTime() {
        return latestHeartbeatTime;
    }

    public void setLatestHeartbeatTime(long latestHeartbeatTime) {
        this.latestHeartbeatTime = latestHeartbeatTime;
    }

    public long getStoredDataSize() {
        return storedDataSize;
    }

    public void setStoredDataSize(long storedDataSize) {
        this.storedDataSize = storedDataSize;
    }

    @Override
    public int compareTo(DataNodeInfo o) {
        if (this.storedDataSize - o.getStoredDataSize() > 0) {
            return 1;
        } else if (this.storedDataSize - o.getStoredDataSize() < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public void addStoredDataSize(long fileSize) {
        storedDataSize += fileSize;
    }

    public void addReplicateTask(ReplicateReplicaTask replicateReplicaTask) {
        replicateReplicaTaskQueue.add(replicateReplicaTask);
    }

    public List<ReplicateReplicaTask> listReplicateTasks() {
        ReplicateReplicaTask replicateReplicaTask;
        List<ReplicateReplicaTask> replicateReplicaTasks = CollUtil.newArrayList();
        while (ObjectUtil.isNotNull((replicateReplicaTask = replicateReplicaTaskQueue.poll()))) {
            replicateReplicaTasks.add(replicateReplicaTask);
        }
        return replicateReplicaTasks;
    }

    public void addRemoveTask(RemoveReplicaTask replicateReplicaTask) {
        removeReplicaTaskQueue.add(replicateReplicaTask);
    }

    public List<RemoveReplicaTask> listRemoveTasks() {
        RemoveReplicaTask replicateReplicaTask;
        List<RemoveReplicaTask> replicateReplicaTasks = CollUtil.newArrayList();
        while (ObjectUtil.isNotNull((replicateReplicaTask = removeReplicaTaskQueue.poll()))) {
            replicateReplicaTasks.add(replicateReplicaTask);
        }
        return replicateReplicaTasks;
    }
}
