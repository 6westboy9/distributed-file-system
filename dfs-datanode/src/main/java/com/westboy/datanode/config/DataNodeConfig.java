package com.westboy.datanode.config;

import lombok.Getter;

@Getter
public class DataNodeConfig {

    private final int index = 1;
    // /**
    //  * 当前 DataNode 的 IP 和主机名称
    //  */
    private final String ip = "127.0.0.1";
    // private final String ip = "116.85.40.109"; // 外网地址

    private final String hostname = "datanode-" + index;
    private final int port = 9300 + index;
    /**
     * NameNode Server 服务配置信息（DataNode 作为客户端）
     */
    private final String namenodeServerIp = "127.0.0.1";
    private final int namenodeServerPort = 50070;

    private final String dataDir = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/datanode-" + index;
    // private final String dataDir = "/root/dfs/data/datanode-" + index;
    private final long heartbeatInterval = 30 * 1000L;
    private final int queueSize = 3;
    // DataNode 之间副本复制的一个线程数
    private final int replicateThreadNum = 3;
    // 负责与客户端进行网络通信的线程数
    private final int processorThreadNum = 4;
    // 具体负责文件读写的业务线程
    private final int ioThreadNum = 3;
}
