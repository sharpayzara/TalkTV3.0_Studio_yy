package com.sumavision.talktv2.bean;

/**
 * @version 2.2
 * @createTime 2012-12-14
 * @description 节目单节目数据实体类
 * @changeLog
 */

public class CpData {
	public static final int  TYPE_LIVE=0;//正在播出
	public static final int  TYPE_REVIEW=1;//回看
	public static final int  TYPE_UNPLAY=2;//尚未播出
	// cpId
	public int id = 0;
	// cpName
	public String name = "暂无数据";
	// 开始时间
	public String startTime = "--:--";
	// 结束时间
	public String endTime = "--:--";
	// 播放方式：1=直接播放，2=网页播放
	public int playType;
	public int isPlaying = 0;//type_*
	// 播放链接
	public String playUrl = "";
	// 节目类型
	public int type = 0;
	// 节目Id
	public String programId = "";
	// 是否预约
	public int order = 0;
	public long remindId;
	public String topicId;
	public String week;
	public String today;
	public String backUrl;
	public String date;
	public String channelName;
}
