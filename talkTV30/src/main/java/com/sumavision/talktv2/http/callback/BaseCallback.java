package com.sumavision.talktv2.http.callback;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * http请求响应回调基类
 * 
 * @author suma-hpb
 * 
 */
public abstract class BaseCallback implements Listener<JSONObject>,
		ErrorListener {

	protected OnHttpErrorListener errorListener;
	protected JSONObject object = new JSONObject();
	private Map<String, String> requestHeaders = new HashMap<String, String>();
	public static final int ERRCODE_NO_NET = 1;

	public BaseCallback(OnHttpErrorListener errorListener) {
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
	public abstract void parseNetworkRespose(JSONObject jsonObject);

	/**
	 * 封装json请求
	 * 
	 * @return
	 */
	public abstract JSONObject makeRequest();

	@Override
	public void onResponse(JSONObject jsonObject) {
		onResponseDelegate();
	}

	@Override
	public void onErrorResponse(VolleyError volleyError) {
		if (errorListener != null) {
			String msg = volleyError.getMessage();
			int code = 2;
			if (msg != null && msg.contains("UnknownHostException")) {
				code = ERRCODE_NO_NET;
			}
			errorListener.onError(code);
		}
	}
}
