// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.westboy.namenode.rpc.model;

public interface HeartbeatResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.westboy.namenode.rpc.HeartbeatResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 status = 1;</code>
   * @return The status.
   */
  int getStatus();

  /**
   * <code>string commands = 2;</code>
   * @return The commands.
   */
  java.lang.String getCommands();
  /**
   * <code>string commands = 2;</code>
   * @return The bytes for commands.
   */
  com.google.protobuf.ByteString
      getCommandsBytes();
}
