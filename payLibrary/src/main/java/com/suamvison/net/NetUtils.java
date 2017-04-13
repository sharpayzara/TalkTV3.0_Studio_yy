package com.suamvison.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;

import comsumavision.utils.ExchangeSecureUtils;

public class NetUtils {

	public static final String DEFAULT_URL = Constants.host;

	/**
	 * 
	 * @param params
	 *            json �ַ�
	 * @param url
	 *            ��������ַ ���?url���� ��ô��Ĭ�ϵ�URL
	 * @return
	 */
	public static String execute(Context context, String params, String url) {
		if (url == null) {
			url = DEFAULT_URL;
		}
		try {
			HttpURLConnection conn = getConnection(url, context);
			if (conn == null) {
				return null;
			}
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/x-javascript");
			conn.setReadTimeout(20000);
			conn.setConnectTimeout(20000);
			conn.connect();
			OutputStream outStream = conn.getOutputStream();
			OutputStreamWriter objSW = new OutputStreamWriter(outStream);
			BufferedWriter out = new BufferedWriter(objSW);
			char[] data = params.toCharArray();
			out.write(data, 0, data.length);
			out.flush();
			out.close();
			int status = conn.getResponseCode();
			if (status == 200) {
				InputStream in = conn.getInputStream();
				StringBuilder sb = new StringBuilder();
				BufferedReader r = new BufferedReader(
						new InputStreamReader(in), 1000);
				for (String line = r.readLine(); line != null; line = r
						.readLine()) {
					sb.append(line);
				}
				in.close();
				return sb.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String executeEncrypt(Context context, String params,
			String url, String userid, String key) {
		if (url == null) {
			url = DEFAULT_URL;
		}
		ExchangeSecureUtils exchangeSecureUtils = new ExchangeSecureUtils();
		try {
			HttpURLConnection conn = getConnection(url, context);
			if (conn == null) {
				return null;
			}
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "application/x-javascript");
			conn.addRequestProperty("encrypt", "1");
			conn.addRequestProperty("userId", userid);
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(30000);
			conn.connect();
			OutputStream outStream = conn.getOutputStream();
			OutputStreamWriter objSW = new OutputStreamWriter(outStream);
			BufferedWriter out = new BufferedWriter(objSW);
			char[] data = params.toCharArray();
			out.write(data, 0, data.length);
			out.flush();
			out.close();
			int status = conn.getResponseCode();
			String encypt = conn.getHeaderField("encrypt");
			if (status == 200) {
				InputStream in = conn.getInputStream();
				StringBuilder sb = new StringBuilder();
				BufferedReader r = new BufferedReader(
						new InputStreamReader(in), 1000);
				for (String line = r.readLine(); line != null; line = r
						.readLine()) {
					sb.append(line);
				}
				in.close();
				String mm = "";
				if (encypt.equals("1")) {
					mm = exchangeSecureUtils.textDecrypt(sb.toString(), key);
				}

				return mm;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * ����httpConnection ��������֧��https ������httpsConnection ��ȫ�Ը��?
	 * 
	 * @param urlString
	 * @param context
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getConnection(String urlString,
			Context context) throws IOException {
		HttpURLConnection connection = null;

		URL url = new URL(urlString);

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (null != netInfo
				&& ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
			connection = (HttpURLConnection) url.openConnection();
		} else {
			String proxyHost = android.net.Proxy.getDefaultHost();
			if (null == proxyHost) {
				connection = (HttpURLConnection) url.openConnection();
			} else {
				java.net.Proxy p = new java.net.Proxy(java.net.Proxy.Type.HTTP,
						new InetSocketAddress(
								android.net.Proxy.getDefaultHost(),
								android.net.Proxy.getDefaultPort()));
				connection = (HttpURLConnection) url.openConnection(p);
			}
		}
		return connection;
	}
}
