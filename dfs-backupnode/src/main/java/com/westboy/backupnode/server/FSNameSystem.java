package com.westboy.backupnode.server;

public interface FSNameSystem {

    boolean mkdir(long txid, String path);

    boolean create(long txid, String filepath);

    void recover();

    boolean isFinishedRecover();

    void setSyncedTxid(long syncedTxid);

    long getSyncedTxid();

    FSImage getFSImage(boolean checkout);
}
