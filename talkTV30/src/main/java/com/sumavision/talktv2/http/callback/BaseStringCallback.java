package com.sumavision.talktv2.http.callback;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public abstract class BaseStringCallback implements Listener<String>,
		ErrorListener {

	protected OnHttpErrorListener errorListener;
	private Map<String, String> requestHeaders = new HashMap<String, String>();

	public BaseStringCallback(OnHttpErrorListener errorListener) {
		this.errorListener = errorListener;
	}

	/**
	 * 添加单个请求头
	 * 
	 * @param key
	 * @param value
	 */
	public void addRequestHeader(String key, String value) {
		requestHeaders.put(key, value);
	}

	/**
	 * 添加多个请求头
	 * 
	 * @param requestHeaders
	 */
	public void addRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders.putAll(requestHeaders);
	}

	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	private String secretKey;// 需加密时初始化秘钥

	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * 设置秘钥
	 * 
	 * @param secretKey
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * 响应回调处理(主线程)
	 * 
	 * @param jsonObject
	 */
	protected abstract void onResponseDelegate();

	/**
	 * json解析(线程)
	 * 
	 * @param jsonObject
	 */
	public abstract void parseNetworkRespose(String str);

	/**
	 * 封装json请求
	 * 
	 * @return
	 */
	public abstract String makeRequest();

	@Override
	public void onResponse(String result) {
		onResponseDelegate();
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		if (errorListener != null) {
			String msg = volleyError.getMessage();
			int code = 2;
			if (msg.contains("UnknownHostException")) {
				code = 1;
			}
			errorListener.onError(code);
		}
	}

}
