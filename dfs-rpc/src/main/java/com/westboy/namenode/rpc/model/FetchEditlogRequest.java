// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.westboy.namenode.rpc.model;

/**
 * Protobuf type {@code com.westboy.namenode.rpc.FetchEditlogRequest}
 */
public final class FetchEditlogRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.westboy.namenode.rpc.FetchEditlogRequest)
    FetchEditlogRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use FetchEditlogRequest.newBuilder() to construct.
  private FetchEditlogRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private FetchEditlogRequest() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new FetchEditlogRequest();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private FetchEditlogRequest(
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
          case 16: {

            syncTxid_ = input.readInt64();
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
    return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_FetchEditlogRequest_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_FetchEditlogRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.westboy.namenode.rpc.model.FetchEditlogRequest.class, com.westboy.namenode.rpc.model.FetchEditlogRequest.Builder.class);
  }

  public static final int SYNCTXID_FIELD_NUMBER = 2;
  private long syncTxid_;
  /**
   * <code>int64 syncTxid = 2;</code>
   * @return The syncTxid.
   */
  @java.lang.Override
  public long getSyncTxid() {
    return syncTxid_;
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
    if (syncTxid_ != 0L) {
      output.writeInt64(2, syncTxid_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (syncTxid_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, syncTxid_);
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
    if (!(obj instanceof com.westboy.namenode.rpc.model.FetchEditlogRequest)) {
      return super.equals(obj);
    }
    com.westboy.namenode.rpc.model.FetchEditlogRequest other = (com.westboy.namenode.rpc.model.FetchEditlogRequest) obj;

    if (getSyncTxid()
        != other.getSyncTxid()) return false;
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
    hash = (37 * hash) + SYNCTXID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getSyncTxid());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.westboy.namenode.rpc.model.FetchEditlogRequest parseFrom(
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
  public static Builder newBuilder(com.westboy.namenode.rpc.model.FetchEditlogRequest prototype) {
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
   * Protobuf type {@code com.westboy.namenode.rpc.FetchEditlogRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.westboy.namenode.rpc.FetchEditlogRequest)
      com.westboy.namenode.rpc.model.FetchEditlogRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_FetchEditlogRequest_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_FetchEditlogRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.westboy.namenode.rpc.model.FetchEditlogRequest.class, com.westboy.namenode.rpc.model.FetchEditlogRequest.Builder.class);
    }

    // Construct using com.westboy.namenode.rpc.model.FetchEditlogRequest.newBuilder()
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
      syncTxid_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.westboy.namenode.rpc.model.NameNodeRpcModel.internal_static_com_westboy_namenode_rpc_FetchEditlogRequest_descriptor;
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.FetchEditlogRequest getDefaultInstanceForType() {
      return com.westboy.namenode.rpc.model.FetchEditlogRequest.getDefaultInstance();
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.FetchEditlogRequest build() {
      com.westboy.namenode.rpc.model.FetchEditlogRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.westboy.namenode.rpc.model.FetchEditlogRequest buildPartial() {
      com.westboy.namenode.rpc.model.FetchEditlogRequest result = new com.westboy.namenode.rpc.model.FetchEditlogRequest(this);
      result.syncTxid_ = syncTxid_;
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
      if (other instanceof com.westboy.namenode.rpc.model.FetchEditlogRequest) {
        return mergeFrom((com.westboy.namenode.rpc.model.FetchEditlogRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.westboy.namenode.rpc.model.FetchEditlogRequest other) {
      if (other == com.westboy.namenode.rpc.model.FetchEditlogRequest.getDefaultInstance()) return this;
      if (other.getSyncTxid() != 0L) {
        setSyncTxid(other.getSyncTxid());
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
      com.westboy.namenode.rpc.model.FetchEditlogRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.westboy.namenode.rpc.model.FetchEditlogRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long syncTxid_ ;
    /**
     * <code>int64 syncTxid = 2;</code>
     * @return The syncTxid.
     */
    @java.lang.Override
    public long getSyncTxid() {
      return syncTxid_;
    }
    /**
     * <code>int64 syncTxid = 2;</code>
     * @param value The syncTxid to set.
     * @return This builder for chaining.
     */
    public Builder setSyncTxid(long value) {
      
      syncTxid_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 syncTxid = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearSyncTxid() {
      
      syncTxid_ = 0L;
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


    // @@protoc_insertion_point(builder_scope:com.westboy.namenode.rpc.FetchEditlogRequest)
  }

  // @@protoc_insertion_point(class_scope:com.westboy.namenode.rpc.FetchEditlogRequest)
  private static final com.westboy.namenode.rpc.model.FetchEditlogRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.westboy.namenode.rpc.model.FetchEditlogRequest();
  }

  public static com.westboy.namenode.rpc.model.FetchEditlogRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<FetchEditlogRequest>
      PARSER = new com.google.protobuf.AbstractParser<FetchEditlogRequest>() {
    @java.lang.Override
    public FetchEditlogRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new FetchEditlogRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<FetchEditlogRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<FetchEditlogRequest> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.westboy.namenode.rpc.model.FetchEditlogRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

