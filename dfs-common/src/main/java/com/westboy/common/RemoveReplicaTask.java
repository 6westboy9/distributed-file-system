package com.westboy.common;

import com.westboy.common.entity.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveReplicaTask {
    private String filename;
    private Node node;
}
