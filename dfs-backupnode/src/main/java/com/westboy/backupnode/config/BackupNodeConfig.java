package com.westboy.backupnode.config;

import cn.hutool.core.io.FileUtil;
import lombok.Getter;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Pattern;

import static cn.hutool.core.util.CharUtil.*;

/**
 * 配置类
 *
 * @author pengbo
 * @since 2021/2/1
 */
@Getter
public class BackupNodeConfig {

    /**
     * NameNode RPC 通信服务配置信息（BackupNode 作为客户端）
     */
    public final String namenodeServerIp = "127.0.0.1";
    public final Integer namenodeServerPort = 50070;
    /**
     * NameNode Image 上传服务配置信息（BackupNode 作为客户端，进行定时上传操作）
     */
    public final Integer namenodeUploaderServerPort = 9000;
    /**
     * 从 BackupNode 生成 image 镜像间隔
     */
    public final long checkpointInterval = 30 * 1000L;
    /**
     * 当 NameNode 没有新的 editlog 数据时，等待拉取的时间间隔
     */
    private final long fetchInterval = 10 * 1000L;

    public final String dataDir = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/backupnode";
    // public final String dataDir = "/root/dfs/data/backupnode";

    // editlog 文件命名模板 edits-1-134.log、edits-135-166.log
    public final String editLogFileSuffix = ".log";
    public final String editLogPrefix = "edits";
    // %s-%s
    public final String editLogRange = "%s-%s";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/editslog/edits-%s-%s.log
    public final String editLogTemplate = dataDir + SLASH + editLogPrefix + DASHED + editLogRange + editLogFileSuffix;

    public final String fsimageFileName = "fsimage.meta";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/editslog/fsimage.meta
    public final String fsimageFilePath = dataDir + SLASH + fsimageFileName;

    public final String fsimageTemplate = "fsimage-%s.meta";
    public final String fsimageTemplatePattern = "^fsimage-\\d+.meta";
    // /Users/westboy/IdeaProjects/shishan/distributed-file-system/editslog/fsimage-%s.meta
    public final String fsimageTemplatePath = dataDir + SLASH + fsimageTemplate;

    // public final String checkPointTxidFileName = "checkpoint-txid.meta";
    // // /Users/westboy/IdeaProjects/shishan/distributed-file-system/editslog/checkpoint-txid.meta
    // public final String checkPointTxidFilePath = editLogPath + SLASH + checkPointTxidFileName;

    public final String checkPointInfoFileName = "checkpoint-info.meta";
    public final String checkPointInfoFilePath = dataDir + SLASH + checkPointInfoFileName;

    public static void main(String[] args) {
        BackupNodeConfig config = new BackupNodeConfig();
        List<File> files = FileUtil.loopFiles(config.getDataDir(), pathname -> {
            String name = pathname.getName();
            System.out.println(name);
            return name.matches(config.getFsimageTemplatePattern()) && !name.equals("fsimage-300.meta");
        });

        System.out.println(files);
    }

}
