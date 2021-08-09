package com.westboy.namenode.server;

import com.westboy.namenode.conf.NameNodeConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pengbo
 * @since 2021/2/4
 */
public class FSNameSystemImplTest {

    private FSNameSystem nameSystem;

    @Before
    public void before() {
        NameNodeConfig config = new NameNodeConfig();
        DataNodeManager dataNodeManager = new DataNodeManager(config);
        nameSystem = new FSNameSystemImpl(config, dataNodeManager);
    }

    @Test
    public void mkdir() {
        nameSystem.mkdir("/path/kafka/test");
    }

    @Test
    public void create() {
        nameSystem.create("/path/kafka/test.txt");
    }

    @Test
    public void flush() {
    }

    @Test
    public void getEditsLog() {
    }

    @Test
    public void test() {
        ((FSNameSystemImpl) nameSystem).loadEditLog();
    }
}