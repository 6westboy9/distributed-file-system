package com.westboy.client;

import cn.hutool.core.util.RuntimeUtil;
import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.reporting.providers.HtmlReportGenerator;
import com.westboy.client.network.newversion.NetworkResponse;
import com.westboy.client.network.newversion.ResponseCallback;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class FileSystemPerformanceTest {

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule(new HtmlReportGenerator("data/report_test.html"));

    private final FileSystem fileSystem = new FileSystemImpl();

    @Test
    public void init() {
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/59922f5866.jpg";
    }


    @Test
    // threads: 使用的线程
    // durationMs: 测试持续时间
    // warmUpMs: 测试热身时间（ms）-- 热身时间的测试数据不会计算进最后的测试结果
    // maxExecutionsPerSecond: 方法执行的上限 -- RateLimiter，控制 TPS 上限
    @JUnitPerfTest(threads = 50, durationMs = 60000, warmUpMs = 1000)
    public void oldUpload() throws Exception {
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/upload/59922f5868.jpg";
        String remoteFilepath = "/image/59922f5868.jpg";
        fileSystem.oldUpload(localFilepath, remoteFilepath);
    }

    // private AtomicBoolean run = new AtomicBoolean(false);

    @Test
    // threads: 使用的线程
    // durationMs: 测试持续时间
    // warmUpMs: 测试热身时间（ms）-- 热身时间的测试数据不会计算进最后的测试结果
    // maxExecutionsPerSecond: 方法执行的上限 -- RateLimiter，控制 TPS 上限
    @JUnitPerfTest(threads = 20, durationMs = 10000, warmUpMs = 1000)
    public void newAsyncUpload() throws Exception {

        // if (!run.get()) {
        //     Thread.sleep(10 * 1000);
        //     run.set(true);
        // }

        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/59922f5866.jpg";
        String remoteFilepath = "/files/59922f5866.jpg";

        CountDownLatch latch = new CountDownLatch(1);

        fileSystem.newAsyncDownload(localFilepath, remoteFilepath, response -> {
            log.info("异步下载【{}】文件成功", remoteFilepath);
            latch.countDown();
        });

    }

    @Test
    // threads: 使用的线程
    // durationMs: 测试持续时间
    // warmUpMs: 测试热身时间（ms）-- 热身时间的测试数据不会计算进最后的测试结果
    // maxExecutionsPerSecond: 方法执行的上限 -- RateLimiter，控制 TPS 上限
    @JUnitPerfTest(threads = 20, durationMs = 10000, warmUpMs = 1000)
    public void oldDownload() throws Exception {
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/59922f5866.jpg";
        String remoteFilepath = "/files/59922f5866.jpg";
        fileSystem.oldDownload(localFilepath, remoteFilepath);
    }

    @Test
    // threads: 使用的线程
    // durationMs: 测试持续时间
    // warmUpMs: 测试热身时间（ms）-- 热身时间的测试数据不会计算进最后的测试结果
    // maxExecutionsPerSecond: 方法执行的上限 -- RateLimiter，控制 TPS 上限
    @JUnitPerfTest(threads = 20, durationMs = 10000, warmUpMs = 1000)
    public void newAsyncDownload() throws Exception {
        // 20KB
        // String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/59922f5866.jpg";
        // String remoteFilepath = "/files/59922f5866.jpg";

        // 1MB
        String localFilepath = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/client/download/10001.pdf";
        String remoteFilepath = "/files/10001.pdf";
        fileSystem.newAsyncDownload(localFilepath, remoteFilepath, response -> {
            log.info("异步下载【{}】文件成功", remoteFilepath);
        });
    }




}
