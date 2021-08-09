package com.westboy.backupnode.server;

import com.westboy.namenode.rpc.model.FetchEditlogResponse;
import com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse;

public interface NameNodeRpcClient {

    FetchEditlogResponse fetchEditsLog(long syncTxid);

    UpdateCheckpointTxidResponse updateCheckpointTxid(long txid);

    boolean isNameNodeRunning();

    void setNameNodeRunning(boolean running);
}
