package com.sumavision.talktv2.bean;

import android.os.Environment;

import java.io.File;

/**
 * @author 郭鹏
 * @version 2.0
 * @createTime
 * @description JSON解析与系统其它常量表
 * @changLog
 */
public class JSONMessageType {

	public static String APP_VERSION = "2.2";
	public static String VERSION_231 = "2.3.1";
	public static String APP_VERSION_NINE = "2.9";
	public static String APP_VERSION_309 = "3.0.9";
	public static String APP_VERSION_310 = "3.1.0";
	public static String APP_VERSION_311 = "3.1.1";
	public static String APP_VERSION_312 = "3.1.2";
	public static String APP_VERSION_313 = "3.1.3";
	public static String APP_VERSION_314 = "3.1.4";

	public static String APP_VERSION_THREE = "3.0.0";
	public static String APP_VERSION_ONE = "1.0.0";
	// 客户端
	public static final int SOURCE = 1;
	public static final int SOURCE_PAD = 10;

	public static final String SERVER_NETFAIL = "网络繁忙，请稍后重试";

	// 临时图片目录
	public static String USER_PIC_SDCARD_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ "TVFan/tempCamera";
	// 客厅栏目类型标记
	// 1=节目类栏目；2=活动类栏目；3=用户类栏目；4=演员类栏目；5=微影视类栏目；6=粉播；7=新片预告
	// 主目录
	public static String USER_ALL_SDCARD_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/TVFan/";

	public static final int SERVER_CODE_OK = 0;
	public static final int SERVER_CODE_ERROR = 1;

}
