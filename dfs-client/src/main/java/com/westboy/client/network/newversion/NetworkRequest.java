package com.westboy.client.network.newversion;

import com.westboy.common.entity.Node;
import lombok.Data;

import java.nio.ByteBuffer;

/**
 * 网络请求
 */
@Data
public class NetworkRequest {
	
	public static final Integer REQUEST_TYPE = 4;
	public static final Integer FILENAME_LENGTH = 4;
	public static final Integer FILE_LENGTH = 8;
	public static final Integer REQUEST_SEND_FILE = 1;
	public static final Integer REQUEST_READ_FILE = 2;
	
	private Integer requestType;
	private String id;
	private Node node;
	private ByteBuffer buffer;
	// 是否需要响应结果（这里设置的上传文件请求不需要，下载文件设置需要）
	private Boolean needResponse;
	// 封装为 NetworkRequest 请求对象时的时间
	private long sendTime;
	// 异步是响应回调
	private ResponseCallback callback;
	
}