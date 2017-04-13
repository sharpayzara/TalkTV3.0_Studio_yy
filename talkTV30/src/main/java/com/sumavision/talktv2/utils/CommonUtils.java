package com.sumavision.talktv2.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author 郭鹏
 * @version 2.0
 * @createTime
 * @decountription 通用工具
 * @changLog
 */
public class CommonUtils {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float countale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * countale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float countale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / countale + 0.5f);
	}

	public static SpannableString getSpannableString(String str,
			int firstIndex, int endIndex, Object style) {
		SpannableString spannableString = new SpannableString(str);
		spannableString.setSpan(style, firstIndex, endIndex,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannableString;
	}

	/**
	 * 读取当前网速
	 */
	public static long getNetSpeed() {
		ProcessBuilder cmd;
		long readBytes = 0;
		BufferedReader rd = null;
		try {
			String[] args = { "/system/bin/cat", "/proc/net/dev" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			rd = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			// int linecount = 0;
			while ((line = rd.readLine()) != null) {
				// linecount++;
				if (line.contains("lan0") || line.contains("eth0")) {
					String[] delim = line.split(":");
					if (delim.length >= 2) {
						readBytes = parserNumber(delim[1].trim());
						break;
					}
				}
			}
			rd.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return readBytes;
	}

	private static long parserNumber(String line) throws Exception {
		long ret = 0;
		if (!TextUtils.isEmpty(line)) {
			String[] delim = line.split(" ");
			if (delim.length >= 1) {
				ret = Long.parseLong(delim[0]);
			}
		}
		return ret;
	}

	@SuppressWarnings("deprecation")
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory(); // 获取数据目录
		StatFs stat = new StatFs(path.getPath());

		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	@SuppressWarnings("deprecation")
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	@SuppressWarnings("deprecation")
	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		} else {
			return -1;
		}
	}

	@SuppressWarnings("deprecation")
	public static long getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long totalBlocks = stat.getBlockCount();
			return totalBlocks * blockSize;
		} else {
			return -1;
		}
	}

	/**
	 * 
	 * @param count
	 * @return 处理过后的count
	 */
	public static String processPlayCount(int count) {
		String result = count + "";

		int l = result.length();
		switch (l) {
		// 万
		case 5:
			result = result.substring(0, 1) + "万+";
			break;
		// 十万
		case 6:
			result = result.substring(0, 2) + "万+";
			break;
		// 百万
		case 7:
			result = result.substring(0, 3) + "万+";
			break;
		// 千万
		case 8:
			result = result.substring(0, 4) + "万+";
			break;
		// 亿
		case 9:
			result = result.substring(0, 5) + "万+";
			break;
		// 十亿
		case 10:
			result = result.substring(0, 6) + "万+";
			break;
		default:
			break;
		}

		return result;
	}

	public static boolean hasAvailableSpace() {
		// 内部空间小于30M时，无法安装电视粉
		if (CommonUtils.getAvailableInternalMemorySize() / (1024 * 1024) < 350)
			return false;
		else
			return true;
	}

	// 解析字符串为时间
	public static int parserString2TimeStamp(String str) {
		int totalSec = 0;
		if (str.contains(":")) {
			String[] my = str.split(":");
			int hour = Integer.parseInt(my[0]);
			int min = Integer.parseInt(my[1]);
			int sec = Integer.parseInt(my[2]);
			totalSec = hour * 3600 + min * 60 + sec;
			totalSec = totalSec * 1000;
		}
		return totalSec;
	}

	public static final String spic = "s.jpg";
	public static final String mpic = "m.jpg";
	public static final String xpic = "b.jpg";

	public static String getUrl(Context context, String path) {
		final float countale = context.getResources().getDisplayMetrics().density;
		if (countale >= 2.0) {
			return path += xpic;
		} else if (countale >= 1.5) {
			return path += mpic;
		} else {
			return path += spic;
		}
	}
	public static void showMethodLog(String name){
		Log.e("msg_method", name);
	}
		public static String getImagCacheDir(Context context) {
		if (CommonUtils.externalMemoryAvailable()) {
			String root = context.getExternalFilesDir(null) + "/";
			return root + "/TVFan/imageCache";
		}
		return "";
	}
	public static String getDateString(){
		String result = "20150513";
		result = new SimpleDateFormat("yyyyMMdd").format(new Date());
		return result;
	}
	//List深度复制
	public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dest = (List<T>) in.readObject();
		return dest;
	}
	//获取当前进程名
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
	public static String getAppVersionName(Context context){
		String result = "";
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(context.getPackageName(),0);
			result = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}
