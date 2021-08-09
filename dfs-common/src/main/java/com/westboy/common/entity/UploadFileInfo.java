package com.westboy.common.entity;

import lombok.Data;

@Data
public class UploadFileInfo {
    private FileInfo fileInfo;
    private String remoteFilepath;
    private byte[] file;

    public UploadFileInfo(FileInfo fileInfo, String remoteFilepath, byte[] file) {
        this.fileInfo = fileInfo;
        this.remoteFilepath = remoteFilepath;
        this.file = file;
    }
}
