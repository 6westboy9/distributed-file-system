package com.westboy.common;

import com.westboy.common.entity.Node;
import com.westboy.common.entity.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplicateReplicaTask {
    private FileInfo fileInfo;
    private Node sourceNode;
    private Node destNode;
}
