package com.westboy.namenode.server;

import com.westboy.namenode.conf.NameNodeConfig;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author pengbo
 * @since 2021/2/5
 */
public class FSImageUploadServerTest {

    private FSImageUploadServer fsImageUploadServer;

    @Before
    public void before() {
        NameNodeConfig config = new NameNodeConfig();
        fsImageUploadServer = new FSImageUploadServer(config);
    }

    @Test
    public void run() {
        fsImageUploadServer.run();
    }

}