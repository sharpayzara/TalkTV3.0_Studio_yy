package com.sumavision.talktv.videoplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class HttpDownloader {
	private URL url = null;

	public String download(String urlStr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * �ú��������?-1����������ļ�����?0����������ļ��ɹ�?1������ļ��Ѿ�����?
	 */
	public int downFile(String urlStr, String path, String fileName) {
		InputStream inputStream = null;
		try {
			FileUtils fileUtils = new FileUtils();
			// Log.i("downfile3", path + fileName);
			// if (fileUtils.isFileExist(path + fileName)) {
			// Log.i("downfile4", path + fileName);
			// // return 1;
			// } else {
			Log.i("downfile5", path + fileName);
			inputStream = getInputStreamFromUrl(urlStr);
			File resultFile = fileUtils.write2SDFromInput(path, fileName,
					inputStream);
			if (resultFile == null) {
				return -1;
			}

			// unZiper unziper = new unZiper();
			// String mDestPath =
			// "/data/data/com.baidu.bvideoviewsample2/files/";
			String mDestPath = path;
			// String mDestPath = "/storage/sdcard0/zys/sofile/";
			// ZipUtils unziper;
			// unziper.unZip((Context)this, path + fileName, path);
			Log.i("unzip", "unzip");
			unZiper.unZip(path + fileName, mDestPath);
			// unZiper.unZip(fileUtils.getSDPATH() + path + fileName,
			// mDestPath);
			// }
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * ���URL�õ�������
	 * 
	 * @param urlStr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public InputStream getInputStreamFromUrl(String urlStr)
			throws MalformedURLException, IOException {
		url = new URL(urlStr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setRequestProperty("Accept-Encoding", "zip");
		// urlConn.setRequestProperty("Content-MD5", contentMD5);
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}
}
