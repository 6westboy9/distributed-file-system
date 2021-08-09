package com.westboy.namenode.server;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.westboy.namenode.conf.NameNodeConfig;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FSEditlogTest {

    private FSEditlog log;

    @Before
    public void before() {
        NameNodeConfig config = new NameNodeConfig();
        log = new FSEditlog(config);
    }

    @SneakyThrows
    @Test
    public void logEdit() {
        ExecutorService executorService = Executors.newFixedThreadPool(5, ThreadFactoryBuilder.create().setNamePrefix("EditLog-").build());
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            executorService.submit(() -> log.logEdit(EditlogFactory.create(String.valueOf(finalI))));
        }

        Thread.sleep(60 * 1000);

    }
}