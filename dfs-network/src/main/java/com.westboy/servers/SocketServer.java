package com.westboy.servers;

import com.westboy.common.MemoryPool;

import java.io.IOException;

public class SocketServer {

    private final ZhangServerConfig config;
    private final RequestChannel requestChannel;
    private ConnectionQuotas connectionQuotas;
    private Acceptor acceptor;
    private Processor[] processors;
    private final MemoryPool memoryPool;

    public SocketServer(ZhangServerConfig config) {
        this.config = config;
        // 请求队列的最大长度
        int maxQueuedRequests = config.getMaxQueuedRequests();
        // 响应队列的个数直接等于 processor 线程数量
        int totalProcessorThreads = config.getTotalProcessorThreads();
        requestChannel = new RequestChannel(maxQueuedRequests, totalProcessorThreads);
        memoryPool = MemoryPool.NONE;
    }

    public RequestChannel getRequestChannel() {
        return requestChannel;
    }

    public void startup() throws IOException {
        connectionQuotas = new ConnectionQuotas();
        String host = config.getHost();
        int port = config.getPort();
        int sendBufferSize = config.getSendBufferSize();
        int receiveBufferSize = config.getReceiveBufferSize();
        int numProcessorThreads = config.getNumProcessorThreads();

        processors = new Processor[numProcessorThreads];
        for (int i = 0; i < processors.length; i++) {
            processors[i] = new Processor(i, connectionQuotas, requestChannel, memoryPool);
        }

        acceptor = new Acceptor(host, port, sendBufferSize, receiveBufferSize, connectionQuotas, processors);
        acceptor.setDaemon(false);
        acceptor.setName("network-acceptor");
        acceptor.start();

        startupProcessors();
    }

    private void startupProcessors() {
        acceptor.startProcessors();
    }
}
