package com.westboy.servers;

import com.westboy.common.MemoryPool;
import com.westboy.common.requests.RequestContext;
import lombok.Builder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RequestChannel {

    private final ArrayBlockingQueue<Request> requestQueue;
    private final List<LinkedBlockingQueue<Request>> responseQueues;

    /**
     * 初始化请求通道
     *
     * @param requestQueueSize 一个是请求队列的最大长度
     * @param responseQueueNum 一个是响应队列的个数
     */
    public RequestChannel(int requestQueueSize, int responseQueueNum) {
        this.requestQueue = new ArrayBlockingQueue<>(requestQueueSize);
        this.responseQueues = new ArrayList<>(responseQueueNum);
    }

    void sendRequest(Request request) throws InterruptedException {
        requestQueue.put(request);
    }


    public Request pollRequest(long timeout) throws InterruptedException {
        return requestQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }

    @Builder
    public static class Request {
        private int processorId;
        private RequestContext context;
        private long startTimeNanos;
        private MemoryPool memoryPool;
        private ByteBuffer byteBuffer;
    }
}
