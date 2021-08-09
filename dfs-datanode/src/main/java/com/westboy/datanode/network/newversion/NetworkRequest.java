package com.westboy.datanode.network.newversion;

import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.constant.RequestType;
import com.westboy.datanode.config.DataNodeConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Optional;

@Slf4j
public class NetworkRequest {

    // NioProcessor 线程唯一标识
    private Integer processorId;
    // 标识该请求是哪个客户端发送过来的
    private String client;
    // 本次网络请求对应的连接
    private SelectionKey key;
    // 本次网络请求对应的连接
    private SocketChannel channel;

    // 缓存当前请求对象
    private final CachedRequest cachedRequest;

    // 缓存没读取完的请求类型
    private ByteBuffer requestTypeCache;
    // 缓存没读取完的文件名大小
    private ByteBuffer filenameLengthCache;
    // 缓存没读取完的文件名
    private ByteBuffer filenameCache;
    // 缓存没读取完的文件大小
    private ByteBuffer fileLengthCache;
    // 缓存没读取完的文件
    private ByteBuffer fileCache;

    // 文件数据存储目录
    private final String dataDir;

    public NetworkRequest(DataNodeConfig config) {
        dataDir = config.getDataDir();
        cachedRequest = new CachedRequest();
    }

    public Integer getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Integer processorId) {
        this.processorId = processorId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    /**
     * 读取客户端发送过来的请求
     */
    public void read() throws Exception {
        int reqType = getReqType(channel);
        if (reqType <= 0) {
            return;
        }

        log.info("解析出请求类型为：{}", reqType);
        if (reqType == RequestType.SEND_FILE) {
            handleSendFileRequest(channel);
        } else if (reqType == RequestType.READ_FILE) {
            handleReadFileRequest(channel);
        }
    }

    public int getReqType(SocketChannel channel) throws Exception {
        int requestType = cachedRequest.getRequestType();
        if (requestType == 0) {
            ByteBuffer requestTypeBuffer = Optional.ofNullable(requestTypeCache).orElse(ByteBuffer.allocate(4));

            int read = channel.read(requestTypeBuffer);
            checkAndClose(read, channel);

            if (!requestTypeBuffer.hasRemaining()) {
                requestTypeBuffer.rewind();
                requestType = requestTypeBuffer.getInt();
                cachedRequest.setRequestType(requestType);
            } else {
                requestTypeCache = requestTypeBuffer;
            }
        }
        return requestType;
    }

    private void handleSendFileRequest(SocketChannel channel) throws Exception {
        int filenameLength = getFilenameLength(channel);
        log.info("从网络请求中解析出来文件名长度：{}", filenameLength);
        if (filenameLength <= 0) {
            return;
        }

        Filename filename = getFilename(channel);
        log.info("从网络请求中解析出来文件名：{}", filename);
        if (ObjectUtil.isNull(filename)) {
            return;
        }

        long fileLength = getFileLength(channel);
        log.info("从网络请求中解析出来文件大小：{}", fileLength);
        if (fileLength <= 0) {
            return;
        }

        getFile(channel);
    }

    private void handleReadFileRequest(SocketChannel channel) throws Exception {
        int filenameLength = getFilenameLength(channel);
        log.info("从网络请求中解析出来文件名长度：{}", filenameLength);
        if (filenameLength <= 0) {
            return;
        }

        Filename filename = getFilename(channel);
        log.info("从网络请求中解析出来文件名：{}", filename);

        cachedRequest.setHasCompletedRead(true);
    }


    private void checkAndClose(int read, SocketChannel channel) throws IOException {
        // 先暂时这么处理
        if (read == -1) {
            log.warn("断开连接，客户端信息：{}", channel.getRemoteAddress());
            channel.close();
            // throw new IOException("读取到数据为-1");
        }
    }


    private int getFilenameLength(SocketChannel channel) throws IOException {
        int filenameLength = cachedRequest.getFilenameLength();
        if (filenameLength <= 0) {
            ByteBuffer filenameLengthBuffer = Optional.ofNullable(filenameLengthCache).orElse(ByteBuffer.allocate(4));
            int read = channel.read(filenameLengthBuffer);
            checkAndClose(read, channel);

            if (!filenameLengthBuffer.hasRemaining()) {
                filenameLengthBuffer.rewind();
                filenameLength = filenameLengthBuffer.getInt();
                cachedRequest.setFilenameLength(filenameLength);
            } else {
                filenameLengthCache = filenameLengthBuffer;
            }
        }
        return filenameLength;
    }


    private Filename getFilename(SocketChannel channel) throws Exception {
        Filename filename = cachedRequest.getFilename();
        if (ObjectUtil.isNull(filename)) {
            ByteBuffer filenameBuffer = Optional.ofNullable(filenameCache).orElse(ByteBuffer.allocate(cachedRequest.getFilenameLength()));
            int read = channel.read(filenameBuffer);
            checkAndClose(read, channel);

            if (!filenameBuffer.hasRemaining()) {
                filenameBuffer.rewind();
                String filenameStr = new String(filenameBuffer.array());

                filename = new Filename();
                filename.setRelativeFilename(filenameStr);
                filename.setAbsoluteFilename(dataDir + filenameStr);
                cachedRequest.setFilename(filename);
            } else {
                filenameCache = filenameBuffer;
            }
        }
        return filename;
    }

    private long getFileLength(SocketChannel channel) throws Exception {
        long fileLength = cachedRequest.getFileLength();
        if (fileLength <= 0) {
            ByteBuffer fileLengthBuffer = Optional.ofNullable(fileLengthCache).orElse(ByteBuffer.allocate(8));
            int read = channel.read(fileLengthBuffer);
            checkAndClose(read, channel);

            if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                cachedRequest.setFileLength(fileLength);
            } else {
                fileLengthCache = fileLengthBuffer;
            }
        }
        return fileLength;
    }

    private ByteBuffer getFile(SocketChannel channel) throws IOException {
        ByteBuffer file = cachedRequest.getFile();
        if (ObjectUtil.isNull(file)) {
            ByteBuffer fileBuffer = Optional.ofNullable(fileCache).orElse(ByteBuffer.allocate(Math.toIntExact(cachedRequest.getFileLength())));
            int read = channel.read(fileBuffer);
            checkAndClose(read, channel);

            if (!fileBuffer.hasRemaining()) {
                fileBuffer.rewind();
                cachedRequest.setFile(fileBuffer);
                cachedRequest.setHasCompletedRead(true);
            } else {
                fileCache = fileBuffer;
            }
        }
        return file;
    }

    public boolean hasCompletedRead() {
        return cachedRequest.isHasCompletedRead();
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
    public static class CachedRequest {
        private String client;
        private int requestType;
        private Filename filename;
        private int filenameLength;
        private long fileLength;
        private ByteBuffer file;
        private boolean hasCompletedRead;
    }

    public CachedRequest getCachedRequest() {
        return cachedRequest;
    }
}
