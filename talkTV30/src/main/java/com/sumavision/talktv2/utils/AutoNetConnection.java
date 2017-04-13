package com.sumavision.talktv2.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * 
 */
public class AutoNetConnection {
	private static HttpURLConnection conn;
	private final static int COUNT = 100;
	public static int count = 0;
	public static Thread connectThread;
	private static String nowUrl;

	public static void processPlayURL(final String playPath) {
		nowUrl = playPath;

		connectThread = new Thread((new Runnable() {

			@Override
			public void run() {
				try {
					URL url = new URL(playPath);
					try {
						conn = (HttpURLConnection) url.openConnection();
						conn.setReadTimeout(5000);
						conn.setConnectTimeout(5000);
						conn.setRequestMethod("GET");
						conn.setDoInput(true);
						conn.setRequestProperty("Charset", "UTF-8");

						conn = (HttpURLConnection) url.openConnection();
						String resultStr = conn.getURL().toString();
						int code = -1;
						try {
							code = conn.getResponseCode();
						} catch (NullPointerException e) {
							e.printStackTrace();
							code = -1;
						}
						Log.i("autonet","返回码: " + code);
						Log.i("autonet",resultStr);

						if (code == 200) {
							count++;
							try {
								int r = (int) Math.rint(Math.random() * 20);
								r *= 100;
								Log.i("autonet","延迟时间: " + r);
								Thread.sleep(r);
							} catch (InterruptedException e) {
								count = 100;
								e.printStackTrace();
							}

							if (count < COUNT) {
								Log.i("autonet","打开次数: " + count);
								processPlayURL(nowUrl);
							} else {
								count = 0;
							}
						}

					} catch (IOException e) {
						count = 100;
						e.printStackTrace();
					} catch (NullPointerException e) {
						count = 100;
						e.printStackTrace();
					}
				} catch (MalformedURLException e1) {
					count = 100;
					e1.printStackTrace();
				}
			}
		}));
		connectThread.start();
	}

	public static void closeconnectThread() {
		count = 100;
		if (connectThread != null) {
			connectThread.interrupt();
			connectThread = null;
			conn.disconnect();
		}
	}

	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}
}
