package com.westboy.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用来描述 DataNode 的信息
 *
 * @author pengbo
 * @since 2021/1/30
 */
@Data
@AllArgsConstructor
public class Node {

    private String ip;
    private String hostname;
    private int port;

    public String getId() {
        return ip + "-" + hostname + "-" + port;
    }
}
