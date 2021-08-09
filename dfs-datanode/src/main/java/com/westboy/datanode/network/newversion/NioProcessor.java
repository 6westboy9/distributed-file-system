package com.westboy.datanode.network.newversion;

import cn.hutool.core.util.ObjectUtil;
import com.westboy.datanode.config.DataNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class NioProcessor extends Thread {

    private final Integer processorId;
    private final DataNodeConfig config;
    private final ConcurrentLinkedQueue<SocketChannel> channelQueue;
    private final Selector selector;
    private final Map<String, NetworkRequest> cachedRequests;
    private final Map<String, NetworkResponse> cachedResponses;
    private final Map<String, SelectionKey> cachedKeys;

    public NioProcessor(Integer processorId, DataNodeConfig config) throws IOException {
        this.processorId = processorId;
        this.config = config;

        selector = Selector.open();
        channelQueue = new ConcurrentLinkedQueue<>();
        cachedRequests = new HashMap<>();
        cachedResponses = new HashMap<>();
        cachedKeys = new HashMap<>();

        // 初始化响应队列
        NetworkResponseQueues.get().initResponseQueue(processorId);
    }

    public void addChannel(SocketChannel channel) {
        channelQueue.add(channel);
        selector.wakeup();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 注册排队等待的连接
                registerQueuedClients();
                // 处理排队中的响应
                cacheQueuedResponse();
                // 以限时阻塞的方式感知连接中的请求
                poll(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerQueuedClients() {
        SocketChannel channel;
        while (ObjectUtil.isNotNull(channel = channelQueue.poll())) {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以多路复用的方式来监听各个连接的请求
     */
    private void poll(long interval) {
        try {
            int keys = selector.select(interval);
            if (keys > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    try {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        SocketChannel channel = (SocketChannel) key.channel();
                        String client = channel.getRemoteAddress().toString();

                        // 可读事件，封装请求到请求队列
                        if (key.isReadable()) {
                            NetworkRequest request = cachedRequests.getOrDefault(client, new NetworkRequest(config));
                            request.setChannel(channel);
                            request.setKey(key);
                            request.read();

                            if (request.hasCompletedRead()) {
                                request.setProcessorId(processorId);
                                request.setClient(client);

                                // 暂存到请求队列
                                NetworkRequestQueue.get().offer(request);

                                cachedKeys.put(client, key);
                                cachedRequests.remove(client);
                                // 必须等该请求处理完成才会对下一个读事件进行处理（等同于同步请求处理）
                                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                            } else {
                                cachedRequests.put(client, request);
                            }
                        }
                        // 可写事件，获取相应队列中的响应
                        else if (key.isWritable()) {
                            NetworkResponse response = cachedResponses.get(client);
                            channel.write(response.getBuffer());

                            log.info("将 response 响应数据写入 channel 完成，response={}", response);
                            cachedResponses.remove(client);
                            cachedKeys.remove(client);

                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂存排队中的响应
     */
    private void cacheQueuedResponse() {
        NetworkResponseQueues responseQueues = NetworkResponseQueues.get();
        NetworkResponse response;
        while (ObjectUtil.isNotNull(response = responseQueues.poll(processorId))) {
            String client = response.getClient();
            cachedResponses.put(client, response);
            cachedKeys.get(client).interestOps(SelectionKey.OP_WRITE);
        }
    }


}
