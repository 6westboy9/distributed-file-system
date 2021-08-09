package com.westboy.namenode.rpc.service;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.35.0)",
    comments = "Source: NameNodeRpcServer.proto")
public final class NameNodeServiceGrpc {

  private NameNodeServiceGrpc() {}

  public static final String SERVICE_NAME = "com.westboy.namenode.rpc.NameNodeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RegisterRequest,
      com.westboy.namenode.rpc.model.RegisterResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "register",
      requestType = com.westboy.namenode.rpc.model.RegisterRequest.class,
      responseType = com.westboy.namenode.rpc.model.RegisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RegisterRequest,
      com.westboy.namenode.rpc.model.RegisterResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RegisterRequest, com.westboy.namenode.rpc.model.RegisterResponse> getRegisterMethod;
    if ((getRegisterMethod = NameNodeServiceGrpc.getRegisterMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getRegisterMethod = NameNodeServiceGrpc.getRegisterMethod) == null) {
          NameNodeServiceGrpc.getRegisterMethod = getRegisterMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.RegisterRequest, com.westboy.namenode.rpc.model.RegisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.RegisterResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("register"))
              .build();
        }
      }
    }
    return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.HeartbeatRequest,
      com.westboy.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "heartbeat",
      requestType = com.westboy.namenode.rpc.model.HeartbeatRequest.class,
      responseType = com.westboy.namenode.rpc.model.HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.HeartbeatRequest,
      com.westboy.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.HeartbeatRequest, com.westboy.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = NameNodeServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getHeartbeatMethod = NameNodeServiceGrpc.getHeartbeatMethod) == null) {
          NameNodeServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.HeartbeatRequest, com.westboy.namenode.rpc.model.HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.HeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.MkdirRequest,
      com.westboy.namenode.rpc.model.MkdirResponse> getMkdirMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "mkdir",
      requestType = com.westboy.namenode.rpc.model.MkdirRequest.class,
      responseType = com.westboy.namenode.rpc.model.MkdirResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.MkdirRequest,
      com.westboy.namenode.rpc.model.MkdirResponse> getMkdirMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.MkdirRequest, com.westboy.namenode.rpc.model.MkdirResponse> getMkdirMethod;
    if ((getMkdirMethod = NameNodeServiceGrpc.getMkdirMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getMkdirMethod = NameNodeServiceGrpc.getMkdirMethod) == null) {
          NameNodeServiceGrpc.getMkdirMethod = getMkdirMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.MkdirRequest, com.westboy.namenode.rpc.model.MkdirResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "mkdir"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.MkdirRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.MkdirResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("mkdir"))
              .build();
        }
      }
    }
    return getMkdirMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ShutdownRequest,
      com.westboy.namenode.rpc.model.ShutdownResponse> getShutdownMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "shutdown",
      requestType = com.westboy.namenode.rpc.model.ShutdownRequest.class,
      responseType = com.westboy.namenode.rpc.model.ShutdownResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ShutdownRequest,
      com.westboy.namenode.rpc.model.ShutdownResponse> getShutdownMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ShutdownRequest, com.westboy.namenode.rpc.model.ShutdownResponse> getShutdownMethod;
    if ((getShutdownMethod = NameNodeServiceGrpc.getShutdownMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getShutdownMethod = NameNodeServiceGrpc.getShutdownMethod) == null) {
          NameNodeServiceGrpc.getShutdownMethod = getShutdownMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.ShutdownRequest, com.westboy.namenode.rpc.model.ShutdownResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "shutdown"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ShutdownRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ShutdownResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("shutdown"))
              .build();
        }
      }
    }
    return getShutdownMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.FetchEditlogRequest,
      com.westboy.namenode.rpc.model.FetchEditlogResponse> getFetchEditlogMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fetchEditlog",
      requestType = com.westboy.namenode.rpc.model.FetchEditlogRequest.class,
      responseType = com.westboy.namenode.rpc.model.FetchEditlogResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.FetchEditlogRequest,
      com.westboy.namenode.rpc.model.FetchEditlogResponse> getFetchEditlogMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.FetchEditlogRequest, com.westboy.namenode.rpc.model.FetchEditlogResponse> getFetchEditlogMethod;
    if ((getFetchEditlogMethod = NameNodeServiceGrpc.getFetchEditlogMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getFetchEditlogMethod = NameNodeServiceGrpc.getFetchEditlogMethod) == null) {
          NameNodeServiceGrpc.getFetchEditlogMethod = getFetchEditlogMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.FetchEditlogRequest, com.westboy.namenode.rpc.model.FetchEditlogResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fetchEditlog"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.FetchEditlogRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.FetchEditlogResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("fetchEditlog"))
              .build();
        }
      }
    }
    return getFetchEditlogMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest,
      com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateCheckpointTxid",
      requestType = com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest.class,
      responseType = com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest,
      com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest, com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod;
    if ((getUpdateCheckpointTxidMethod = NameNodeServiceGrpc.getUpdateCheckpointTxidMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getUpdateCheckpointTxidMethod = NameNodeServiceGrpc.getUpdateCheckpointTxidMethod) == null) {
          NameNodeServiceGrpc.getUpdateCheckpointTxidMethod = getUpdateCheckpointTxidMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest, com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateCheckpointTxid"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("updateCheckpointTxid"))
              .build();
        }
      }
    }
    return getUpdateCheckpointTxidMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.CreateFileRequest,
      com.westboy.namenode.rpc.model.CreateFileResponse> getCreateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "create",
      requestType = com.westboy.namenode.rpc.model.CreateFileRequest.class,
      responseType = com.westboy.namenode.rpc.model.CreateFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.CreateFileRequest,
      com.westboy.namenode.rpc.model.CreateFileResponse> getCreateMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.CreateFileRequest, com.westboy.namenode.rpc.model.CreateFileResponse> getCreateMethod;
    if ((getCreateMethod = NameNodeServiceGrpc.getCreateMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getCreateMethod = NameNodeServiceGrpc.getCreateMethod) == null) {
          NameNodeServiceGrpc.getCreateMethod = getCreateMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.CreateFileRequest, com.westboy.namenode.rpc.model.CreateFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "create"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.CreateFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.CreateFileResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("create"))
              .build();
        }
      }
    }
    return getCreateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.AllocateDataNodesRequest,
      com.westboy.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "allocateDataNode",
      requestType = com.westboy.namenode.rpc.model.AllocateDataNodesRequest.class,
      responseType = com.westboy.namenode.rpc.model.AllocateDataNodesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.AllocateDataNodesRequest,
      com.westboy.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodeMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.AllocateDataNodesRequest, com.westboy.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodeMethod;
    if ((getAllocateDataNodeMethod = NameNodeServiceGrpc.getAllocateDataNodeMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getAllocateDataNodeMethod = NameNodeServiceGrpc.getAllocateDataNodeMethod) == null) {
          NameNodeServiceGrpc.getAllocateDataNodeMethod = getAllocateDataNodeMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.AllocateDataNodesRequest, com.westboy.namenode.rpc.model.AllocateDataNodesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "allocateDataNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.AllocateDataNodesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.AllocateDataNodesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("allocateDataNode"))
              .build();
        }
      }
    }
    return getAllocateDataNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReallocateDataNodeRequest,
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> getReallocateDataNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "reallocateDataNode",
      requestType = com.westboy.namenode.rpc.model.ReallocateDataNodeRequest.class,
      responseType = com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReallocateDataNodeRequest,
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> getReallocateDataNodeMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReallocateDataNodeRequest, com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> getReallocateDataNodeMethod;
    if ((getReallocateDataNodeMethod = NameNodeServiceGrpc.getReallocateDataNodeMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getReallocateDataNodeMethod = NameNodeServiceGrpc.getReallocateDataNodeMethod) == null) {
          NameNodeServiceGrpc.getReallocateDataNodeMethod = getReallocateDataNodeMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.ReallocateDataNodeRequest, com.westboy.namenode.rpc.model.ReallocateDataNodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "reallocateDataNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReallocateDataNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("reallocateDataNode"))
              .build();
        }
      }
    }
    return getReallocateDataNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportStorageInfoRequest,
      com.westboy.namenode.rpc.model.ReportStorageInfoResponse> getReportStorageInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "reportStorageInfo",
      requestType = com.westboy.namenode.rpc.model.ReportStorageInfoRequest.class,
      responseType = com.westboy.namenode.rpc.model.ReportStorageInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportStorageInfoRequest,
      com.westboy.namenode.rpc.model.ReportStorageInfoResponse> getReportStorageInfoMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportStorageInfoRequest, com.westboy.namenode.rpc.model.ReportStorageInfoResponse> getReportStorageInfoMethod;
    if ((getReportStorageInfoMethod = NameNodeServiceGrpc.getReportStorageInfoMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getReportStorageInfoMethod = NameNodeServiceGrpc.getReportStorageInfoMethod) == null) {
          NameNodeServiceGrpc.getReportStorageInfoMethod = getReportStorageInfoMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.ReportStorageInfoRequest, com.westboy.namenode.rpc.model.ReportStorageInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "reportStorageInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReportStorageInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReportStorageInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("reportStorageInfo"))
              .build();
        }
      }
    }
    return getReportStorageInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportFileInfoRequest,
      com.westboy.namenode.rpc.model.ReportFileInfoResponse> getReportFileInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "reportFileInfo",
      requestType = com.westboy.namenode.rpc.model.ReportFileInfoRequest.class,
      responseType = com.westboy.namenode.rpc.model.ReportFileInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportFileInfoRequest,
      com.westboy.namenode.rpc.model.ReportFileInfoResponse> getReportFileInfoMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ReportFileInfoRequest, com.westboy.namenode.rpc.model.ReportFileInfoResponse> getReportFileInfoMethod;
    if ((getReportFileInfoMethod = NameNodeServiceGrpc.getReportFileInfoMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getReportFileInfoMethod = NameNodeServiceGrpc.getReportFileInfoMethod) == null) {
          NameNodeServiceGrpc.getReportFileInfoMethod = getReportFileInfoMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.ReportFileInfoRequest, com.westboy.namenode.rpc.model.ReportFileInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "reportFileInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReportFileInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ReportFileInfoResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("reportFileInfo"))
              .build();
        }
      }
    }
    return getReportFileInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ChooseDataNodeRequest,
      com.westboy.namenode.rpc.model.ChooseDataNodeResponse> getChooseDataNodeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "chooseDataNode",
      requestType = com.westboy.namenode.rpc.model.ChooseDataNodeRequest.class,
      responseType = com.westboy.namenode.rpc.model.ChooseDataNodeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ChooseDataNodeRequest,
      com.westboy.namenode.rpc.model.ChooseDataNodeResponse> getChooseDataNodeMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.ChooseDataNodeRequest, com.westboy.namenode.rpc.model.ChooseDataNodeResponse> getChooseDataNodeMethod;
    if ((getChooseDataNodeMethod = NameNodeServiceGrpc.getChooseDataNodeMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getChooseDataNodeMethod = NameNodeServiceGrpc.getChooseDataNodeMethod) == null) {
          NameNodeServiceGrpc.getChooseDataNodeMethod = getChooseDataNodeMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.ChooseDataNodeRequest, com.westboy.namenode.rpc.model.ChooseDataNodeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "chooseDataNode"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ChooseDataNodeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.ChooseDataNodeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("chooseDataNode"))
              .build();
        }
      }
    }
    return getChooseDataNodeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RebalanceRequest,
      com.westboy.namenode.rpc.model.RebalanceResponse> getRebalanceMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "rebalance",
      requestType = com.westboy.namenode.rpc.model.RebalanceRequest.class,
      responseType = com.westboy.namenode.rpc.model.RebalanceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RebalanceRequest,
      com.westboy.namenode.rpc.model.RebalanceResponse> getRebalanceMethod() {
    io.grpc.MethodDescriptor<com.westboy.namenode.rpc.model.RebalanceRequest, com.westboy.namenode.rpc.model.RebalanceResponse> getRebalanceMethod;
    if ((getRebalanceMethod = NameNodeServiceGrpc.getRebalanceMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getRebalanceMethod = NameNodeServiceGrpc.getRebalanceMethod) == null) {
          NameNodeServiceGrpc.getRebalanceMethod = getRebalanceMethod =
              io.grpc.MethodDescriptor.<com.westboy.namenode.rpc.model.RebalanceRequest, com.westboy.namenode.rpc.model.RebalanceResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "rebalance"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.RebalanceRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.westboy.namenode.rpc.model.RebalanceResponse.getDefaultInstance()))
              .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("rebalance"))
              .build();
        }
      }
    }
    return getRebalanceMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NameNodeServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceStub>() {
        @java.lang.Override
        public NameNodeServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameNodeServiceStub(channel, callOptions);
        }
      };
    return NameNodeServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NameNodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceBlockingStub>() {
        @java.lang.Override
        public NameNodeServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameNodeServiceBlockingStub(channel, callOptions);
        }
      };
    return NameNodeServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NameNodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<NameNodeServiceFutureStub>() {
        @java.lang.Override
        public NameNodeServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new NameNodeServiceFutureStub(channel, callOptions);
        }
      };
    return NameNodeServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class NameNodeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void register(com.westboy.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RegisterResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    public void heartbeat(com.westboy.namenode.rpc.model.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }

    /**
     */
    public void mkdir(com.westboy.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.MkdirResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getMkdirMethod(), responseObserver);
    }

    /**
     */
    public void shutdown(com.westboy.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ShutdownResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShutdownMethod(), responseObserver);
    }

    /**
     */
    public void fetchEditlog(com.westboy.namenode.rpc.model.FetchEditlogRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.FetchEditlogResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFetchEditlogMethod(), responseObserver);
    }

    /**
     */
    public void updateCheckpointTxid(com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateCheckpointTxidMethod(), responseObserver);
    }

    /**
     */
    public void create(com.westboy.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.CreateFileResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateMethod(), responseObserver);
    }

    /**
     */
    public void allocateDataNode(com.westboy.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.AllocateDataNodesResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAllocateDataNodeMethod(), responseObserver);
    }

    /**
     */
    public void reallocateDataNode(com.westboy.namenode.rpc.model.ReallocateDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReallocateDataNodeMethod(), responseObserver);
    }

    /**
     */
    public void reportStorageInfo(com.westboy.namenode.rpc.model.ReportStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportStorageInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportStorageInfoMethod(), responseObserver);
    }

    /**
     */
    public void reportFileInfo(com.westboy.namenode.rpc.model.ReportFileInfoRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportFileInfoResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReportFileInfoMethod(), responseObserver);
    }

    /**
     */
    public void chooseDataNode(com.westboy.namenode.rpc.model.ChooseDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ChooseDataNodeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getChooseDataNodeMethod(), responseObserver);
    }

    /**
     */
    public void rebalance(com.westboy.namenode.rpc.model.RebalanceRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RebalanceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRebalanceMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.RegisterRequest,
                com.westboy.namenode.rpc.model.RegisterResponse>(
                  this, METHODID_REGISTER)))
          .addMethod(
            getHeartbeatMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.HeartbeatRequest,
                com.westboy.namenode.rpc.model.HeartbeatResponse>(
                  this, METHODID_HEARTBEAT)))
          .addMethod(
            getMkdirMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.MkdirRequest,
                com.westboy.namenode.rpc.model.MkdirResponse>(
                  this, METHODID_MKDIR)))
          .addMethod(
            getShutdownMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.ShutdownRequest,
                com.westboy.namenode.rpc.model.ShutdownResponse>(
                  this, METHODID_SHUTDOWN)))
          .addMethod(
            getFetchEditlogMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.FetchEditlogRequest,
                com.westboy.namenode.rpc.model.FetchEditlogResponse>(
                  this, METHODID_FETCH_EDITLOG)))
          .addMethod(
            getUpdateCheckpointTxidMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest,
                com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse>(
                  this, METHODID_UPDATE_CHECKPOINT_TXID)))
          .addMethod(
            getCreateMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.CreateFileRequest,
                com.westboy.namenode.rpc.model.CreateFileResponse>(
                  this, METHODID_CREATE)))
          .addMethod(
            getAllocateDataNodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.AllocateDataNodesRequest,
                com.westboy.namenode.rpc.model.AllocateDataNodesResponse>(
                  this, METHODID_ALLOCATE_DATA_NODE)))
          .addMethod(
            getReallocateDataNodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.ReallocateDataNodeRequest,
                com.westboy.namenode.rpc.model.ReallocateDataNodeResponse>(
                  this, METHODID_REALLOCATE_DATA_NODE)))
          .addMethod(
            getReportStorageInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.ReportStorageInfoRequest,
                com.westboy.namenode.rpc.model.ReportStorageInfoResponse>(
                  this, METHODID_REPORT_STORAGE_INFO)))
          .addMethod(
            getReportFileInfoMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.ReportFileInfoRequest,
                com.westboy.namenode.rpc.model.ReportFileInfoResponse>(
                  this, METHODID_REPORT_FILE_INFO)))
          .addMethod(
            getChooseDataNodeMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.ChooseDataNodeRequest,
                com.westboy.namenode.rpc.model.ChooseDataNodeResponse>(
                  this, METHODID_CHOOSE_DATA_NODE)))
          .addMethod(
            getRebalanceMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.westboy.namenode.rpc.model.RebalanceRequest,
                com.westboy.namenode.rpc.model.RebalanceResponse>(
                  this, METHODID_REBALANCE)))
          .build();
    }
  }

  /**
   */
  public static final class NameNodeServiceStub extends io.grpc.stub.AbstractAsyncStub<NameNodeServiceStub> {
    private NameNodeServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameNodeServiceStub(channel, callOptions);
    }

    /**
     */
    public void register(com.westboy.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RegisterResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(com.westboy.namenode.rpc.model.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.HeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void mkdir(com.westboy.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.MkdirResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getMkdirMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void shutdown(com.westboy.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ShutdownResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchEditlog(com.westboy.namenode.rpc.model.FetchEditlogRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.FetchEditlogResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFetchEditlogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateCheckpointTxid(com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateCheckpointTxidMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void create(com.westboy.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.CreateFileResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void allocateDataNode(com.westboy.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.AllocateDataNodesResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAllocateDataNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reallocateDataNode(com.westboy.namenode.rpc.model.ReallocateDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReallocateDataNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reportStorageInfo(com.westboy.namenode.rpc.model.ReportStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportStorageInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportStorageInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reportFileInfo(com.westboy.namenode.rpc.model.ReportFileInfoRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportFileInfoResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getReportFileInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void chooseDataNode(com.westboy.namenode.rpc.model.ChooseDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ChooseDataNodeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getChooseDataNodeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void rebalance(com.westboy.namenode.rpc.model.RebalanceRequest request,
        io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RebalanceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRebalanceMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NameNodeServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<NameNodeServiceBlockingStub> {
    private NameNodeServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameNodeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.RegisterResponse register(com.westboy.namenode.rpc.model.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.HeartbeatResponse heartbeat(com.westboy.namenode.rpc.model.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.MkdirResponse mkdir(com.westboy.namenode.rpc.model.MkdirRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getMkdirMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.ShutdownResponse shutdown(com.westboy.namenode.rpc.model.ShutdownRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getShutdownMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.FetchEditlogResponse fetchEditlog(com.westboy.namenode.rpc.model.FetchEditlogRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFetchEditlogMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse updateCheckpointTxid(com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateCheckpointTxidMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.CreateFileResponse create(com.westboy.namenode.rpc.model.CreateFileRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.AllocateDataNodesResponse allocateDataNode(com.westboy.namenode.rpc.model.AllocateDataNodesRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAllocateDataNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.ReallocateDataNodeResponse reallocateDataNode(com.westboy.namenode.rpc.model.ReallocateDataNodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReallocateDataNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.ReportStorageInfoResponse reportStorageInfo(com.westboy.namenode.rpc.model.ReportStorageInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportStorageInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.ReportFileInfoResponse reportFileInfo(com.westboy.namenode.rpc.model.ReportFileInfoRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getReportFileInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.ChooseDataNodeResponse chooseDataNode(com.westboy.namenode.rpc.model.ChooseDataNodeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getChooseDataNodeMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.westboy.namenode.rpc.model.RebalanceResponse rebalance(com.westboy.namenode.rpc.model.RebalanceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRebalanceMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NameNodeServiceFutureStub extends io.grpc.stub.AbstractFutureStub<NameNodeServiceFutureStub> {
    private NameNodeServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new NameNodeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.RegisterResponse> register(
        com.westboy.namenode.rpc.model.RegisterRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.HeartbeatResponse> heartbeat(
        com.westboy.namenode.rpc.model.HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.MkdirResponse> mkdir(
        com.westboy.namenode.rpc.model.MkdirRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getMkdirMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.ShutdownResponse> shutdown(
        com.westboy.namenode.rpc.model.ShutdownRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.FetchEditlogResponse> fetchEditlog(
        com.westboy.namenode.rpc.model.FetchEditlogRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFetchEditlogMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse> updateCheckpointTxid(
        com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateCheckpointTxidMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.CreateFileResponse> create(
        com.westboy.namenode.rpc.model.CreateFileRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.AllocateDataNodesResponse> allocateDataNode(
        com.westboy.namenode.rpc.model.AllocateDataNodesRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAllocateDataNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.ReallocateDataNodeResponse> reallocateDataNode(
        com.westboy.namenode.rpc.model.ReallocateDataNodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReallocateDataNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.ReportStorageInfoResponse> reportStorageInfo(
        com.westboy.namenode.rpc.model.ReportStorageInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportStorageInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.ReportFileInfoResponse> reportFileInfo(
        com.westboy.namenode.rpc.model.ReportFileInfoRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getReportFileInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.ChooseDataNodeResponse> chooseDataNode(
        com.westboy.namenode.rpc.model.ChooseDataNodeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getChooseDataNodeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.westboy.namenode.rpc.model.RebalanceResponse> rebalance(
        com.westboy.namenode.rpc.model.RebalanceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRebalanceMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_HEARTBEAT = 1;
  private static final int METHODID_MKDIR = 2;
  private static final int METHODID_SHUTDOWN = 3;
  private static final int METHODID_FETCH_EDITLOG = 4;
  private static final int METHODID_UPDATE_CHECKPOINT_TXID = 5;
  private static final int METHODID_CREATE = 6;
  private static final int METHODID_ALLOCATE_DATA_NODE = 7;
  private static final int METHODID_REALLOCATE_DATA_NODE = 8;
  private static final int METHODID_REPORT_STORAGE_INFO = 9;
  private static final int METHODID_REPORT_FILE_INFO = 10;
  private static final int METHODID_CHOOSE_DATA_NODE = 11;
  private static final int METHODID_REBALANCE = 12;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NameNodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NameNodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((com.westboy.namenode.rpc.model.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RegisterResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.westboy.namenode.rpc.model.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.HeartbeatResponse>) responseObserver);
          break;
        case METHODID_MKDIR:
          serviceImpl.mkdir((com.westboy.namenode.rpc.model.MkdirRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.MkdirResponse>) responseObserver);
          break;
        case METHODID_SHUTDOWN:
          serviceImpl.shutdown((com.westboy.namenode.rpc.model.ShutdownRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ShutdownResponse>) responseObserver);
          break;
        case METHODID_FETCH_EDITLOG:
          serviceImpl.fetchEditlog((com.westboy.namenode.rpc.model.FetchEditlogRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.FetchEditlogResponse>) responseObserver);
          break;
        case METHODID_UPDATE_CHECKPOINT_TXID:
          serviceImpl.updateCheckpointTxid((com.westboy.namenode.rpc.model.UpdateCheckpointTxidRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.UpdateCheckpointTxidResponse>) responseObserver);
          break;
        case METHODID_CREATE:
          serviceImpl.create((com.westboy.namenode.rpc.model.CreateFileRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.CreateFileResponse>) responseObserver);
          break;
        case METHODID_ALLOCATE_DATA_NODE:
          serviceImpl.allocateDataNode((com.westboy.namenode.rpc.model.AllocateDataNodesRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.AllocateDataNodesResponse>) responseObserver);
          break;
        case METHODID_REALLOCATE_DATA_NODE:
          serviceImpl.reallocateDataNode((com.westboy.namenode.rpc.model.ReallocateDataNodeRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReallocateDataNodeResponse>) responseObserver);
          break;
        case METHODID_REPORT_STORAGE_INFO:
          serviceImpl.reportStorageInfo((com.westboy.namenode.rpc.model.ReportStorageInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportStorageInfoResponse>) responseObserver);
          break;
        case METHODID_REPORT_FILE_INFO:
          serviceImpl.reportFileInfo((com.westboy.namenode.rpc.model.ReportFileInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ReportFileInfoResponse>) responseObserver);
          break;
        case METHODID_CHOOSE_DATA_NODE:
          serviceImpl.chooseDataNode((com.westboy.namenode.rpc.model.ChooseDataNodeRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.ChooseDataNodeResponse>) responseObserver);
          break;
        case METHODID_REBALANCE:
          serviceImpl.rebalance((com.westboy.namenode.rpc.model.RebalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.westboy.namenode.rpc.model.RebalanceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NameNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NameNodeServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.westboy.namenode.rpc.service.NameNodeServer.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NameNodeService");
    }
  }

  private static final class NameNodeServiceFileDescriptorSupplier
      extends NameNodeServiceBaseDescriptorSupplier {
    NameNodeServiceFileDescriptorSupplier() {}
  }

  private static final class NameNodeServiceMethodDescriptorSupplier
      extends NameNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NameNodeServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NameNodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NameNodeServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .addMethod(getHeartbeatMethod())
              .addMethod(getMkdirMethod())
              .addMethod(getShutdownMethod())
              .addMethod(getFetchEditlogMethod())
              .addMethod(getUpdateCheckpointTxidMethod())
              .addMethod(getCreateMethod())
              .addMethod(getAllocateDataNodeMethod())
              .addMethod(getReallocateDataNodeMethod())
              .addMethod(getReportStorageInfoMethod())
              .addMethod(getReportFileInfoMethod())
              .addMethod(getChooseDataNodeMethod())
              .addMethod(getRebalanceMethod())
              .build();
        }
      }
    }
    return result;
  }
}
