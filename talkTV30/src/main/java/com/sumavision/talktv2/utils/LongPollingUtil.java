package com.sumavision.talktv2.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import android.content.Context;
import android.util.Log;

import com.suamvison.net.NetUtils;

public class LongPollingUtil {
	HttpURLConnection conn = null;

	/**
	 * 
	 * @param params
	 *            json 字符串
	 * @param url
	 *            服务器地址 如果 url不传 那么用默认的URL
	 * @return
	 */
	public String execute(Context context, String url) {
		if (url == null) {
			url = Constants.host;
		}

		String result = "";
		try {
			conn = NetUtils.getConnection(url, context);
			if (conn == null) {
				return null;
			}
			conn.setUseCaches(false);
			conn.setReadTimeout(60000 * 60);
			conn.setConnectTimeout(60000 * 60);
			conn.setInstanceFollowRedirects(true);
			conn.connect();
			if (conn != null) {
				int status = conn.getResponseCode();
				if (status == 200) {
					result = parse(conn.getInputStream());
				}
			}
		} catch (Exception e) {
			Log.e("LongPollingUtil", e.toString());
		}
		return result;
	}

	public void close() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}

	private String parse(InputStream in) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(in),
					1000);
			for (String line = r.readLine(); line != null; line = r.readLine()) {
				sb.append(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
