package com.westboy.common.record;

public interface Records {

    // [上传路径名称长度 + 上传路径名称] + [文件名称长度 + 文件名称] + [文件大小 + 文件]
    int OFFSET_OFFSET = 0;
    int OFFSET_LENGTH = 2; // 2 个字节
}
