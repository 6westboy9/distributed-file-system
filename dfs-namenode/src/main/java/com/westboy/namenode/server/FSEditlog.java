package com.westboy.namenode.server;


import com.westboy.common.FSEditlogTxidRange;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负责管理 editlog 日志的核心组件
 *
 * @author pengbo
 * @since 2021/1/28
 */
@Slf4j
public class FSEditlog {

    /**
     * 当前递增到的 txid 的序号
     */
    private long txidSeq = 0L;
    /**
     * 内存双缓冲区
     */
    private final DoubleBuffer doubleBuffer;
    /**
     * 当前是否将内存缓冲刷入磁盘中
     */
    private volatile Boolean isSyncRunning = false;
    /**
     * 当前是否有线程在等待刷新下一批 editlog 到磁盘中去
     */
    private volatile Boolean isWaitSync = false;
    /**
     * 在同步到磁盘的最大的一个 txid
     */
    private volatile long syncedTxid = 0L;
    /**
     * 是否正在调度一次刷盘的操作
     */
    private volatile boolean isSchedulingSync = false;
    /**
     * 每个线程自己本地 txid 副本
     */
    private final ThreadLocal<Long> localTxid = new ThreadLocal<>();

    private final AtomicInteger write2BufferCount = new AtomicInteger(0);

    public FSEditlog(NameNodeConfig config) {
        this.doubleBuffer = new DoubleBuffer(config);
    }

    public void setSyncedTxid(long syncedTxid, boolean recover) {
        synchronized (this) {
            this.syncedTxid = syncedTxid;
            doubleBuffer.setSyncedTxid(syncedTxid);
            if (recover) {
                txidSeq = syncedTxid;
                doubleBuffer.setStartTxid(syncedTxid + 1);
            }
        }
    }

    public void logEdit(String content) {
        synchronized (this) {
            // 是否有人正在调度一次刷盘的操作
            // 发现 currentBuffer 写满后，要等待刷盘
            // 其实，仅仅是依次将 currentBuffer 交换到 syncBuffer 中的过程，内存级别交换，速度很快，只要交换到 syncBuffer 后，currentBuffer 清空，就又可以写日志了
            try {
                waitSchedulingSync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            txidSeq++;
            long txid = txidSeq;
            localTxid.set(txid);

            Editlog editlog = new Editlog(txid, content);
            try {
                doubleBuffer.write(editlog);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 每次写完一条 editlog 之后，就应该检查一下当前这个缓冲区是否满了
            // 没写满，直接返回
            if (!doubleBuffer.shouldSyncDisk()) {
                log.debug("currentBuffer 暂未写满，第 {} 个请求", write2BufferCount.incrementAndGet());
                return;
            }
            log.debug("currentBuffer 已写满，第 {} 个请求", write2BufferCount.incrementAndGet());
            // 需要刷盘
            isSchedulingSync = true;
        }

        // 涉及重操作
        // 感觉这里不会存在并发，上面的逻辑就保证了 currentBuffer 写满后，会将 isSchedulingSync 设置为 true
        // 然后前面的 waitSchedulingSync 方法，就会导致，其他线程在写时发现已满，想往下执行 logSync 时，被阻塞了
        logSync();
    }

    private void waitSchedulingSync() throws InterruptedException {
        while (isSchedulingSync) {
            log.info("等待刷盘，当前线程：{}", Thread.currentThread().getName());
            wait(1000);
        }
    }

    /**
     * 将内存缓冲区中的数据刷入磁盘中
     */
    private void logSync() {
        log.info("参与刷盘操作的线程：{}", Thread.currentThread().getName());
        // 在执行该同步代码块中的内容时，是不会在写入数据到 currentBuffer 中，此代码块逻辑就是负责将 currentBuffer 数据复制到 syncBuffer 中去
        synchronized (this) {
            long txid = localTxid.get();
            // 1.正在同步中
            if (isSyncRunning) {
                // 已经同步过
                if (txid <= syncedTxid) {
                    return;
                }

                // 只要有一个线程在同步中，并且有另外一个线程在等待中，后续再来的线程直接返回
                // 也就就是说，同时只能有一个线程在刷盘，一个线程在等待刷盘，其他线程均直接返回，作用上，等同于间隔性刷盘（重要！重要！重要！）
                // 等待同步中
                if (isWaitSync) {
                    return;
                }

                isWaitSync = true;

                while (isSyncRunning) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isWaitSync = false;
            }

            // 2.并未在同步过程中的处理逻辑
            // 2.1.交换两块缓冲区，将 currentBuffer 交换到 syncBuffer 中
            doubleBuffer.setReadyToSync();
            // 2.2.设置已经同步完成后当前最大 txid
            syncedTxid = txid;
            // 2.3.设置当前正在同步到磁盘的标志位
            isSchedulingSync = false;
            // 2.4.唤醒哪些卡在 while 循环哪儿的线程
            notifyAll();
            // 2.5.设置正在同步中
            isSyncRunning = true;
        }

        // 线程在刷盘时，其他线程都在等待中...
        // 这个过程其实是比较慢，基本上肯定是毫秒级了，弄不好就几十毫秒
        // 将 syncBuffer 数据写入底层磁盘文件
        doubleBuffer.flush(); // 这里其实也有并发问题，可能跟下边的 flush 方法中的 doubleBuffer.flush() 并发刷盘

        // 刷盘完成后，唤醒等待中的线程
        synchronized (this) {
            isSyncRunning = false;
            notifyAll();
        }
    }

    /**
     * 强制把内存缓冲里的数据刷入磁盘中
     */
    public void flush() {
        synchronized (this) {
            if (doubleBuffer.size() > 0L) {
                log.info("执行 editlog 内存数据刷入磁盘");
                doubleBuffer.setReadyToSync();
                doubleBuffer.flush();
            }
        }
    }

    public void addFlushedTxids(List<FSEditlogTxidRange> ranges) {
        synchronized (this) {
            doubleBuffer.getFlushedTxids().addAll(ranges);
            log.info("恢复刷入磁盘中的 txid 数组，详情：{}", doubleBuffer.getFlushedTxids());
        }
    }

    public List<FSEditlogTxidRange> getFlushedTxids() {
        synchronized (this) {
            return doubleBuffer.getFlushedTxids();
        }
    }

    public String[] getBufferedEditLogs() {
        synchronized (this) {
            return doubleBuffer.getBufferedEditLogs();
        }
    }

}
