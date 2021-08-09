package com.westboy.datanode.server;

import com.westboy.common.entity.StorageInfo;
import com.westboy.datanode.config.DataNodeConfig;
import org.junit.Before;
import org.junit.Test;

public class StorageManagerTest {

    private StorageManager storageManager;

    @Before
    public void before() {
        DataNodeConfig config = new DataNodeConfig();
        storageManager = new StorageManager(config);
    }

    @Test
    public void getStorageInfo() {
        StorageInfo info = storageManager.getStorageInfo();
        String str1 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/datanode-2/image/59922f5866.jpg";
        String str2 = "/Users/westboy/IdeaProjects/shishan/distributed-file-system/data/datanode-2";

        System.out.println(str1.substring(str2.length()));
    }
}