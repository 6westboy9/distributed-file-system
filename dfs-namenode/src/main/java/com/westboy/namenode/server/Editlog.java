package com.westboy.namenode.server;

import com.alibaba.fastjson.JSONObject;
import com.westboy.common.FSEditlogTxidRange;

import java.util.List;

/**
 * 代表一条 editlog
 *
 * @author pengbo
 * @since 2021/2/1
 */
public class Editlog {
    private final long txid;
    private final String content;

    public Editlog(long txid, String content) {
        this.txid = txid;

        JSONObject object = JSONObject.parseObject(content);
        object.put("txid", txid);
        this.content = object.toJSONString();
    }

    public long getTxid() {
        return txid;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "[txid= " + txid + ", content=" + content + "]";
    }
}
