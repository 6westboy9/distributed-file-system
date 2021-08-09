package com.westboy.backupnode.server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FSImage {
    private long maxTxid;
    private String fsimageJson;
}
