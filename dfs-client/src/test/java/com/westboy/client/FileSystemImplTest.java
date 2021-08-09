package com.westboy.client;

import com.westboy.client.network.newversion.NetworkResponse;
import com.westboy.client.network.newversion.ResponseCallback;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author pengbo
 * @since 2021/1/30
 */
@Slf4j
public class FileSystemImplTest {

    FileSystem fileSystem;

    @Before
    public void before() {
        fileSystem = new FileSystemImpl();
    }

    @Test
    public void mkdir() {
        fileSystem.mkdir("/usr/local/kafka/data1");
        // fileSystem.mkdir("/usr/local/kafka/data2");
        // fileSystem.mkdir("/usr/local/kafka/data3");
    }

    @Test
    public void multiThreadMkdir() throws InterruptedException {
        AtomicInteger count = new AtomicInteger(1);
        for (int i = 1; i <= 10; i++) {
            new Thread(() -> {
                for (int j = 1; j <= 10; j++) {
                    fileSystem.mkdir("/usr/local/kafka/data" + count.getAndIncrement());
                }
            }).start();
        }

        Thread.sleep(10 * 1000L);
        // fileSystem.shutdown();
    }

    @Test
    public void createFile() {
        fileSystem.createFile("/usr/local/kafka/img-01.jpg");
    }

    @Test
    public void shutdown() {
        fileSystem.shutdown();
    }

    @Test
    @Deprecated
    public void oldUpload() throws Exception {
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/59922f5868.jpg";
        String remoteFilepath = "/files/59922f5868.jpg";
        fileSystem.oldUpload(localFilepath, remoteFilepath);
    }

    @Test
    public void newUpload() throws Exception {
        String localFilepath1 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/10001.pdf";
        String localFilepath2 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/59922f5869.jpg";
        String localFilepath3 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/59922f5870.jpg";
        String remoteFilepath1 = "/files/10001.pdf";
        String remoteFilepath2 = "/files/59922f5869.jpg";
        String remoteFilepath3 = "/files/59922f5870.jpg";

        upload(localFilepath1, remoteFilepath1, 1);

        // String localFilepath4 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/10001.pdf";
        // String remoteFilepath4 = "/contracts/10001.pdf";
        // upload(localFilepath4, remoteFilepath4, 1);
    }

    private void upload(String localFilePath, String remoteFilePath, int count) throws Exception {
        long start = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            fileSystem.newAsyncUpload(localFilePath, remoteFilePath,
                    response -> {
                        log.info("异步上传【{}】文件成功", remoteFilePath);
                        latch.countDown();
                    }
            );
        }

        log.info("异步发送耗时：{}ms", (System.currentTimeMillis() - start));
        latch.await();
        log.info("异步接收耗时：{}ms", (System.currentTimeMillis() - start));
    }

    @Test
    @Deprecated
    public void oldDownload() throws Exception {
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/59922f5866.jpg";
        String remoteFilepath = "/files/59922f5866.jpg";
        fileSystem.oldDownload(localFilepath, remoteFilepath);
        // long start = System.currentTimeMillis();
        // CountDownLatch latch = new CountDownLatch(1000);
        // for (int i = 0; i < 1000; i++) {
        //     fileSystem.oldUpload(localFilepath, remoteFilepath);
        //     latch.countDown();
        // }
        //
        // latch.await();
        // log.info("耗时：{}ms", (System.currentTimeMillis() - start));

    }

    @Test
    public void newDownload() throws Exception {
        // String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/59922f5866.jpg";
        // String remoteFilepath = "/files/59922f5866.jpg";

        // 1MB
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/10001.pdf";
        String remoteFilepath = "/files/10001.pdf";

        CountDownLatch latch = new CountDownLatch(1);

        fileSystem.newAsyncDownload(localFilepath, remoteFilepath, new ResponseCallback() {
            @Override
            public void process(NetworkResponse response) {
                log.info("异步下载【{}】文件成功", remoteFilepath);
                latch.countDown();
            }
        });

        latch.await();
        // long start = System.currentTimeMillis();
        // CountDownLatch latch = new CountDownLatch(1000);
        // for (int i = 0; i < 1000; i++) {
        //     fileSystem.oldUpload(localFilepath, remoteFilepath);
        //     latch.countDown();
        // }
        //
        // latch.await();
        // log.info("耗时：{}ms", (System.currentTimeMillis() - start));

    }


}