// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.westboy.namenode.rpc.model;

public interface ReportFileInfoRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.westboy.namenode.rpc.ReportFileInfoRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string datanodeInfo = 1;</code>
   * @return The datanodeInfo.
   */
  java.lang.String getDatanodeInfo();
  /**
   * <code>string datanodeInfo = 1;</code>
   * @return The bytes for datanodeInfo.
   */
  com.google.protobuf.ByteString
      getDatanodeInfoBytes();

  /**
   * <code>string fileInfo = 4;</code>
   * @return The fileInfo.
   */
  java.lang.String getFileInfo();
  /**
   * <code>string fileInfo = 4;</code>
   * @return The bytes for fileInfo.
   */
  com.google.protobuf.ByteString
      getFileInfoBytes();
}