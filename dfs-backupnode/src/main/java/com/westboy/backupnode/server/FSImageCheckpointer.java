package com.westboy.backupnode.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.westboy.backupnode.config.BackupNodeConfig;
import com.westboy.common.constant.GlobalConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 关于 fsimage 文件的 checkpoint 组件
 *
 * @author pengbo
 * @since 2021/2/4
 */
@Slf4j
public class FSImageCheckpointer extends Thread {

    private final BackupNode backupNode;
    private final FSNameSystem nameSystem;
    private final NameNodeRpcClient nameNodeRpcClient;

    private String lastFSImageFile;
    private long lastCheckpointTime;
    private final AtomicInteger counter = new AtomicInteger(0);

    private final long checkpointInterval;
    private final String dataDir;

    private final String fsimageMetaTemplate;
    private final String fsimageMetaTemplatePath;
    private final String fsimageTemplatePattern;
    private final String checkPointInfoFilePath;

    private final String namenodeIp;
    private final int uploaderPort;


    public FSImageCheckpointer(BackupNodeConfig config, BackupNode backupNode, FSNameSystem nameSystem, NameNodeRpcClient nameNodeRpcClient) {
        super("fsimage-checkpointer");
        this.backupNode = backupNode;
        this.nameSystem = nameSystem;
        this.nameNodeRpcClient = nameNodeRpcClient;

        this.checkpointInterval = config.getCheckpointInterval();
        this.dataDir = config.getDataDir();
        this.fsimageMetaTemplate = config.getFsimageTemplate();
        this.fsimageMetaTemplatePath = config.getFsimageTemplatePath();
        this.fsimageTemplatePattern = config.getFsimageTemplatePattern();
        this.checkPointInfoFilePath = config.getCheckPointInfoFilePath();
        this.namenodeIp = config.getNamenodeServerIp();
        this.uploaderPort = config.getNamenodeUploaderServerPort();
    }

    @Override
    public void run() {
        log.info("启动 FSImageCheckpointer 线程");
        while (backupNode.isRunning()) {
            try {
                if (!nameSystem.isFinishedRecover()) {
                    log.info("当前还没有完成元数据恢复, 延迟进行 checkpoint 操作");
                    Thread.sleep(1000);
                    continue;
                }

                if (!nameNodeRpcClient.isNameNodeRunning()) {
                    log.info("NameNode 当前无法访问, 无法执行 checkpoint 操作, 休息 10s 后再次尝试.");
                    Thread.sleep(10 * 1000);
                    continue;
                }

                long interval = System.currentTimeMillis() - lastCheckpointTime;
                if (lastCheckpointTime != 0 && interval < checkpointInterval) {
                    Thread.sleep(checkpointInterval - interval);
                    continue;
                }

                log.info("执行第 {} 次 checkpoint 操作", counter.incrementAndGet());
                doCheckpoint();
                lastCheckpointTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void doCheckpoint() {
        // 校验当前上传的内容与上次一样的话就不需要再进行上传操作了
        FSImage fsimage = nameSystem.getFSImage(true);
        if (ObjectUtil.isNull(fsimage)) {
            log.debug("当前文件目录树为空，不需要执行 checkpoint 操作");
            return;
        }

        log.info("当前文件目录树不为空，需要执行 checkpoint 操作");

        try {
            writeLocalFSImageFile(fsimage);
            uploadRemoteFSImageFile(fsimage);
            writeLocalCheckpointTxid(fsimage.getMaxTxid());
            uploadRemoteCheckpointTxid(fsimage.getMaxTxid());
            nameSystem.setSyncedTxid(fsimage.getMaxTxid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeLocalFSImageFile(FSImage fsimage) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(fsimage.getFsimageJson().getBytes());
        String newFsimageName = String.format(fsimageMetaTemplate, fsimage.getMaxTxid());
        String newFsimageFile = String.format(fsimageMetaTemplatePath, fsimage.getMaxTxid());

        RandomAccessFile newFile = new RandomAccessFile(newFsimageFile, GlobalConstant.RW_FILE_MODE);
        FileOutputStream out = new FileOutputStream(newFile.getFD());
        FileChannel channel = out.getChannel();

        log.info("创建新的 fsimage 文件并写入：{}", newFsimageFile);

        channel.write(buffer);
        channel.force(false);

        // 延迟删除，并发重启后，立即删除，等待第一次 checkpoint 才进行删除
        List<File> files = FileUtil.loopFiles(dataDir, pathname -> {
            String name = pathname.getName();
            return name.matches(fsimageTemplatePattern) && !name.equals(newFsimageName);
        });
        files.forEach(file -> {
            FileUtil.del(file);
            log.info("删除旧的 fsimage 文件：{}", file.getName());
        });

        lastFSImageFile = newFsimageName;
    }

    private void uploadRemoteFSImageFile(FSImage fsimage) {
        FSImageUploader fsimageUploader = new FSImageUploader(fsimage, namenodeIp, uploaderPort);
        fsimageUploader.start();
        log.info("开启上传 fsimage 文件异步调度任务");
    }

    private void writeLocalCheckpointTxid(long maxTxid) throws IOException {
        long now = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.wrap((String.valueOf(now) + CharUtil.UNDERLINE + maxTxid + CharUtil.UNDERLINE + lastFSImageFile).getBytes());

        // checkpoint-info.meta
        RandomAccessFile file = new RandomAccessFile(checkPointInfoFilePath, GlobalConstant.RW_FILE_MODE);
        FileOutputStream out = new FileOutputStream(file.getFD());
        FileChannel channel = out.getChannel();

        // 底层覆盖写
        channel.position(0);
        channel.write(buffer);
        channel.force(false);
        log.info("保存最新的 checkpoint txid 至 {}", checkPointInfoFilePath);
    }


    private void uploadRemoteCheckpointTxid(long txid) {
        nameNodeRpcClient.updateCheckpointTxid(txid);
    }

}
