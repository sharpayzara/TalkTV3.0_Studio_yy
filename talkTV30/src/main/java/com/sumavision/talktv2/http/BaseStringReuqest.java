package com.sumavision.talktv2.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.sumavision.talktv2.http.callback.BaseStringCallback;
import comsumavision.utils.ExchangeSecureUtils;

public class BaseStringReuqest extends StringRequest {

	private final BaseStringCallback callback;
	private static final int SOCKET_TIMEOUT = 10000;

	public BaseStringReuqest(String url, BaseStringCallback callback) {
		super(Method.POST, url, callback, callback);
		this.callback = callback;
		setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected void deliverResponse(String str) {
		if (callback != null) {
			callback.onResponse(str);
		}
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		String content = callback.makeRequest().toString();
		return content.getBytes();
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
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String resultStr = getRealString(response.data);
		Map<String, String> headers = response.headers;
		String encrypt = headers.get("encrypt");
		if (!TextUtils.isEmpty(encrypt) && encrypt.equals("1")) {
			resultStr = new ExchangeSecureUtils().textDecrypt(resultStr,
					callback.getSecretKey());
		}
		callback.parseNetworkRespose(resultStr);
		Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
		return Response.success(resultStr, cacheEntry);
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
}
