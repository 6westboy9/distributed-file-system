// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcServer.proto

package com.westboy.namenode.rpc.service;

public final class NameNodeServer {
  private NameNodeServer() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027NameNodeRpcServer.proto\022\030com.westboy.n" +
      "amenode.rpc\032\026NameNodeRpcModel.proto2\277\013\n\017" +
      "NameNodeService\022a\n\010register\022).com.westbo" +
      "y.namenode.rpc.RegisterRequest\032*.com.wes" +
      "tboy.namenode.rpc.RegisterResponse\022d\n\the" +
      "artbeat\022*.com.westboy.namenode.rpc.Heart" +
      "beatRequest\032+.com.westboy.namenode.rpc.H" +
      "eartbeatResponse\022X\n\005mkdir\022&.com.westboy." +
      "namenode.rpc.MkdirRequest\032\'.com.westboy." +
      "namenode.rpc.MkdirResponse\022a\n\010shutdown\022)" +
      ".com.westboy.namenode.rpc.ShutdownReques" +
      "t\032*.com.westboy.namenode.rpc.ShutdownRes" +
      "ponse\022m\n\014fetchEditlog\022-.com.westboy.name" +
      "node.rpc.FetchEditlogRequest\032..com.westb" +
      "oy.namenode.rpc.FetchEditlogResponse\022\205\001\n" +
      "\024updateCheckpointTxid\0225.com.westboy.name" +
      "node.rpc.UpdateCheckpointTxidRequest\0326.c" +
      "om.westboy.namenode.rpc.UpdateCheckpoint" +
      "TxidResponse\022c\n\006create\022+.com.westboy.nam" +
      "enode.rpc.CreateFileRequest\032,.com.westbo" +
      "y.namenode.rpc.CreateFileResponse\022{\n\020all" +
      "ocateDataNode\0222.com.westboy.namenode.rpc" +
      ".AllocateDataNodesRequest\0323.com.westboy." +
      "namenode.rpc.AllocateDataNodesResponse\022\177" +
      "\n\022reallocateDataNode\0223.com.westboy.namen" +
      "ode.rpc.ReallocateDataNodeRequest\0324.com." +
      "westboy.namenode.rpc.ReallocateDataNodeR" +
      "esponse\022|\n\021reportStorageInfo\0222.com.westb" +
      "oy.namenode.rpc.ReportStorageInfoRequest" +
      "\0323.com.westboy.namenode.rpc.ReportStorag" +
      "eInfoResponse\022s\n\016reportFileInfo\022/.com.we" +
      "stboy.namenode.rpc.ReportFileInfoRequest" +
      "\0320.com.westboy.namenode.rpc.ReportFileIn" +
      "foResponse\022s\n\016chooseDataNode\022/.com.westb" +
      "oy.namenode.rpc.ChooseDataNodeRequest\0320." +
      "com.westboy.namenode.rpc.ChooseDataNodeR" +
      "esponse\022d\n\trebalance\022*.com.westboy.namen" +
      "ode.rpc.RebalanceRequest\032+.com.westboy.n" +
      "amenode.rpc.RebalanceResponseB4\n com.wes" +
      "tboy.namenode.rpc.serviceB\016NameNodeServe" +
      "rP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.westboy.namenode.rpc.model.NameNodeRpcModel.getDescriptor(),
        });
    com.westboy.namenode.rpc.model.NameNodeRpcModel.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
