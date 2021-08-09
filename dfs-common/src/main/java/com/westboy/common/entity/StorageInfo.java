package com.westboy.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StorageInfo implements Serializable {
    private static final long serialVersionUID = -2667374222672528440L;
    private List<FileInfo> files;
    private long storedDataSize;

    public StorageInfo() {
        files = new ArrayList<>();
        storedDataSize = 0;
    }

    public void addFile(String filename, long length) {
        files.add(new FileInfo(filename, length));
    }


    public void addDataSize(long length) {
        storedDataSize += length;
    }
}
