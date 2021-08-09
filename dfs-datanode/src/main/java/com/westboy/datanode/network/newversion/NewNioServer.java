package com.westboy.datanode.network.newversion;

import cn.hutool.core.util.ObjectUtil;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.datanode.network.NioServer;
import com.westboy.datanode.rpc.NameNodeRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 与新的客户端通信时为长连接
 */
@Slf4j
public class NewNioServer extends Thread implements NioServer {

    private NameNodeRpcClient namenodeRpcClient;

    private final int port;
    private final int bufferSize = 10 * 1024;
    private final int queueSize;
    private final String dataDir;
    private final int processorThreadNum;
    private final int ioThreadNum;

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private List<NioProcessor> processors;

    public NewNioServer(DataNodeConfig config, NameNodeRpcClient namenodeRpcClient) {
        super("datanode-server");
        port = config.getPort();
        queueSize = config.getQueueSize();
        dataDir = config.getDataDir();
        processorThreadNum = config.getProcessorThreadNum();
        ioThreadNum = config.getIoThreadNum();

        this.namenodeRpcClient = namenodeRpcClient;
        processors = new ArrayList<>();

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 1. 每个 NioProcessor 都有一个 Selector 负责建立连接
            // 2. 负责读取请求封装为 NetworkRequest 对象，放置到 NetworkRequestQueue 队列中，等待 IoThread 去处理（上传/下载）
            // 3. IoThread 处理完成请求（上传/下载）后，封装 NetworkResponse 对象，并放置到 NetworkResponseQueues 队列，等待 NioProcessor 线程处理
            for (int i = 0; i < processorThreadNum; i++) {
                NioProcessor processor = new NioProcessor(i, config);
                processor.setName("processor-" + i);
                processors.add(processor);
                processor.start();
            }

            // 处理
            for (int i = 0; i < ioThreadNum; i++) {
                IOThread ioThread = new IOThread(this.namenodeRpcClient);
                ioThread.setName("io-thread-" + i);
                ioThread.start();
            }

            log.info("DataNode NioServer 已启动，开始监听端口：{}", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel = serverSocketChannel.accept();
                        if (ObjectUtil.isNotNull(channel)) {
                            log.info("监听到客户端请求，address={}", channel.getRemoteAddress());
                            channel.configureBlocking(false);
                            int processorIndex = new Random().nextInt(processorThreadNum);
                            NioProcessor processor = processors.get(processorIndex);
                            processor.addChannel(channel);
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
