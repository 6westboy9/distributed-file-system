package com.westboy.namenode.server;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 负责 fsimage 文件上传的服务
 *
 * @author pengbo
 * @since 2021/2/4
 */
@Slf4j
public class FSImageUploadServer extends Thread {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private final String serverIp;
    private final int uploaderServerPort;
    private final int uploaderServerBackLog;
    private final String fsimageMetaFilePath;

    public FSImageUploadServer(NameNodeConfig config) {
        super("fsimage-upload-server");
        serverIp = config.getServerIp();
        uploaderServerPort = config.getUploaderServerPort();
        uploaderServerBackLog = config.getUploaderServerBackLog();
        fsimageMetaFilePath = config.getFsimageFilePath();
        init();
    }

    private void init() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(serverIp, uploaderServerPort), uploaderServerBackLog);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.info("启动 FSImageUploadServer 线程，并监听端口号：{}", uploaderServerPort);
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    handleRequest(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() throws IOException {
        if (ObjectUtil.isNotNull(serverSocketChannel)) {
            log.info("执行 FSImageUploadServer 关闭");
            serverSocketChannel.close();
            log.info("FSImageUploadServer 已关闭");
        }
    }

    private void handleRequest(SelectionKey key) {
        if (key.isAcceptable()) {
            handleConnectRequest(key);
        } else if (key.isReadable()) {
            handleReadableRequest(key);
        } else if (key.isWritable()) {
            handleWritableRequest(key);
        }
    }

    private void handleConnectRequest(SelectionKey key) {
        SocketChannel channel = null;
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            channel = serverSocketChannel.accept();
            channel.socket().setSoTimeout(30);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            IoUtil.close(channel);
            e.printStackTrace();
        }
    }

    private void handleReadableRequest(SelectionKey key) {
        SocketChannel channel = null;
        RandomAccessFile fsimageImageRAF = null;
        FileOutputStream fsimageOut = null;
        FileChannel fsimageFileChannel = null;

        try {
            channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            int total = 0;
            int count;
            if ((count = channel.read(buffer)) > 0) {
                total += count;
                FileUtil.del(fsimageMetaFilePath);

                fsimageImageRAF = new RandomAccessFile(fsimageMetaFilePath, GlobalConstant.RW_FILE_MODE);
                fsimageOut = new FileOutputStream(fsimageImageRAF.getFD());
                fsimageFileChannel = fsimageOut.getChannel();

                buffer.flip();
                fsimageFileChannel.write(buffer);
                buffer.clear();

                // 存在拆包问题，循环写入
                while ((count = channel.read(buffer)) > 0) {
                    total += count;
                    buffer.flip();
                    fsimageFileChannel.write(buffer);
                    buffer.clear();
                }
                fsimageFileChannel.force(false);
                channel.register(selector, SelectionKey.OP_WRITE);
                log.info("接收 fsimage 文件并写入本地磁盘完毕，共计大小：{}", total);
            }
        } catch (Exception e) {
            IoUtil.close(channel);
            e.printStackTrace();
        } finally {
            IoUtil.close(fsimageOut);
            IoUtil.close(fsimageImageRAF);
            IoUtil.close(fsimageFileChannel);
        }
    }


    private void handleWritableRequest(SelectionKey key) {
        SocketChannel channel = null;
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            buffer.put("SUCCESS".getBytes());
            buffer.flip();

            channel = (SocketChannel) key.channel();
            channel.write(buffer);

            log.info("发送 BackupNode 响应 fsimage 上传完毕");

            // 不需要关注 OP_READ 事件，因为客户端接收到响应后会关闭通道，所以在此，也是发送成功后，即关闭通道
            // 客户端通信时又会重新连接服务端
            // channel.register(selector, SelectionKey.OP_READ);
            channel.close();
        } catch (Exception e) {
            IoUtil.close(channel);
            e.printStackTrace();
        }
    }

}
