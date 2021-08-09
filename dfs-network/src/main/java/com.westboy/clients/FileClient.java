package com.westboy.clients;

import java.io.IOException;
import java.util.concurrent.Future;

public interface FileClient {

    Future<Result> upload(byte[] fileBytes, String remotePath) throws IOException;
    
    Future<Result> upload(byte[] fileBytes, String remotePath, Callback callback) throws IOException;

    Future<Result> download();

}
