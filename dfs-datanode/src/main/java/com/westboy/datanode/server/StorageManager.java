package com.westboy.datanode.server;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.westboy.common.entity.StorageInfo;
import com.westboy.datanode.config.DataNodeConfig;

import java.io.File;
import java.util.List;

public class StorageManager {

    private final StorageInfo storageInfo;
    private final String dataDir;

    public StorageManager(DataNodeConfig config) {
        dataDir = config.getDataDir();
        storageInfo = new StorageInfo();
    }

    public StorageInfo getStorageInfo() {
        List<File> files = FileUtil.loopFiles(dataDir);
        if (CollUtil.isEmpty(files)) {
            return storageInfo;
        }

        for(File file : files) {
            String absolutePath = file.getAbsolutePath();
            String filepath = absolutePath.substring(dataDir.length());
            storageInfo.addFile(filepath, file.length());
            storageInfo.addDataSize(file.length());
        }

        return storageInfo;
    }

}
