package com.westboy.common.requests;

public enum ApiKeys {

    UPLOAD_FILE((short) 0, "上传文件"),
    DOWNLOAD_FILE((short) 1, "下载文件"),
    ;

    private final short id;
    private final String name;

    ApiKeys(short id, String name) {
        this.id = id;
        this.name = name;
    }

}
