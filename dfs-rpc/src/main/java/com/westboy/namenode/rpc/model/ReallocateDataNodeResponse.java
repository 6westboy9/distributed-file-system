// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.westboy.namenode.rpc.model;

/**
 * Protobuf type {@code com.westboy.namenode.rpc.ReallocateDataNodeResponse}
 */
public final class ReallocateDataNodeResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.westboy.namenode.rpc.ReallocateDataNodeResponse)
    ReallocateDataNodeResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ReallocateDataNodeResponse.newBuilder() to construct.
  private ReallocateDataNodeResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ReallocateDataNodeResponse() {
    datanodeInfo_ = "";
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new ReallocateDataNodeResponse();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ReallocateDataNodeResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            status_ = input.readInt32();
            break;
          }
          case 18: {
            java.lang.String s = input.readStringRequireUtf8();

            datanodeInfo_ = s;
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_ReallocateDataNodeResponse_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_ReallocateDataNodeResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.class, com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.Builder.class);
  }

  public static final int STATUS_FIELD_NUMBER = 1;
  private int status_;
  /**
   * <code>int32 status = 1;</code>
   * @return The status.
   */
  @java.lang.Override
  public int getStatus() {
    return status_;
  }

  public static final int DATANODEINFO_FIELD_NUMBER = 2;
  private volatile java.lang.Object datanodeInfo_;
  /**
   * <code>string datanodeInfo = 2;</code>
   * @return The datanodeInfo.
   */
  @java.lang.Override
  public java.lang.String getDatanodeInfo() {
    java.lang.Object ref = datanodeInfo_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      datanodeInfo_ = s;
      return s;
    }
  }
  /**
   * <code>string datanodeInfo = 2;</code>
   * @return The bytes for datanodeInfo.
   */
  @java.lang.Override
  public com.google.protobuf.ByteString
      getDatanodeInfoBytes() {
    java.lang.Object ref = datanodeInfo_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      datanodeInfo_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (status_ != 0) {
      output.writeInt32(1, status_);
    }
    if (!getDatanodeInfoBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, datanodeInfo_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (status_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, status_);
    }
    if (!getDatanodeInfoBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, datanodeInfo_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.westboy.namenode.rpc.model.ReallocateDataNodeResponse)) {
      return super.equals(obj);
    }
    com.westboy.namenode.rpc.model.ReallocateDataNodeResponse other = (com.westboy.namenode.rpc.model.ReallocateDataNodeResponse) obj;

    if (getStatus()
        != other.getStatus()) return false;
    if (!getDatanodeInfo()
        .equals(other.getDatanodeInfo())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + STATUS_FIELD_NUMBER;
    hash = (53 * hash) + getStatus();
    hash = (37 * hash) + DATANODEINFO_FIELD_NUMBER;
    hash = (53 * hash) + getDatanodeInfo().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.westboy.namenode.rpc.model.ReallocateDataNodeResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.westboy.namenode.rpc.ReallocateDataNodeResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.westboy.namenode.rpc.ReallocateDataNodeResponse)
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_ReallocateDataNodeResponse_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_ReallocateDataNodeResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.class, com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.Builder.class);
    }

    // Construct using com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      status_ = 0;

      datanodeInfo_ = "";

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_ReallocateDataNodeResponse_descriptor;
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.ReallocateDataNodeResponse getDefaultInstanceForType() {
      return com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.getDefaultInstance();
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.ReallocateDataNodeResponse build() {
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.ReallocateDataNodeResponse buildPartial() {
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponse result = new com.westboy.namenode.rpc.model.ReallocateDataNodeResponse(this);
      result.status_ = status_;
      result.datanodeInfo_ = datanodeInfo_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.westboy.namenode.rpc.model.ReallocateDataNodeResponse) {
        return mergeFrom((com.westboy.namenode.rpc.model.ReallocateDataNodeResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.westboy.namenode.rpc.model.ReallocateDataNodeResponse other) {
      if (other == com.westboy.namenode.rpc.model.ReallocateDataNodeResponse.getDefaultInstance()) return this;
      if (other.getStatus() != 0) {
        setStatus(other.getStatus());
      }
      if (!other.getDatanodeInfo().isEmpty()) {
        datanodeInfo_ = other.datanodeInfo_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.westboy.namenode.rpc.model.ReallocateDataNodeResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.westboy.namenode.rpc.model.ReallocateDataNodeResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private int status_ ;
    /**
     * <code>int32 status = 1;</code>
     * @return The status.
     */
    @java.lang.Override
    public int getStatus() {
      return status_;
    }
    /**
     * <code>int32 status = 1;</code>
     * @param value The status to set.
     * @return This builder for chaining.
     */
    public Builder setStatus(int value) {
      
      status_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 status = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearStatus() {
      
      status_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object datanodeInfo_ = "";
    /**
     * <code>string datanodeInfo = 2;</code>
     * @return The datanodeInfo.
     */
    public java.lang.String getDatanodeInfo() {
      java.lang.Object ref = datanodeInfo_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        datanodeInfo_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string datanodeInfo = 2;</code>
     * @return The bytes for datanodeInfo.
     */
    public com.google.protobuf.ByteString
        getDatanodeInfoBytes() {
      java.lang.Object ref = datanodeInfo_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        datanodeInfo_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string datanodeInfo = 2;</code>
     * @param value The datanodeInfo to set.
     * @return This builder for chaining.
     */
    public Builder setDatanodeInfo(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      datanodeInfo_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string datanodeInfo = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearDatanodeInfo() {
      
      datanodeInfo_ = getDefaultInstance().getDatanodeInfo();
      onChanged();
      return this;
    }
    /**
     * <code>string datanodeInfo = 2;</code>
     * @param value The bytes for datanodeInfo to set.
     * @return This builder for chaining.
     */
    public Builder setDatanodeInfoBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      datanodeInfo_ = value;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.westboy.namenode.rpc.ReallocateDataNodeResponse)
  }

  // @@protoc_insertion_point(class_scope:com.westboy.namenode.rpc.ReallocateDataNodeResponse)
  private static final com.westboy.namenode.rpc.model.ReallocateDataNodeResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.westboy.namenode.rpc.model.ReallocateDataNodeResponse();
  }

  public static com.westboy.namenode.rpc.model.ReallocateDataNodeResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ReallocateDataNodeResponse>
      PARSER = new com.google.protobuf.AbstractParser<ReallocateDataNodeResponse>() {
    @java.lang.Override
    public ReallocateDataNodeResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ReallocateDataNodeResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ReallocateDataNodeResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<ReallocateDataNodeResponse> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.westboy.namenode.rpc.model.ReallocateDataNodeResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

