package com.westboy.servers;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Acceptor extends Thread {

    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    private int sendBufferSize;
    private int receiveBufferSize;
    // 用于统计每个 Acceptor 接收的客户端连接数量
    private ConnectionQuotas connectionQuotas;

    private final AtomicBoolean processorsStarted = new AtomicBoolean();


    private final Processor[] processors;

    public Acceptor(String host, int port, int sendBufferSize, int receiveBufferSize, ConnectionQuotas connectionQuotas, Processor[] processors) throws IOException {
        this.sendBufferSize = sendBufferSize;
        this.receiveBufferSize = receiveBufferSize;
        this.connectionQuotas = connectionQuotas;
        this.processors = processors;

        selector = Selector.open();
        serverSocketChannel = openServerSocket(host, port);
    }

    private ServerSocketChannel openServerSocket(String host, int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(host, port));
        return serverSocketChannel;
    }

    public void startProcessors() {
        for (int i = 0; i < processors.length; i++) {
            processors[i].setName("network-processor-thread-" + i);
            processors[i].setDaemon(false);
            processors[i].start();
        }
    }

    @Override
    public void run() {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (ClosedChannelException e) {
            log.error("Error while registering selector", e);
        }
        startupComplete();

        int currentProcessor = 0;
        while (isRunning.get()) {
            try {
                int ready = selector.select(500);
                if (ready > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        // 只管连接建立，其他请求事件交给 Processor 去处理
                        if (key.isAcceptable()) {
                            accept(key, processors[currentProcessor]);
                        } else {
                            throw new IllegalStateException("Unrecognized key state for acceptor thread.");
                        }
                        currentProcessor = (currentProcessor + 1) % processors.length;
                    }
                }
            } catch (IOException e) {
                log.error("Error while accepting connection.", e);
            }
        }
    }

    private void startupComplete() {
        log.info("Startup has completed.");
    }

    private void accept(SelectionKey key, Processor processor) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        connectionQuotas.increase(socketChannel.socket().getInetAddress());
        socketChannel.configureBlocking(false);
        // 关闭 Nagle 算法，默认开启，
        socketChannel.socket().setTcpNoDelay(true);
        socketChannel.socket().setKeepAlive(true);

        if (sendBufferSize != -1) {
            socketChannel.socket().setSendBufferSize(sendBufferSize);
        }
        if (receiveBufferSize != -1) {
            socketChannel.socket().setReceiveBufferSize(receiveBufferSize);
        }

        processor.accept(socketChannel, key);
    }
}
