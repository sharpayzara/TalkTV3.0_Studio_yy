package com.sumavision.talktv2.bean;

import android.text.SpannableString;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author 姜浩
 * @version2.2.6
 * @createTime 2013-5-9
 * @descrition 频道少字段存储 用来放在list中 提高效率
 * 
 */
public class ShortChannelData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 频道ID
	public int channelId;
	// 频道名字
	public String channelName;
	// 频道LOGO地址
	public String channelPicUrl = "";
	// 正在播出的节目名称
	public String programName;
	// 时段
	public String time;
	// 播放次数
	public int viewCount = 0;
	// 节目ID
	public int programId;
	// 节目的播放次数
	public int viewCountProgram = 0;
	// 节目的图片地址
	public String programPicUrl = "";
	// 话题ID
	public String topicId;

	public String startTime;

	// 时间
	public SpannableString spannableTimeString;

	public String endTime;
	public int programType;

	// live play
	public boolean livePlay;

	//
	public ArrayList<NetPlayData> netPlayDatas = null;
	// 节目单ID
	public int cpId;

	public boolean isReminded;

	public boolean flagMyChannel;
	// 频道类型
	public int channelType;

	public String programInfo;
	public String timeInfo;
	public SpannableString spannableTimeInfo;
	
	public String typeName;
	public boolean haveProgram;
	public boolean toWeb;
}
