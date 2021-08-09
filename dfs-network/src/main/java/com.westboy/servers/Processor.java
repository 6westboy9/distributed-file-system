package com.westboy.servers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.ZhangSelector;
import com.westboy.common.MemoryPool;
import com.westboy.common.requests.RequestContext;
import com.westboy.common.requests.RequestHeader;
import com.westboy.common.utils.SocketSupport;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Processor extends Thread {

    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final int id;
    private ConnectionQuotas connectionQuotas;
    private final RequestChannel requestChannel;
    private final ZhangSelector zhangSelector;
    // 管理新建立的 SocketChannel 队列
    private final ConcurrentLinkedDeque<SocketChannel> newConnections;
    // private final Map<SocketChannel, SelectionKey> channelKeys;
    private final MemoryPool memoryPool;

    public Processor(int id, ConnectionQuotas connectionQuotas, RequestChannel requestChannel, MemoryPool memoryPool) throws IOException {
        this.id = id;
        this.connectionQuotas = connectionQuotas;
        this.requestChannel = requestChannel;
        this.memoryPool = memoryPool;
        zhangSelector = new ZhangSelector();
        newConnections = new ConcurrentLinkedDeque<>();
        // channelKeys = new ConcurrentHashMap<>();
    }

    public void accept(SocketChannel socketChannel, SelectionKey key) {
        newConnections.add(socketChannel);
        // channelKeys.put(socketChannel, key);
    }

    @Override
    public void run() {
        while (isRunning.get()) {
            try {
                // 接收并建立新的连接
                configureNewConnections();
                // 监听请求事件
                poll();

                // 处理当前批次 poll 读取的数据
                handleCompletedReceives();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建连接
     */
    private void configureNewConnections() {
        // newConnections 为非阻塞，没有则返回 null
        while (CollUtil.isNotEmpty(newConnections)) {
            SocketChannel channel = newConnections.poll();
            if (ObjectUtil.isNotNull(channel)) {
                try {
                    log.info("Processor {} listening to new connection from {}", id, channel.getRemoteAddress());
                    zhangSelector.register(SocketSupport.connectionId(channel), channel);
                } catch (IOException e) {
                    log.error("Error while new connection.", e);
                }
            }
        }
    }

    /**
     * 监听事件
     */
    private void poll() throws IOException {
        // 处理已经客户端非连接请求事件
        zhangSelector.poll(300);
    }


    private void handleCompletedReceives() {
        try {
            for (NetworkReceive receive : zhangSelector.getCompletedReceives()) {
                String connectionId = receive.connectionId();
                ZhangChannel channel = zhangSelector.getChannel(connectionId);

                RequestHeader header = RequestHeader.parse(receive.payload());
                RequestContext context = new RequestContext(header, connectionId, channel.address());

                // 生成一个请求发送到 requestChannel 的请求队列中去，后续通过 RequestHandler 异步处理
                RequestChannel.Request request = new RequestChannel.Request.RequestBuilder()
                        .processorId(id)
                        .context(context)
                        .startTimeNanos(System.nanoTime())
                        .memoryPool(memoryPool)
                        .byteBuffer(receive.payload())
                        .build();
                requestChannel.sendRequest(request);
                // 移除对读事件的监听
                zhangSelector.mute(receive.connectionId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
