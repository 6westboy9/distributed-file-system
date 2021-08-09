package com.westboy.clients;

import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.record.MemoryRecords;
import com.westboy.common.record.MemoryRecordsBuilder;
import com.westboy.common.utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Accumulator {

    private final long totalMemory;
    private final long usedMemory;
    private final AtomicInteger appendsInProgress;
    private final Deque<FileBatch> deque;

    public Accumulator(int totalMemory) {
        this.totalMemory = totalMemory;
        usedMemory = 0L;
        appendsInProgress = new AtomicInteger(0);
        deque = new ArrayDeque<>();
    }

    public FutureResultMetadata tryAppend(FileEntity fileEntity, Callback callback) {
        ByteBuffer buffer = null;
        appendsInProgress.incrementAndGet();
        try {
            long size = fileEntity.size();
            if (usedMemory + size > totalMemory) {
                throw new IllegalArgumentException("Attempt to allocate " + size + " bytes, but there is a hard limit of " + this.totalMemory + " on memory allocations.");
            }

            buffer = ByteBuffer.allocate(Math.toIntExact(size));
            MemoryRecordsBuilder recordsBuilder = recordsBuilder(buffer);
            FileBatch batch = new FileBatch(recordsBuilder);

            FutureResultMetadata future = batch.tryAppend(fileEntity, callback);

            synchronized (deque) {
                deque.add(batch);
            }


            // 验证
            // buffer.flip();
            // int bufferSize = buffer.remaining();
            // int bodySize = buffer.getInt();
            // int fileSize = buffer.getInt();
            // byte[] fileBytes = Utils.toArray(buffer, buffer.position(), fileSize);
            // buffer.position(fileSize + buffer.position());
            // int pathSize = buffer.getInt();
            // byte[] pathBytes = Utils.toArray(buffer, 0, pathSize);
            // System.out.println(new String(pathBytes));


            // for gc
            buffer = null;

            return future;
        } finally {
            // 后续搞池化之后需要释放
            // if (ObjectUtil.isNotNull(buffer)) { }
            appendsInProgress.decrementAndGet();
        }
    }

    private MemoryRecordsBuilder recordsBuilder(ByteBuffer buffer) {
        return MemoryRecords.builder(buffer);
    }


}
