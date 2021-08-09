package com.westboy.common;

import java.util.Objects;


public class FSEditlogTxidRange {
    private final long startTxid;
    private final long endTxid;

    public FSEditlogTxidRange(long startTxid, long endTxid) {
        this.startTxid = startTxid;
        this.endTxid = endTxid;
    }

    public long getStartTxid() {
        return startTxid;
    }

    public long getEndTxid() {
        return endTxid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FSEditlogTxidRange range = (FSEditlogTxidRange) o;
        return startTxid == range.startTxid && endTxid == range.endTxid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTxid, endTxid);
    }

    @Override
    public String toString() {
        return "[startTxid=" + startTxid + ", endTxid=" + endTxid + ']';
    }
}
