package com.sumavision.talktv2.bean;


import java.util.ArrayList;

/**
 * 推荐页焦点图
 * 
 * @version 2.2
 * @createTime 2012-11-29
 * @description 推荐类
 * @changeLog
 */
public class RecommendData {
	public static final int TYPE_PROGRAM = 1;
	public static final int TYPE_ACTIVITY = 2;
	public static final int TYPE_USER = 3;
	public static final int TYPE_STAR = 4;
	public static final int TYPE_MICRO_VIDEO = 4;
	public static final int TYPE_ADVERTISE = 12;
	public static final int TYPE_APP_RECOMMEND = 13;
	public static final int TYPE_NEWS_ZONE = 14;
	public static final int TYPE_INTERACTIVE_ZONE = 16;
	public static final int TYPE_USHOW =17;
	public static final int TYPE_SHOPPING_HOME = 18;
	public static final int TYPE_CHILD_PROGRAM = 19;
	public static final int TYPE_CHANNEL = 20;
	public static final int TYPE_SPECIAL = 21;
	public static final int TYPE_SPECIAL_PROGRAM = 22;
	public static final int TYPE_GOODS_DETAIL = 23;
	public static final int TYPE_KUGOU_FANXING = 24;
	public long id;
	public int type;
	public String name;
	public String pic;
	public String url;
	public String topicId;
	public String videoPath;
	public String identifyName;
	public String appName;
	
	public long otherId;// type=16时作为zoneId
	public ArrayList<NetPlayData> liveUrls ;

}
