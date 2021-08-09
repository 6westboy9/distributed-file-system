package com.westboy.namenode.server;

/**
 * 生成 editlog 内容的工厂类
 */
public class EditlogFactory {

    public static String mkdir(String path) {
        return "{'OP':'MKDIR','PATH':'" + path + "'}";
    }

    public static String create(String filepath) {
        return "{'OP':'CREATE','PATH':'" + filepath + "'}";
    }
}
