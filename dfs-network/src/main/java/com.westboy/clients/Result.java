package com.westboy.clients;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Result {

    private final CountDownLatch latch = new CountDownLatch(1);
    // 上传完成时间
    private volatile long completionTime;
    // 上传异常信息
    private volatile RuntimeException exception;


    public void await() throws InterruptedException {
        latch.await();
    }

    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return latch.await(timeout, unit);
    }

    public boolean completed() {
        return latch.getCount() == 0L;
    }
}
