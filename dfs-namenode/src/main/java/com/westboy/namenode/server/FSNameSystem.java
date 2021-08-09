package com.westboy.namenode.server;

import com.westboy.common.entity.FileInfo;

import java.util.List;

public interface FSNameSystem {

    void start();

    boolean mkdir(String path);

    boolean create(String filepath);

    void flush();

    void recover();

    FSEditlog getFsEditlog();

    void setSyncedTxid(long syncedTxid, boolean recover, boolean flush);

    long getSyncedTxid();

    void addReceivedReplica(DataNodeInfo dataNode, FileInfo fileInfo);

    /**
     * 查找当前文件副本所在的有效 DataNode 节点
     *
     * @param excludedNodeId 排除节点
     */
    DataNodeInfo chooseDataNode(String filename, String excludedNodeId);

    /**
     * 移除该节点，同时移除该节点上的所有副本文件
     */
    void removeDataNode(String nodeId);

    /**
     * 移除该节点上的文件副本
     */
    void removeTheFileOnTheDataNode(String nodeId, FileInfo file);

    List<FileInfo> getAllFilesOnTheDatanode(String nodeId);

}
