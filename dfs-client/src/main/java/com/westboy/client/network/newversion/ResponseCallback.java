package com.westboy.client.network.newversion;

/**
 * 响应回调函数接口
 * @author zhonghuashishan
 *
 */
public interface ResponseCallback {

	/**
	 * 处理响应结果
	 * @param response
	 */
	void process(NetworkResponse response);
	
}
