package com.sumavision.talktv2.bean;

public class SinaData {
	public static String CUSTOMER_KEY = "2064721383";
	public static String REDIRECT_URL = "http://www.tvfan.cn";
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog";
	public static String accessToken = "";
	public static String expires_in = "";
	public static boolean isSinaBind = false;
	public static String content;
	public static String pic;
	// 微博类型0文字1带图片
	public static int type = 0;
	public static String id;
	public static String name;
	public static String icon;
	public static int gender = 1;
	public static String description = "电视粉";

}
