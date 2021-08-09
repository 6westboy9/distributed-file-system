package com.westboy.datanode.network.newversion;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class NetworkResponse {
    public static final String UPLOAD = "客户端上传文件";
    public static final String DOWNLOAD = "客户端下载文件";

    private String desc;
    private String Client;
    private ByteBuffer buffer;
}
