package com.westboy.namenode.server;

import com.westboy.common.FSEditlogTxidRange;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 内存双缓冲
 *
 * @author pengbo
 * @since 2021/2/1
 */
@Slf4j
public class DoubleBuffer {

    private static final String LINE_SEPARATOR = "\n";
    /**
     * 专门用来承载线程写入的 editlog 的一块缓冲区
     */
    private EditLogBuffer currentBuffer;
    /**
     * 专门用来将数据同步到磁盘中去的一块缓冲
     */
    private EditLogBuffer syncBuffer;
    /**
     * 已经刷入磁盘中的 txid 的范围
     */
    private final List<FSEditlogTxidRange> flushedTxids;

    /**
     * 当前这块缓冲区写入的最大的一个 txid 值
     */
    private long startTxid = 1L;
    private volatile long syncedTxid = 0L;

    private final String editLogTemplate;
    private final long editlogBufferLimit;

    public DoubleBuffer(NameNodeConfig config) {
        this.currentBuffer = new EditLogBuffer();
        this.syncBuffer = new EditLogBuffer();
        this.flushedTxids = new CopyOnWriteArrayList<>();
        this.editLogTemplate = config.getEditLogTemplate();
        this.editlogBufferLimit = config.getEditlogBufferLimit();
    }

    public void setStartTxid(long startTxid) {
        this.startTxid = startTxid;
    }

    public void setSyncedTxid(long syncedTxid) {
        this.syncedTxid = syncedTxid;
    }

    /**
     * 将 editlog 写入内存缓冲中去
     */
    public void write(Editlog log) throws IOException {
        currentBuffer.write(log);
    }

    public long size() {
        return currentBuffer.size();
    }

    /**
     * 交换两个缓冲区，为了同步内存数据到磁盘做准备
     */
    public void setReadyToSync() {
        log.debug("交换 buffer 前信息：currentBuffer endTxid={}，syncBuffer endTxid={}", currentBuffer.getEndTxid(), syncBuffer.getEndTxid());
        EditLogBuffer tmp = currentBuffer;
        currentBuffer = syncBuffer;
        syncBuffer = tmp;
        log.debug("交换 buffer 后信息：currentBuffer endTxid={}，syncBuffer endTxid={}", currentBuffer.getEndTxid(), syncBuffer.getEndTxid());
    }

    /**
     * 将同步缓冲区中的数据刷入磁盘中去
     */
    public void flush() {
        syncBuffer.flush();
        syncBuffer.clear();
    }

    public List<FSEditlogTxidRange> getFlushedTxids() {
        return flushedTxids;
    }

    /**
     * 判断一下当前的缓冲区是否写满了需要刷到磁盘上去
     */
    public boolean shouldSyncDisk() {
        return currentBuffer.size() >= editlogBufferLimit;
    }

    /**
     * 获取当前缓冲区里的数据
     */
    public String[] getBufferedEditLogs() {
        if (currentBuffer.size() == 0) {
            return null;
        }
        String editsLogRawData = new String(currentBuffer.getBufferData());
        return editsLogRawData.split(LINE_SEPARATOR);
    }

    class EditLogBuffer {
        /**
         * 针对内存缓冲区的字节数组输出流
         */
        private final ByteArrayOutputStream outputStream;
        /**
         * 上次刷盘时最大 txid 的值
         */
        private long endTxid = 0L;

        public EditLogBuffer() {
            this.outputStream = new ByteArrayOutputStream((int) (editlogBufferLimit * 2));
        }

        public long getEndTxid() {
            return endTxid;
        }

        public long size() {
            return outputStream.size();
        }

        public byte[] getBufferData() {
            return outputStream.toByteArray();
        }

        public void clear() {
            outputStream.reset();
        }

        /**
         * 将 editlog 日志写入缓冲区
         */
        public void write(Editlog editlog) throws IOException {
            endTxid = editlog.getTxid();
            outputStream.write(editlog.getContent().getBytes());
            outputStream.write(LINE_SEPARATOR.getBytes());
            log.info("写入一条 editlog 数据：" + editlog.getContent() + "， 大小：" + outputStream.size());
        }

        public void flush() {
            // 当前需要刷新内存中的 txid 小于等于已经被同步的 txid 时，不需要再刷盘
            if (endTxid <= syncedTxid) {
                // TODO 但是还是可能存在，刚刷入 syncTxid=100，然后立马有一个新的 endTxid=101，但是 startTxid=90，还是会存在有部分数据被刷入磁盘
                log.warn("当前需要刷新内存中的 endTxid 小于等于已经被同步的 syncTxid 不需要再刷盘 syncTxid={}，endTxid={}", syncedTxid, endTxid);
                return;
            }

            ByteBuffer buffer = ByteBuffer.wrap(outputStream.toByteArray());

            String editlogFile = String.format(editLogTemplate, startTxid, endTxid);

            log.info("创建新的 editlog 文件：" + editlogFile);

            flushedTxids.add(new FSEditlogTxidRange(startTxid, endTxid));

            try (RandomAccessFile file = new RandomAccessFile(editlogFile, GlobalConstant.RW_FILE_MODE);
                 FileOutputStream out = new FileOutputStream(file.getFD());
                 FileChannel editsLogFileChannel = out.getChannel()) {

                editsLogFileChannel.write(buffer);
                // 强制把数据刷入磁盘上
                editsLogFileChannel.force(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
            startTxid = endTxid + 1;
        }
    }
}
