package com.westboy.datanode.network.newversion;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.common.constant.GlobalConstant;
import com.westboy.common.entity.FileInfo;
import com.westboy.datanode.rpc.NameNodeRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class IOThread extends Thread {

    public static final int REQUEST_SEND_FILE = 1;
    public static final int REQUEST_READ_FILE = 2;

    private NetworkRequestQueue requestQueue = NetworkRequestQueue.get();
    private final NameNodeRpcClient nameNodeRpcClient;

    public IOThread(NameNodeRpcClient nameNodeRpcClient) {
        this.nameNodeRpcClient = nameNodeRpcClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                NetworkRequest request = requestQueue.poll();
                if (ObjectUtil.isNull(request)) {
                    Thread.sleep(200);
                    continue;
                }

                int requestType = request.getCachedRequest().getRequestType();

                if (requestType == REQUEST_SEND_FILE) {
                    writeFileToLocalDisk(request);
                } else if (requestType == REQUEST_READ_FILE) {
                    readFileFromLocalDisk(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeFileToLocalDisk(NetworkRequest request) throws IOException {
        NetworkRequest.CachedRequest cachedRequest = request.getCachedRequest();
        NetworkRequest.Filename filename = cachedRequest.getFilename();

        String filepath = filename.getAbsoluteFilename();

        boolean exist = FileUtil.exist(filepath);
        if (!exist) {
            FileUtil.touch(filepath);
        }

        try (RandomAccessFile file = new RandomAccessFile(filepath, GlobalConstant.RW_FILE_MODE);
             FileChannel fileChannel = file.getChannel()) {

            if (exist) {
                // ????????????
                fileChannel.position(0);
            }

            // fileChannel.position(fileChannel.size());
            log.info("?????????????????????????????? position={}", fileChannel.size());

            ByteBuffer fileBuffer = cachedRequest.getFile();
            int write = fileChannel.write(fileBuffer);
            log.info("?????????????????????????????? {} ???????????????????????????????????????", write);

            reportFileInfo(cachedRequest);

            NetworkResponse response = new NetworkResponse();
            response.setDesc(NetworkResponse.UPLOAD);
            response.setClient(request.getClient());
            response.setBuffer(ByteBuffer.wrap("????????????????????????".getBytes()));
            NetworkResponseQueues.get().offer(request.getProcessorId(), response);
        }
    }

    private void reportFileInfo(NetworkRequest.CachedRequest cachedRequest) {
        NetworkRequest.Filename filename = cachedRequest.getFilename();
        // ???????????? Master ????????????????????????????????????????????? /image/product/iphone.jpg
        FileInfo fileInfo = new FileInfo(filename.getRelativeFilename(), cachedRequest.getFileLength());
        nameNodeRpcClient.reportFileInfo(fileInfo);
        log.info("??????????????????????????? {} ??? NameNode ??????", filename.getRelativeFilename());
    }


    private void readFileFromLocalDisk(NetworkRequest request) throws IOException {
        NetworkRequest.CachedRequest cachedRequest = request.getCachedRequest();
        NetworkRequest.Filename filename = cachedRequest.getFilename();


        try (RandomAccessFile file = new RandomAccessFile(filename.getAbsoluteFilename(), GlobalConstant.RW_FILE_MODE);
             FileChannel fileChannel = file.getChannel()) {

            long fileLength = file.length();

            ByteBuffer buffer = ByteBuffer.allocate(8 + (int) fileLength);
            buffer.putLong(fileLength); // 8

            int read = fileChannel.read(buffer);
            log.info("????????????????????????????????? {} ???????????????", read);

            buffer.rewind();

            NetworkResponse response = new NetworkResponse();
            response.setDesc(NetworkResponse.DOWNLOAD);
            response.setClient(request.getClient());
            response.setBuffer(buffer);

            NetworkResponseQueues.get().offer(request.getProcessorId(), response);
        }
    }
}
