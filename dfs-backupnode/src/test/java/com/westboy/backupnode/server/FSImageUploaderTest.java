package com.westboy.backupnode.server;

import com.westboy.backupnode.config.BackupNodeConfig;
import org.junit.Before;
import org.junit.Test;

/**
 * @author pengbo
 * @since 2021/2/5
 */
public class FSImageUploaderTest {

    private FSImageUploader fsImageUploader;

    @Before
    public void before() {
        BackupNodeConfig config = new BackupNodeConfig();
        String namenodeIp = config.getNamenodeServerIp();
        int uploaderPort = config.getNamenodeUploaderServerPort();

        FSImage image = new FSImage(1, "hello");
        fsImageUploader = new FSImageUploader(image, namenodeIp, uploaderPort);
    }

    @Test
    public void run() {
        fsImageUploader.run();
    }
}