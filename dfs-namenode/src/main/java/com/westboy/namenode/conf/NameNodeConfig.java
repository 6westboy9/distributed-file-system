package com.westboy.namenode.conf;

import lombok.Getter;

import static cn.hutool.core.util.CharUtil.DASHED;
import static cn.hutool.core.util.CharUtil.SLASH;

/**
 * 配置类
 *
 * @author pengbo
 * @since 2021/2/1
 */
@Getter
public class NameNodeConfig {

    public final String serverIp = "127.0.0.1";
    public final Integer rpcServerPort = 50070;
    public final Integer uploaderServerPort = 9000;
    public final Integer uploaderServerBackLog = 100;

    private final long editlogCleanerInterval = 60 * 1000;

    /**
     * 单块 editlog 缓冲区的最大大小，默认 25 字节
     */
    private final long editlogBufferLimit = 2 * 1024;
    private final int fetchSize = 10;

    // editlog 文件命名模板 edits-1-134.log、edits-135-166.log
    public final String editLogFileSuffix = ".log";
    public final String editLogPrefix = "edits";

    public final String editLogPath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/namenode";
    // public final String editLogPath = "/root/dfs/data/namenode";

    // %s-%s
    public final String editLogRange = "%s-%s";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/data/namenode/edits-%s-%s.log
    public final String editLogTemplate = editLogPath + SLASH + editLogPrefix + DASHED + editLogRange + editLogFileSuffix;

    public final String fsimageFileName = "fsimage.meta";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/data/namenode/fsimage.meta
    public final String fsimageFilePath = editLogPath + SLASH + fsimageFileName;

    public final String fsimageTemplate = "fsimage-%s.meta";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/data/namenode/fsimage-%s.meta
    public final String fsimageTemplatePath = editLogPath + SLASH + fsimageTemplate;

    public final String checkPointTxidFileName = "checkpoint-txid.meta";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/data/namenode/checkpoint-txid.meta
    public final String checkPointTxidFilePath = editLogPath + SLASH + checkPointTxidFileName;

    // 当客户端请求节点时分配 DataNode 副本数量
    private final int dataNodeReplicas = 2;
    // 与 DataNode 心跳检测时间间隔
    private final long heartbeatCheckInterval = 30 * 1000L;
    // 与 DataNode 心跳超时时间
    private final long heartbeatTimeout = 90 * 1000L;
    // 负载均衡后，需要删除文件副本的节点进行删除操作的延迟时间
    private final long delayRemoveReplicaTime = 30 * 1000L;

}
