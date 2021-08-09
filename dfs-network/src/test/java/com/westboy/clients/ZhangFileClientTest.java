package com.westboy.clients;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.westboy.common.utils.Utils;
import org.junit.Test;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Future;

public class ZhangFileClientTest {

    @Test
    public void upload() throws IOException {
        FileClientConfig config = new FileClientConfig();
        ZhangFileClient fileClient = new ZhangFileClient(config);

        String localPath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/dfs-network/tmp/59922f5866.jpg";

        if (!FileUtil.exist(localPath)) {
            throw new FileNotFoundException();
        }

        FileInputStream fileInputStream = new FileInputStream(localPath);
        FileChannel channel = fileInputStream.getChannel();

        // TODO 大小校验
        // channel.size()
        ByteBuffer buffer = ByteBuffer.allocate(Math.toIntExact(channel.size()));

        // 现在是小文件直接使用内存，后续考虑大文件
        // int read;
        // while ((read = channel.read(buffer)) > 0) {
        //     byte[] bytes = Utils.toArray(buffer, 0, read);
        // }

        channel.read(buffer);
        buffer.flip();

        byte[] fileBytes = buffer.array();

        String remotePath = "/usr/local/temp.jpg";
        Future<Result> upload = fileClient.upload(fileBytes, remotePath);
    }

}