package com.sumavision.talktv2.bean;

import java.util.List;

/**
 * @author 李梦思
 * @createTime 2012-6-14
 * @description 私信实体类
 * @changeLog
 */
public class MailData {
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_WEB = 2;
	public static final int TYPE_PROGRAM = 3;
	public static final int TYPE_PLAY = 4;
	public static final int TYPE_LIVE_PLAY = 5;
	public static final int TYPE_ACTIVITY = 6;
	public static final int TYPE_SPECIAL = 7;
	public static final int TYPE_ZONE = 8;
	public static final int TYPE_USHOW = 9;
	public static final int TYPE_EXHCNAGE_GOODS = 10;
	
	public static final int TYPE_SPECIAL_PROGRAM = 1;
	public static final int TYPE_SPECIAL_SUB = 15;
	// 内容
	public String content = "";
	// 接收用户Id
	public int rid;
	// 发送用户Id
	public int sid;
	// Id
	public long id;
	// 0表示未阅读，1表示已经阅读
	public int flag = 0;
	// 发信时间
	public String timeStemp;
	// 接收用户名
	public String rUserName;
	// 接收用户头像
	public String rUserPhoto;
	// 发送用户名
	public String sUserName;
	// 发送用户头像
	public String sUserPhoto;
	// 内容图片
	public String pic = "";
	// 常用短语
	public List<String> phrases;
	// 是否是自己发的
	public boolean isFromSelf;

	public int type;
	public long otherId;
	public String url;
	public String playVideopath;
	public String highPath;
	public String superPath;
	
	//专题类型，为1时表示节目专题，15表示子节目专题
	public int specialType;
	public boolean isVip;
}
