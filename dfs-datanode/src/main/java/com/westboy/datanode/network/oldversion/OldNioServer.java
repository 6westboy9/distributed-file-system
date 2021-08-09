package com.westboy.datanode.network.oldversion;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.common.entity.FileInfo;
import com.westboy.datanode.config.DataNodeConfig;
import com.westboy.datanode.network.NioServer;
import com.westboy.datanode.rpc.NameNodeRpcClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 与旧的客户端通信为短连接，即一次上传或者读取文件后，就断开连接
 */
@Slf4j
public class OldNioServer extends Thread implements NioServer {

    public static final Integer SEND_FILE = 1;
    public static final Integer READ_FILE = 2;

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    // 内存队列，无界队列
    private final List<LinkedBlockingQueue<SelectionKey>> queues = new ArrayList<>();
    // 缓存没读取完的文件数据
    private final Map<String, CachedRequest> cachedRequests = new ConcurrentHashMap<>();
    // 缓存没读取完的请求类型
    private final Map<String, ByteBuffer> requestTypeCache = new ConcurrentHashMap<>();
    // 缓存没读取完的文件名大小
    private final Map<String, ByteBuffer> filenameLengthCache = new ConcurrentHashMap<>();
    // 缓存没读取完的文件名
    private final Map<String, ByteBuffer> filenameCache = new ConcurrentHashMap<>();
    // 缓存没读取完的文件大小
    private final Map<String, ByteBuffer> fileLengthCache = new ConcurrentHashMap<>();
    // 缓存没读取完的文件
    private final Map<String, ByteBuffer> fileCache = new ConcurrentHashMap<>();

    private final int port;
    private final int bufferSize = 10 * 1024;
    private final int queueSize;
    private final String dataDir;

    private final NameNodeRpcClient namenodeRpcClient;

    public OldNioServer(DataNodeConfig config, NameNodeRpcClient namenodeRpcClient) {
        super("datanode-server");
        port = config.getPort();
        queueSize = config.getQueueSize();
        dataDir = config.getDataDir();
        this.namenodeRpcClient = namenodeRpcClient;

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            for (int i = 0; i < queueSize; i++) {
                LinkedBlockingQueue<SelectionKey> queue = new LinkedBlockingQueue<>();
                queues.add(queue);
                Worker worker = new Worker(queue);
                worker.setName("worker-" + i);
                worker.start();
            }

            log.info("DataNode NioServer 已启动，开始监听端口：{}", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                log.debug("监听到事件数量：" + selectionKeys.size());
                Iterator<SelectionKey> keysIterator = selectionKeys.iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    handleEvents(key);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * 处理请求分发
     */
    private void handleEvents(SelectionKey key) {
        try {
            SocketChannel channel;
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                channel = serverSocketChannel.accept();
                if (ObjectUtil.isNotNull(channel)) {
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                }
            } else if (key.isReadable()) {
                channel = (SocketChannel) key.channel();
                String client = channel.getRemoteAddress().toString();
                int queueIndex = client.hashCode() % queues.size();
                queues.get(queueIndex).put(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求的工作线程
     */
    class Worker extends Thread {

        private final LinkedBlockingQueue<SelectionKey> queue;

        public Worker(LinkedBlockingQueue<SelectionKey> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    SelectionKey key = queue.take();
                    log.debug("worker 线程当前队列大小：{}", queue.size());
                    SocketChannel channel = (SocketChannel) key.channel();
                    handleRequest(channel, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleRequest(SocketChannel channel, SelectionKey key) throws Exception {
        String client = channel.getRemoteAddress().toString();
        CachedRequest cachedRequest = getCachedRequest(client);
        log.debug("接收到客户端的请求：{}", client);

        int requestType = getRequestType(channel, cachedRequest);
        if (requestType <= 0) {
            return;
        }

        log.info("解析出请求类型为：{}", requestType);
        if (SEND_FILE.equals(requestType)) {
            handleSendFileRequest(channel, key, cachedRequest);
        } else if (READ_FILE.equals(requestType)) {
            handleReadFileRequest(channel, key, cachedRequest);
        }
    }

    public int getRequestType(SocketChannel channel, CachedRequest cachedRequest) throws Exception {
        int requestType = cachedRequest.getRequestType();
        if (requestType <= 0) {
            String client = cachedRequest.getClient();
            ByteBuffer requestTypeBuffer = requestTypeCache.getOrDefault(client, ByteBuffer.allocate(4));
            channel.read(requestTypeBuffer);

            if (!requestTypeBuffer.hasRemaining()) {
                requestTypeBuffer.rewind();
                requestType = requestTypeBuffer.getInt();
                cachedRequest.setRequestType(requestType);
                requestTypeCache.remove(client);
            } else {
                requestTypeCache.put(client, requestTypeBuffer);
            }
        }
        return requestType;
    }

    private CachedRequest getCachedRequest(String client) {
        CachedRequest cachedRequest = cachedRequests.getOrDefault(client, new CachedRequest());
        cachedRequest.setClient(client);
        cachedRequests.put(client, cachedRequest);
        return cachedRequest;
    }

    private int getFilenameLength(SocketChannel channel, CachedRequest cachedRequest) throws IOException {
        int filenameLength = cachedRequest.getFilenameLength();
        if (filenameLength <= 0) {
            String client = cachedRequest.getClient();
            ByteBuffer filenameLengthBuffer = filenameLengthCache.getOrDefault(client, ByteBuffer.allocate(4));
            channel.read(filenameLengthBuffer);
            if (!filenameLengthBuffer.hasRemaining()) {
                filenameLengthBuffer.rewind();
                filenameLength = filenameLengthBuffer.getInt();
                cachedRequest.setFilenameLength(filenameLength);
                filenameLengthCache.remove(client);
            } else {
                filenameLengthCache.put(client, filenameLengthBuffer);
            }
        }
        return filenameLength;
    }


    private Filename getFilename(SocketChannel channel, CachedRequest cachedRequest) throws Exception {
        Filename filename = cachedRequest.getFilename();
        if (ObjectUtil.isNull(filename)) {
            String client = cachedRequest.getClient();
            ByteBuffer filenameBuffer = filenameCache.getOrDefault(client, ByteBuffer.allocate(cachedRequest.getFilenameLength()));

            channel.read(filenameBuffer);
            if (!filenameBuffer.hasRemaining()) {
                filenameBuffer.rewind();
                String filenameStr = new String(filenameBuffer.array());

                filename = new Filename();
                filename.setRelativeFilename(filenameStr);
                filename.setAbsoluteFilename(dataDir + filenameStr);
                cachedRequest.setFilename(filename);
                filenameCache.remove(client);
            } else {
                filenameCache.put(client, filenameBuffer);
            }
        }
        return filename;
    }

    private long getFileLength(SocketChannel channel, CachedRequest cachedRequest) throws Exception {
        long fileLength = cachedRequest.getFileLength();
        if (fileLength <= 0) {
            String client = cachedRequest.getClient();
            ByteBuffer fileLengthBuffer = fileLengthCache.getOrDefault(client, ByteBuffer.allocate(8));
            channel.read(fileLengthBuffer);

            if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                cachedRequest.setFileLength(fileLength);
                fileLengthCache.remove(client);
            } else {
                fileLengthCache.put(client, fileLengthBuffer);
            }
        }
        return fileLength;
    }

    private void handleSendFileRequest(SocketChannel channel, SelectionKey key, CachedRequest cachedRequest) throws Exception {
        int filenameLength = getFilenameLength(channel, cachedRequest);
        log.info("从网络请求中解析出来文件名长度：{}", filenameLength);
        if (filenameLength <= 0) {
            return;
        }

        Filename filename = getFilename(channel, cachedRequest);
        log.info("从网络请求中解析出来文件名：{}", filename);
        if (ObjectUtil.isNull(filename)) {
            return;
        }

        long fileLength = getFileLength(channel, cachedRequest);
        log.info("从网络请求中解析出来文件大小：{}", fileLength);
        if (fileLength <= 0) {
            return;
        }

        boolean result = createFile(channel, key, cachedRequest);
        if (result) {
            reportFileInfo(cachedRequest);
        }
    }

    private boolean createFile(SocketChannel channel, SelectionKey key, CachedRequest cachedRequest) throws IOException {
        String client = cachedRequest.getClient();
        Filename filename = cachedRequest.getFilename();

        String filepath = filename.getAbsoluteFilename();
        // 底层会校验，如果存在不需要创建
        FileUtil.touch(filepath);


        RandomAccessFile file = new RandomAccessFile(filepath, GlobalConstant.RW_FILE_MODE);
        FileChannel fileChannel = file.getChannel();

        fileChannel.position(fileChannel.size());
        log.info("对本地磁盘文件定位到 position={}", fileChannel.size());

        ByteBuffer fileBuffer = fileCache.getOrDefault(client, ByteBuffer.allocate((int) cachedRequest.getFileLength()));
        channel.read(fileBuffer);

        if (!fileBuffer.hasRemaining()) {
            fileBuffer.rewind();
            int written = fileChannel.write(fileBuffer);
            fileCache.remove(client);
            log.info("本次文件上传完毕，将 {} 字节的数据写入本地磁盘文件", written);

            ByteBuffer outBuffer = ByteBuffer.wrap(GlobalConstant.SUCCESS.getBytes());
            channel.write(outBuffer);
            cachedRequests.remove(client);
            log.info("文件读取完毕，返回响应给客户端：{}", client);
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
            return true;
        } else {
            log.info("本次文件上传出现拆包问题，缓存起来，下次继续读取");
            fileCache.put(client, fileBuffer);
            return false;
        }
    }

    private void reportFileInfo(CachedRequest cachedRequest) {
        Filename filename = cachedRequest.getFilename();
        // 增量上报 Master 节点自己接收到了一个文件的副本 /image/product/iphone.jpg
        FileInfo fileInfo = new FileInfo(filename.getRelativeFilename(), cachedRequest.getFileLength());
        namenodeRpcClient.reportFileInfo(fileInfo);
        log.info("增量上报收到的文件 {} 给 NameNode 节点", filename.getRelativeFilename());
    }


    /**
     * 读取文件
     */
    private void handleReadFileRequest(SocketChannel channel, SelectionKey key, CachedRequest cachedRequest) throws Exception {
        int filenameLength = getFilenameLength(channel, cachedRequest);
        log.info("从网络请求中解析出来文件名长度：{}", filenameLength);
        if (filenameLength <= 0) {
            return;
        }

        Filename filename = getFilename(channel, cachedRequest);
        log.info("从网络请求中解析出来文件名：{}", filename);
        if (ObjectUtil.isNull(filename)) {
            return;
        }

        readFile(channel, key, cachedRequest);
    }

    private void readFile(SocketChannel channel, SelectionKey key, CachedRequest cachedRequest) throws IOException {
        String client = cachedRequest.getClient();
        Filename filename = cachedRequest.getFilename();
        RandomAccessFile accessFile = new RandomAccessFile(filename.getAbsoluteFilename(), GlobalConstant.RW_FILE_MODE);
        long fileLength = accessFile.length();
        cachedRequest.setFileLength(fileLength);
        FileChannel fileChannel = accessFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(8 + (int) fileLength);
        buffer.putLong(fileLength); // 8

        int read = fileChannel.read(buffer);
        log.info("从本次磁盘文件中读取了 {} 字节的数据", read);

        buffer.rewind();
        int sent = channel.write(buffer);
        log.info("将 {} 字节的数据发送给了客户端", sent);

        if (read == fileLength) {
            log.info("文件发送完毕，给客户端：{}", client);
            cachedRequests.remove(client);
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        }
    }


    static class Filename {
        private String relativeFilename;
        private String absoluteFilename;

        public String getRelativeFilename() {
            return relativeFilename;
        }

        public void setRelativeFilename(String relativeFilename) {
            this.relativeFilename = relativeFilename;
        }

        public String getAbsoluteFilename() {
            return absoluteFilename;
        }

        public void setAbsoluteFilename(String absoluteFilename) {
            this.absoluteFilename = absoluteFilename;
        }

        @Override
        public String toString() {
            return "Filename [relativeFilename=" + relativeFilename + ", absoluteFilename=" + absoluteFilename + "]";
        }
    }

    @Data
    static class CachedRequest {
        private String client;
        private int requestType;
        private Filename filename;
        private int filenameLength;
        private long fileLength;
    }
}
