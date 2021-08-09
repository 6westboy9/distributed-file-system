package com.westboy.client.network.newversion;


import com.westboy.common.entity.Node;
import com.westboy.common.entity.UploadFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.UUID;

@Slf4j
public class NewNioClient {

    private final NetworkManager networkManager;

    public NewNioClient() {
        this.networkManager = new NetworkManager();
    }

    /**
     * 发送一个文件过去
     */
    public Boolean sendFile(UploadFileInfo fileInfo, Node node, ResponseCallback callback) {
        if (!networkManager.maybeConnect(node)) {
            return false;
        }

        NetworkRequest request = createSendFileRequest(fileInfo, node, callback);
        networkManager.sendRequest(request);
        return true;
    }

    /**
     * 读取文件
     */
    public byte[] readFile(Node host, String filename, Boolean retry, ResponseCallback callback) throws Exception {
        if (!networkManager.maybeConnect(host)) {
            if (retry) {
                throw new Exception();
            }
        }

        NetworkRequest request = createReadFileRequest(host, filename, callback);
        networkManager.sendRequest(request);
        NetworkResponse response = networkManager.waitResponse(request.getId());
        if (response.getError()) {
            if (retry) {
                throw new Exception();
            }
        }

        return response.getBuffer().array();
    }

    /**
     * 构建一个发送文件的网络请求
     */
    private NetworkRequest createSendFileRequest(UploadFileInfo fileInfo, Node node, ResponseCallback callback) {
        NetworkRequest request = new NetworkRequest();


        ByteBuffer buffer = ByteBuffer.allocate(NetworkRequest.REQUEST_TYPE +
                NetworkRequest.FILENAME_LENGTH +
                fileInfo.getFileInfo().getFilename().getBytes().length +
                NetworkRequest.FILE_LENGTH +
                (int) fileInfo.getFileInfo().getFileLength());

        buffer.putInt(NetworkRequest.REQUEST_SEND_FILE);
        buffer.putInt(fileInfo.getFileInfo().getFilename().getBytes().length);
        buffer.put(fileInfo.getFileInfo().getFilename().getBytes());
        buffer.putLong(fileInfo.getFileInfo().getFileLength());
        buffer.put(fileInfo.getFile());
        buffer.rewind();

        request.setId(UUID.randomUUID().toString());
        request.setNode(node);
        request.setRequestType(NetworkRequest.REQUEST_SEND_FILE);
        request.setBuffer(buffer);
        request.setNeedResponse(false);
        request.setCallback(callback);
        return request;
    }

    /**
     * 构建一个发送文件的网络请求
     */
    private NetworkRequest createReadFileRequest(Node node, String filename, ResponseCallback callback) {
        NetworkRequest request = new NetworkRequest();

        byte[] filenameBytes = filename.getBytes();

        ByteBuffer buffer = ByteBuffer.allocate(
                NetworkRequest.REQUEST_TYPE +
                        NetworkRequest.FILENAME_LENGTH +
                        filenameBytes.length);
        buffer.putInt(NetworkRequest.REQUEST_READ_FILE);
        buffer.putInt(filenameBytes.length);
        buffer.put(filenameBytes);
        buffer.rewind();

        request.setId(UUID.randomUUID().toString());
        request.setNode(node);
        request.setRequestType(NetworkRequest.REQUEST_READ_FILE);
        request.setBuffer(buffer);
        request.setNeedResponse(true);
        request.setCallback(callback);
        return request;
    }

}
