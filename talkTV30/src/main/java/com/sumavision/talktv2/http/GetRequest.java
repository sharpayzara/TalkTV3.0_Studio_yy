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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.sumavision.talktv2.http.callback.BaseCallback;

public class GetRequest extends Request<JSONObject> {
	private final BaseCallback callback;

	public GetRequest(String url, BaseCallback callback) {
		super(Method.GET, url, callback);
		this.callback = callback;
		setRetryPolicy(new DefaultRetryPolicy(15000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	@Override
	protected void deliverResponse(JSONObject arg0) {
		if (callback != null) {
			callback.onResponse(arg0);
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
		try {
			result = new JSONObject(getRealString(response.data));
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
}
