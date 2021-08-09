package com.westboy.clients;

public class FileEntity {

    private final byte[] fileBytes;
    private final byte[] pathBytes;

    public FileEntity(byte[] fileBytes, byte[] pathBytes) {
        this.fileBytes = fileBytes;
        this.pathBytes = pathBytes;
    }

    public long size() {
        // [上传路径名称长度 + 上传路径名称] + [文件大小 + 文件]
        int allLength = 4 + fileBytes.length + 4 + pathBytes.length;
        return 4 + allLength;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public byte[] getPathBytes() {
        return pathBytes;
    }
}
