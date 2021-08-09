package com.westboy.backupnode.server;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 负责上传 fsimage 到 NameNode 线程任务
 *
 * @author pengbo
 * @since 2021/2/4
 */
@Slf4j
public class FSImageUploader extends Thread {

    private final FSImage fsImage;
    private final String namenodeIp;
    private final int uploaderPort;

    public FSImageUploader(FSImage fsImage, String namenodeIp, int uploaderPort) {
        super("fsimage-uploader");
        this.fsImage = fsImage;
        this.namenodeIp = namenodeIp;
        this.uploaderPort = uploaderPort;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        Selector selector = null;
        try {
            // 每次轮询时重新建立连接
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(namenodeIp, uploaderPort));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean uploading = true;
            while (uploading) {
                // 阻塞等待
                selector.select();
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();
                    channel = (SocketChannel) key.channel();

                    if (key.isConnectable() && channel.isConnectionPending() && channel.finishConnect()) {
                        ByteBuffer buffer = ByteBuffer.wrap(fsImage.getFsimageJson().getBytes());
                        log.info("准备上传 fsimage 文件数据，上传字节数：{}", buffer.capacity());
                        channel.write(buffer);
                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                        int count = channel.read(buffer);
                        if (count > 0) {
                            log.info("上传 fsimage 文件成功，响应消息：{}", new String(buffer.array(), 0, count));
                            channel.close();
                            uploading = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(channel);
            IoUtil.close(selector);
        }
    }
}
