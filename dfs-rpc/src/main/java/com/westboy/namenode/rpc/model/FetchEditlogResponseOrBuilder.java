// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.westboy.namenode.rpc.model;

public interface FetchEditlogResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.westboy.namenode.rpc.FetchEditlogResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 status = 1;</code>
   * @return The status.
   */
  int getStatus();

  /**
   * <code>string editsLog = 2;</code>
   * @return The editsLog.
   */
  java.lang.String getEditsLog();
  /**
   * <code>string editsLog = 2;</code>
   * @return The bytes for editsLog.
   */
  com.google.protobuf.ByteString
      getEditsLogBytes();
}
