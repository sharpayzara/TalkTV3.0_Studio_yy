package com.sumavision.talktv2.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sumavision.talktv2.http.callback.BaseCallback;

/**
 * volley http common resquest
 * 
 * @author pengbing han 16152
 * 
 * @param <T>
 *            custom response(json type)parser class
 */
public abstract class BaseRequest extends JsonObjectRequest {

	private final BaseCallback callback;
	public static final int SOCKET_TIMEOUT = 10000;

	/**
	 * 
	 * @param url
	 *            请求地址
	 * @param callback
	 *            回调
	 */
	public BaseRequest(String url, BaseCallback callback) {
		super(Method.POST, url, callback.makeRequest(), callback, callback);
		setShouldCache(false);
		setTag(parseMethod(callback.makeRequest()));
		setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		this.callback = callback;
	}

	@Override
	protected void deliverResponse(JSONObject obj) {
		if (callback != null) {
			callback.onResponse(obj);
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Charset", "UTF-8");
		headers.put("Content-Type", "application/x-javascript");
		Map<String, String> requestHeaders = callback.getRequestHeaders();
		if (requestHeaders != null && requestHeaders.size() > 0) {
			headers.putAll(requestHeaders);
		}
		return headers;
	}

	@Override
	public void deliverError(VolleyError error) {
		if (callback != null) {
			callback.onErrorResponse(error);
		}
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		JSONObject result = null;
		String resultStr = getRealString(response.data);
		try {
			result = new JSONObject(resultStr);
			callback.parseNetworkRespose(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
		return Response.success(result, cacheEntry);
	}

	/**
	 * distinct gzip
	 * 
	 * @param data
	 * @return
	 */
	private String getRealString(byte[] data) {
		byte[] h = new byte[2];
		h[0] = (data)[0];
		h[1] = (data)[1];
		int head = getShort(h);
		boolean t = head == 0x1f8b;
		InputStream in;
		StringBuilder sb = new StringBuilder();
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			if (t) {
				in = new GZIPInputStream(bis);
			} else {
				in = bis;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in),
					1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private int getShort(byte[] data) {
		return (int) ((data[0] << 8) | data[1] & 0xFF);
	}

	private String parseMethod(JSONObject content) {
		if (content == null)
			return "";
		return content.optString("method");
	}
}
