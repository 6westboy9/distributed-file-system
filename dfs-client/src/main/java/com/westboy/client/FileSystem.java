package com.westboy.client;

import com.westboy.client.network.newversion.ResponseCallback;

/**
 * 作为文件系统的接口
 *
 * @author pengbo
 * @since 2021/1/30
 */
public interface FileSystem {

    /**
     * 创建目录
     *
     * @param path 目录对应的路径
     */
    boolean mkdir(String path);

    boolean createFile(String filename);

    void shutdown();

    @Deprecated
    void oldUpload(String localFilepath, String remoteFilepath) throws Exception;

    @Deprecated
    void oldUpload(byte[] file, String remoteFilepath) throws Exception;

    @Deprecated
    void oldDownload(String localFilepath, String remoteFilepath) throws Exception;

    @Deprecated
    byte[] oldDownload(String remoteFilepath) throws Exception;

    /* ---------------------------------- */

    void newAsyncUpload(String localFilepath, String remoteFilepath, ResponseCallback callback) throws Exception;

    void newAsyncUpload(byte[] file, String remoteFilepath, ResponseCallback callback);

    void newAsyncDownload(String localFilepath, String remoteFilepath, ResponseCallback callback) throws Exception;

    byte[] newAsyncDownload(String remoteFilepath, ResponseCallback callback) throws Exception;
}
