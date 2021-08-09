package com.westboy.common;

import cn.hutool.core.util.ObjectUtil;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class SimpleMemoryPool implements MemoryPool {

    protected final long sizeBytes;
    protected final int maxSingleAllocationSize;
    protected final AtomicLong availableMemory;
    protected final AtomicLong startOfNoMemPeriod;

    public SimpleMemoryPool(long sizeBytes, int maxSingleAllocationSize) {
        this.sizeBytes = sizeBytes;
        this.maxSingleAllocationSize = maxSingleAllocationSize;
        availableMemory = new AtomicLong(sizeBytes);
        startOfNoMemPeriod = new AtomicLong();
    }

    @Override
    public ByteBuffer tryAllocate(int sizeBytes) {
        if (sizeBytes < 1) {
            throw new IllegalArgumentException("Requested size " + sizeBytes + "<=0");
        }
        if (sizeBytes > maxSingleAllocationSize) {
            throw new IllegalArgumentException("Requested size " + sizeBytes + " is larger than maxSingleAllocationSize " + maxSingleAllocationSize);
        }

        long available;
        long threshold = 1;
        while ((available = availableMemory.get()) >= threshold) {
            if (availableMemory.compareAndSet(available, available - sizeBytes)) {
                break;
            }
        }
        return ByteBuffer.allocate(sizeBytes);

    }

    @Override
    public void release(ByteBuffer previouslyAllocated) {
        if (ObjectUtil.isNull(previouslyAllocated)) {
            throw new IllegalArgumentException("provided null buffer");
        }
        availableMemory.addAndGet(previouslyAllocated.capacity());
    }

    @Override
    public long size() {
        return sizeBytes;
    }

    @Override
    public long availableMemory() {
        return availableMemory.get();
    }

    @Override
    public boolean isOutOfMemory() {
        return availableMemory.get() <= 0;
    }
}
