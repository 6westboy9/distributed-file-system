package com.westboy.client.network.oldversion;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.constant.RequestType;
import com.westboy.common.entity.UploadFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class OldNioClient {

    public void sendFile(String ip, int nioPort, UploadFileInfo uploadFileInfo) throws Exception {
        Selector selector = null;
        SocketChannel channel = null;
        ByteBuffer buffer = null;
        long fileSize = uploadFileInfo.getFileInfo().getFileLength();
        String filename = uploadFileInfo.getFileInfo().getFilename();
        byte[] file = uploadFileInfo.getFile();

        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(ip, nioPort));
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean sending = true;

            while (sending) {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            while (!channel.finishConnect()) {
                                Thread.sleep(100);
                            }
                        }

                        log.info("完成与服务端的连接的建立");

                        // 假设图片是 128kb，4byte + 4byte + 26byte + 8byte + 128kb，数据包的大小就是这样
                        // type | filenameLength | filename | fileSize | file
                        buffer = ByteBuffer.allocate(4 + 4 + filename.getBytes().length + 8 + (int) fileSize);
                        buffer.putInt(RequestType.SEND_FILE); // 占 4
                        buffer.putInt(filename.getBytes().length); // 占 4
                        buffer.put(filename.getBytes()); // 占 filename.getBytes().length
                        buffer.putLong(fileSize); // 占 8
                        buffer.put(file); // 占 fileSize

                        log.info("准备发送的数据包大小为：{}", buffer.capacity());
                        buffer.rewind();

                        int sent = channel.write(buffer);
                        log.info("已经发送了 {} 字节的数据到服务端去", sent);

                        if (buffer.hasRemaining()) {
                            log.info("本次数据包没有发送完毕，下次会继续发送");
                            key.interestOps(SelectionKey.OP_WRITE);
                        } else {
                            log.info("本次数据包发送完毕，准备读取服务端的响应");
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } else if (key.isWritable()) {
                        channel = (SocketChannel) key.channel();
                        int sent = channel.write(buffer);
                        log.info("上一次数据包没有发送完毕，本次继续发送了 {} 字节", sent);
                        if (ObjectUtil.isNotNull(buffer) && !buffer.hasRemaining()) {
                            log.info("本次数据包没有发送完毕，下次会继续发送");
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();
                        buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);
                        buffer.flip();

                        if (len > 0) {
                            log.info("收到服务端响应：{}", new String(buffer.array(), 0, len));
                            sending = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IoUtil.close(channel);
            IoUtil.close(selector);
        }
    }

    public byte[] readFile(String ip, int nioPort, String filename) throws Exception {
        ByteBuffer fileLengthBuffer = null;
        long fileLength = 0;
        ByteBuffer fileBuffer = null;
        byte[] file = null;

        SocketChannel channel = null;
        Selector selector = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(ip, nioPort));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean reading = true;

            while (reading) {
                selector.select();

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();

                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }

                        byte[] filenameBytes = filename.getBytes();
                        // int（4个字节）int（4个字节）文件名（N个字节）

                        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + filenameBytes.length);
                        buffer.putInt(RequestType.READ_FILE);
                        buffer.putInt(filenameBytes.length);
                        buffer.put(filenameBytes);
                        buffer.flip();

                        channel.write(buffer);
                        log.info("发送文件下载的请求过去");
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();

                        if (fileLength == 0L) {
                            if (ObjectUtil.isNull(fileLengthBuffer)) {
                                fileLengthBuffer = ByteBuffer.allocate(8);
                            }

                            channel.read(fileLengthBuffer);
                            if (!fileLengthBuffer.hasRemaining()) {
                                fileLengthBuffer.rewind();
                                fileLength = fileLengthBuffer.getLong();
                                log.info("从服务端返回数据中解析文件大小：{}", fileLength);
                            }
                        }

                        if (fileLength > 0L) {
                            if (ObjectUtil.isNull(fileBuffer)) {
                                fileBuffer = ByteBuffer.allocate((int) fileLength);
                            }
                            int hasRead = channel.read(fileBuffer);
                            log.info("从服务端读取了 {} 字节的数据出来到内存中", hasRead);

                            if (ObjectUtil.isNotNull(fileBuffer) && !fileBuffer.hasRemaining()) {
                                fileBuffer.rewind();
                                file = fileBuffer.array();
                                log.info("最终获取到的文件的大小为 {} 字节", file.length);
                                reading = false;
                            }
                        }
                    }
                }
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IoUtil.close(channel);
            IoUtil.close(selector);
        }
    }
}
