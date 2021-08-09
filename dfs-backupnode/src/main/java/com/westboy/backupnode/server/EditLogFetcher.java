package com.westboy.backupnode.server;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.westboy.backupnode.config.BackupNodeConfig;
import com.westboy.namenode.rpc.model.FetchEditlogResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责从 NameNode 同步 editlog 文件
 *
 * @author pengbo
 * @since 2021/2/1
 */
@Slf4j
public class EditLogFetcher extends Thread {

    private final BackupNode backupNode;
    private final NameNodeRpcClient nameNodeRpcClient;
    private final FSNameSystem nameSystem;
    private long syncTxid = 0L;
    private final long fetchInterval;

    public EditLogFetcher(BackupNodeConfig config, BackupNode backupNode, FSNameSystem nameSystem, NameNodeRpcClient nameNodeRpcClient) {
        super("editlog-fetcher");
        fetchInterval = config.getFetchInterval();
        this.backupNode = backupNode;
        this.nameSystem = nameSystem;
        this.nameNodeRpcClient = nameNodeRpcClient;
    }

    @Override
    public void run() {
        log.info("启动 EditLogFetcher 拉取线程成功");
        while (backupNode.isRunning()) {
            try {

                if (!nameSystem.isFinishedRecover()) {
                    log.info("当前还没有完成元数据恢复，延迟进行 checkpoint 操作");
                    Thread.sleep(1000);
                    continue;
                }

                // 对于已经拉取过的不需要再进行拉取
                if (nameSystem.getSyncedTxid() > syncTxid) {
                    syncTxid = nameSystem.getSyncedTxid();
                    log.info("恢复需要同步的起始 syncTxid={}", syncTxid);
                }

                FetchEditlogResponse response = nameNodeRpcClient.fetchEditsLog(syncTxid);

                String editsLogJsonStr = response.getEditsLog();
                JSONArray editlogs = JSONUtil.parseArray(editsLogJsonStr);
                if (editlogs.size() == 0) {
                    log.debug("没有拉取到任何数据，等待 {} 秒后再次继续去拉取", fetchInterval / 1000);
                    Thread.sleep(fetchInterval);
                    continue;
                }

                log.info("拉取到的 {} 条数据", editlogs.size());

                for (int i = 0; i < editlogs.size(); i++) {
                    JSONObject editlog = editlogs.getJSONObject(i);
                    log.info("拉取详情：{}", editlog.toString());
                    String op = editlog.getStr("OP");
                    String path = editlog.getStr("PATH");
                    long txid = editlog.getLong("txid");
                    if ("MKDIR".equals(op)) {
                        boolean result = nameSystem.mkdir(txid, path);
                        log.info("创建目录成功，result={}，txid={}，path={}", result, txid, path);
                    } else if ("CREATE".equals(op)) {
                        boolean result = nameSystem.create(txid, path);
                        log.info("创建文件成功，result={}，txid={}，path={}", result, txid, path);
                    }
                    syncTxid = txid;
                }

                nameNodeRpcClient.setNameNodeRunning(true);
            } catch (InterruptedException e) {
                nameNodeRpcClient.setNameNodeRunning(false);
                e.printStackTrace();
            }
        }
    }
}
