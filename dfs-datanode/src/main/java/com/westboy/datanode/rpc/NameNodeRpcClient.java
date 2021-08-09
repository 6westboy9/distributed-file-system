package com.westboy.datanode.rpc;

import com.westboy.common.entity.FileInfo;
import com.westboy.common.entity.StorageInfo;
import com.westboy.namenode.rpc.model.HeartbeatResponse;
import com.westboy.namenode.rpc.model.RegisterResponse;
import com.westboy.namenode.rpc.model.ReportFileInfoResponse;
import com.westboy.namenode.rpc.model.ReportStorageInfoResponse;

public interface NameNodeRpcClient {

    RegisterResponse register();

    HeartbeatResponse heartbeat();

    ReportStorageInfoResponse reportStorageInfo(StorageInfo storageInfo);

    ReportFileInfoResponse reportFileInfo(FileInfo fileInfo);
}
