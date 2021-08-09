package com.westboy.namenode.server;

import lombok.Data;

@Data
public class Command {
    // 重新注册
    public static final Integer REGISTER = 1;
    // 上报全量文件存储信息
    public static final Integer REPORT_STORAGE_INFO = 2;
    // 复制文件副本
    public static final Integer REPLICATE_REPLICA = 3;
    // 移除文件副本
    public static final Integer REMOVE_REPLICA = 4;

    private Integer type;
    private String content;

    public Command(Integer type) {
        this.type = type;
    }

    public Command(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
