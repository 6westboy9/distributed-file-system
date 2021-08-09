package com.westboy.namenode.server;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.RemoveReplicaTask;
import com.westboy.common.entity.Node;
import com.westboy.common.ReplicateReplicaTask;
import com.westboy.common.entity.FileInfo;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toList;

/**
 * 负责管理集群中的所有 DataNode 组件
 *
 * @author pengbo
 * @since 2021/1/30
 */
@Slf4j
public class DataNodeManager {

    private final int dataNodeReplicas;
    private final long heartbeatCheckInterval;
    private final long heartbeatTimeout;
    private final long delayRemoveReplicaTime;

    private FSNameSystem nameSystem;

    /**
     * 内存中维护的 DataNode 集合
     */
    private final Map<String /* ip-hostname-port */, DataNodeInfo> dataNodeMap = new ConcurrentHashMap<>();

    public DataNodeManager(NameNodeConfig config) {
        dataNodeReplicas = config.getDataNodeReplicas();
        heartbeatCheckInterval = config.getHeartbeatCheckInterval();
        heartbeatTimeout = config.getHeartbeatTimeout();
        delayRemoveReplicaTime = config.getDelayRemoveReplicaTime();
    }

    public void setNameSystem(FSNameSystem nameSystem) {
        this.nameSystem = nameSystem;
    }

    public void start() {
        DataNodeAliveMonitor monitor = new DataNodeAliveMonitor();
        monitor.setName("datanode-alive-monitor");
        monitor.start();
    }

    /**
     * 注册
     */
    public boolean register(Node node) {
        if (dataNodeMap.containsKey(node.getId())) {
            log.info("DataNode 重复注册：nodeInfo={}", node);
            return false;
        }

        DataNodeInfo dataNode = BeanUtil.copyProperties(node, DataNodeInfo.class);
        dataNodeMap.put(dataNode.getId(), dataNode);
        log.info("DataNode 注册成功：nodeInfo={}", node);
        return true;
    }

    public boolean heartbeat(Node node) {
        DataNodeInfo dataNodeInfo = dataNodeMap.get(node.getId());

        if (ObjectUtil.isNull(dataNodeInfo)) {
            log.info("DataNode 心跳失败，需要重新注册");
            return false;
        }

        dataNodeInfo.setLatestHeartbeatTime(System.currentTimeMillis());
        log.info("DataNode 发送心跳：nodeInfo={}", node);
        return true;
    }

    public List<DataNodeInfo> allocateDataNodes(FileInfo fileInfo) {
        synchronized (this) {
            long fileSize = fileInfo.getFileLength();
            List<DataNodeInfo> datanodeList = new ArrayList<>(dataNodeMap.values());
            Collections.sort(datanodeList);
            List<DataNodeInfo> selectedDataNodes = new ArrayList<>();
            if (CollUtil.isNotEmpty(datanodeList)) {
                for (int i = 0; i < Math.min(dataNodeReplicas, datanodeList.size()); i++) {
                    DataNodeInfo dataNodeInfo = datanodeList.get(i);
                    selectedDataNodes.add(dataNodeInfo);
                    dataNodeInfo.addStoredDataSize(fileSize);
                }
            }
            return selectedDataNodes;
        }
    }

    public DataNodeInfo reallocateDataNode(FileInfo fileInfo, Node excludedNode) {
        synchronized (this) {
            // 先得把排除掉的那个数据节点的存储的数据量减少文件的大小
            long fileSize = fileInfo.getFileLength();
            DataNodeInfo excludedDataNode = dataNodeMap.get(excludedNode.getId());
            excludedDataNode.addStoredDataSize(-fileSize);

            // 取出来所有的 DataNode，并且按照已经存储的数据大小来排序
            List<DataNodeInfo> datanodeList = new ArrayList<>(dataNodeMap.values());
            datanodeList = datanodeList.stream()
                    .filter(dataNodeInfo -> !excludedDataNode.equals(dataNodeInfo))
                    .collect(toList());

            DataNodeInfo selectedDatanode = null;
            if (CollUtil.isNotEmpty(datanodeList)) {
                Collections.sort(datanodeList);
                selectedDatanode = datanodeList.get(0);
                datanodeList.get(0).addStoredDataSize(fileSize);
            }
            return selectedDatanode;
        }
    }


    public void setStoredDataSize(Node node, Long storedDataSize) {
        DataNodeInfo dataNodeInfo = getDataNode(node.getId());
        dataNodeInfo.setStoredDataSize(storedDataSize);
    }

    public DataNodeInfo getDataNode(String nodeId) {
        return dataNodeMap.get(nodeId);
    }

    public void createRebalanceTasks() {
        synchronized (this) {
            // 计算集群节点存储数据的平均值
            long totalStoredDataSize = 0;
            for (DataNodeInfo datanode : dataNodeMap.values()) {
                totalStoredDataSize += datanode.getStoredDataSize();
            }
            long averageStoredDataSize = totalStoredDataSize / dataNodeMap.size();

            // 将集群中的节点区分为两类：迁出节点和迁入节点
            List<DataNodeInfo> sourceNode = new ArrayList<>();
            List<DataNodeInfo> destNode = new ArrayList<>();

            for (DataNodeInfo datanode : dataNodeMap.values()) {
                if (datanode.getStoredDataSize() > averageStoredDataSize) {
                    sourceNode.add(datanode);
                }
                if (datanode.getStoredDataSize() < averageStoredDataSize) {
                    destNode.add(datanode);
                }
            }

            // 为迁入节点生成复制的任务，为迁出节点生成删除的任务
            // 在这里生成的删除任务统一放到 24 小时之后延迟调度执行，咱们可以实现一个延迟调度执行的线程
            List<RemoveReplicaTask> removeReplicaTasks = new ArrayList<>();
            for (DataNodeInfo sourceDatanode : sourceNode) {
                long toRemoveDataSize = sourceDatanode.getStoredDataSize() - averageStoredDataSize;

                for (DataNodeInfo destDatanode : destNode) {
                    // 直接一次性放到一台机器就可以了
                    if (destDatanode.getStoredDataSize() + toRemoveDataSize <= averageStoredDataSize) {
                        createRebalanceTasks(sourceDatanode, destDatanode, removeReplicaTasks, toRemoveDataSize);
                        break;
                    } else if (destDatanode.getStoredDataSize() < averageStoredDataSize) {
                        long maxRemoveDataSize = averageStoredDataSize - destDatanode.getStoredDataSize();
                        long removedDataSize = createRebalanceTasks(sourceDatanode, destDatanode, removeReplicaTasks, maxRemoveDataSize);
                        toRemoveDataSize -= removedDataSize;
                    }
                }
            }

            // 注意这里的复制是异步执行，而删除并没有添加到 DataNode 的队列中去，后续延迟进行删除
            if (CollUtil.isNotEmpty(removeReplicaTasks)) {
                CountDownLatch latch = new CountDownLatch(1);
                ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix("delay-remove-replica-").build();
                ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, factory);
                executorService.schedule(new DelayRemoveReplicaThread(latch,this, removeReplicaTasks), delayRemoveReplicaTime, TimeUnit.SECONDS);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    executorService.shutdown();
                }
            }
        }
    }

    private long createRebalanceTasks(DataNodeInfo sourceDatanode, DataNodeInfo destDatanode,
                                      List<RemoveReplicaTask> removeReplicaTasks, long maxRemoveDataSize) {
        List<FileInfo> files = nameSystem.getAllFilesOnTheDatanode(sourceDatanode.getId());

        // 遍历文件，不停的为每个文件生成一个复制的任务，直到准备迁移的文件的大小
        // 超过了待迁移总数据量的大小为止
        long removedDataSize = 0;

        for (FileInfo file : files) {
            String filename = file.getFilename();
            long fileSize = file.getFileLength();

            if (removedDataSize + fileSize >= maxRemoveDataSize) {
                break;
            }

            // 注意这里的复制是异步执行，而删除并没有添加到 DataNode 的队列中去，后续延迟进行删除

            // 为这个文件生成复制任务
            ReplicateReplicaTask replicateTask = new ReplicateReplicaTask(file, sourceDatanode, destDatanode);
            destDatanode.addReplicateTask(replicateTask);
            destDatanode.addStoredDataSize(fileSize);

            // 为这个文件生成删除任务
            sourceDatanode.addStoredDataSize(-fileSize);
            nameSystem.removeTheFileOnTheDataNode(sourceDatanode.getId(), file);
            RemoveReplicaTask removeReplicaTask = new RemoveReplicaTask(filename, sourceDatanode);
            removeReplicaTasks.add(removeReplicaTask);

            removedDataSize += fileSize;
        }

        return removedDataSize;
    }


    /**
     * DataNode 心跳检测
     */
    class DataNodeAliveMonitor extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    List<DataNodeInfo> deadDataNodes = new ArrayList<>();
                    for (DataNodeInfo nodeInfo : dataNodeMap.values()) {
                        // 在 90 秒内没有收到心跳，就认为 DataNode 已经断开连接
                        if (System.currentTimeMillis() - nodeInfo.getLatestHeartbeatTime() > heartbeatTimeout) {
                            log.warn("没有收到来自 {} 的心跳，移除注册信息", nodeInfo.getHostname());
                            deadDataNodes.add(nodeInfo);
                        }
                    }

                    // 在 DataNode 移除之后，需要将本地维护的 DataNode 上的文件副本转移至其它可用 DataNode 节点上
                    if (CollUtil.isNotEmpty(deadDataNodes)) {
                        for (DataNodeInfo deadDataNode : deadDataNodes) {
                            createLostReplicaTask(deadDataNode);
                            dataNodeMap.remove(deadDataNode.getId());
                            nameSystem.removeDataNode(deadDataNode.getId());
                        }
                    }

                    Thread.sleep(heartbeatCheckInterval);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createLostReplicaTask(DataNodeInfo deadDataNode) {
            // 获取该节点上的所有文件副本
            List<FileInfo> files = nameSystem.getAllFilesOnTheDatanode(deadDataNode.getId());
            for (FileInfo fileInfo : files) {
                // 查找移至合适目标的 DataNode 节点
                List<DataNodeInfo> nodeInfos = allocateDataNodes(fileInfo);
                if (CollUtil.isNotEmpty(nodeInfos)) {
                    DataNodeInfo destNode = nodeInfos.get(0);
                    // 查找当前被移除 DataNode 的文件副本所在的其他有效 DataNode 节点
                    DataNodeInfo sourceNode = nameSystem.chooseDataNode(fileInfo.getFilename(), deadDataNode.getId());

                    Node source = BeanUtil.copyProperties(sourceNode, Node.class);
                    Node dest = BeanUtil.copyProperties(destNode, Node.class);

                    ReplicateReplicaTask replicateReplicaTask = new ReplicateReplicaTask(fileInfo, source, dest);
                    // 采用的是目标节点拉取源节点数据模式
                    destNode.addReplicateTask(replicateReplicaTask);
                }
            }
        }
    }

    /**
     * 延迟移除副本线程
     */
    static class DelayRemoveReplicaThread implements Runnable {
        private final CountDownLatch latch;
        private final DataNodeManager dataNodeManager;
        private final List<RemoveReplicaTask> removeReplicaTasks;

        public DelayRemoveReplicaThread(CountDownLatch latch, DataNodeManager dataNodeManager, List<RemoveReplicaTask> removeReplicaTasks) {
            this.latch = latch;
            this.dataNodeManager = dataNodeManager;
            this.removeReplicaTasks = removeReplicaTasks;
        }

        @Override
        public void run() {
            for (RemoveReplicaTask removeReplicaTask : removeReplicaTasks) {
                Node node = removeReplicaTask.getNode();
                DataNodeInfo dataNode = dataNodeManager.getDataNode(node.getId());
                dataNode.addRemoveTask(removeReplicaTask);
            }
            latch.countDown();
        }
    }
}

