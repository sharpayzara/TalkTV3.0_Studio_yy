package com.sumavision.talktv2.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 活动
 * 
 * @author suma-hpb
 * 
 */
public class ActivityData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int JOIN_STATUS_UNJOIN = 1;// 未参加
	public static final int JOIN_STATUS_UNFINISH = 2;// 参加未完成
	public static final int JOIN_STATUS_FINISHED = 3;// 参加已完成
	public static final int STATE_NOT_START = 1;// 活动未开始
	public static final int STATE_ONGOING = 2;// 活动进行中
	public static final int STATE_OVER = 3;// 活动已结束

	public long programId;
	public long activityId;
	public int state;
	public String activityName;
	public String videoPath;
	public String url;
	public String playPic;
	public String activityPic = "";
	public String taskIntro;
	public String announcement;
	public ArrayList<String> pics = null;

	public int userCount;
	public Good good;
	public int joinStatus;

	public int totalTimes = 500;// 总次数
	public int joinedTimes = 0;// 已抽奖次数
	public String winMsg = "";// 中奖用户信息
	public ArrayList<ReceiverInfo> receiverList = new ArrayList<ReceiverInfo>();

}
