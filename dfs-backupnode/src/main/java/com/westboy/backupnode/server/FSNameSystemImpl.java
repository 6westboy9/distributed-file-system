package com.westboy.backupnode.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.CharUtil;
import cn.hutool.json.JSONUtil;
import com.westboy.backupnode.config.BackupNodeConfig;
import com.westboy.common.constant.GlobalConstant;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 负责管理元数据的核心组件
 *
 * @author pengbo
 * @since 2021/1/28
 */
@Slf4j
public class FSNameSystemImpl implements FSNameSystem {

    private final FSDirectory directory;
    private long syncedTxid;
    private volatile boolean finishedRecover = false;

    private final String fsimageTemplatePath;
    private final String checkPointInfoFilePath;

    public FSNameSystemImpl(BackupNodeConfig config) {
        fsimageTemplatePath = config.getFsimageTemplatePath();
        checkPointInfoFilePath = config.getCheckPointInfoFilePath();

        directory = new FSDirectory();
    }

    /**
     * 创建目录
     */
    @Override
    public boolean mkdir(long txid, String path) {
        directory.mkdir(txid, path);
        return true;
    }

    /**
     * 创建文件
     *
     * @param filepath 文件名称，包含所在的绝对路径，比如 /products/img001.jpg
     */
    @Override
    public boolean create(long txid, String filepath) {
        return directory.create(txid, filepath);
    }

    @Override
    public void setSyncedTxid(long syncedTxid) {
        this.syncedTxid = syncedTxid;
    }

    @Override
    public long getSyncedTxid() {
        return syncedTxid;
    }

    @Override
    public FSImage getFSImage(boolean checkout) {
        if (checkout && syncedTxid >= directory.getSyncedTxid()) {
            return null;
        }
        return directory.getFSImage();
    }

    /**
     * 恢复元数据
     */
    @Override
    public void recover() {
        loadCheckpointTxid();
        loadFSImage();
        finishedRecover = true;
    }

    private void loadFSImage() {
        String fsimageFile = String.format(fsimageTemplatePath, syncedTxid);
        if (!FileUtil.exist(fsimageFile)) {
            log.info("暂无 {} 文件需要恢复", fsimageFile);
            return;
        }

        try (FileInputStream in = new FileInputStream(fsimageFile);
             FileChannel channel = in.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);

            buffer.flip();
            String fsimageJson = new String(buffer.array(), 0, count);

            log.info("恢复 {} 文件中的数据", fsimageFile);
            FSDirectory.INodeDirectory dirTree = JSONUtil.toBean(fsimageJson, new TypeReference<FSDirectory.INodeDirectory>() {}, false);
            directory.setDirTree(dirTree);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCheckpointTxid() {
        if (!FileUtil.exist(checkPointInfoFilePath)) {
            log.info("暂无 {} 文件需要恢复", checkPointInfoFilePath);
            return;
        }

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try (RandomAccessFile raf = new RandomAccessFile(checkPointInfoFilePath, GlobalConstant.RW_FILE_MODE);
             FileInputStream out = new FileInputStream(raf.getFD());
             FileChannel channel = out.getChannel()) {

            int count = channel.read(buffer);
            buffer.flip();
            String checkpointInfo = new String(buffer.array(), 0, count);
            long checkpointTxid = Long.parseLong(checkpointInfo.split(String.valueOf(CharUtil.UNDERLINE))[1]);

            log.info("恢复 {} 文件出来的数据：checkpointInfo={}", checkPointInfoFilePath, checkpointInfo);
            this.syncedTxid = checkpointTxid;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isFinishedRecover() {
        return finishedRecover;
    }
}
