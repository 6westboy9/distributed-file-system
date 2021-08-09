package com.westboy.common.requests;

import java.nio.ByteBuffer;

public class RequestHeader {

    private static final String API_KEY_FIELD_NAME = "api_key";
    private static final String API_VERSION_FIELD_NAME = "api_version";
    private static final String CLIENT_ID_FIELD_NAME = "client_id";
    private static final String CORRELATION_ID_FIELD_NAME = "correlation_id";

    // public static final Schema SCHEMA = new Schema(
    //         new Field(API_KEY_FIELD_NAME, INT16, "The id of the request type."),
    //         new Field(API_VERSION_FIELD_NAME, INT16, "The version of the API."),
    //         new Field(CORRELATION_ID_FIELD_NAME, INT32, "A user-supplied integer value that will be passed back with the response"),
    //         new Field(CLIENT_ID_FIELD_NAME, NULLABLE_STRING, "A user specified identifier for the client making the request.", ""));
    //
    // private final ApiKeys apiKey;
    // private final short apiVersion;
    // private final String clientId;
    // private final int correlationId;


    public static RequestHeader parse(ByteBuffer buffer) {
        short apiKey = buffer.getShort();
        short apiVersion = buffer.getShort();


        return null;
    }
}
