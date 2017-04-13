package com.sumavision.talktv2.bean.interactive;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * 互动活动数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveActivity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int STATUS_JOINED = 1;

	public static final int STATUS_WIN = 2;

	public static final int STATUS_LOSE = 3;

	public static final int TYPE_SPORTS = 1;

	public static final int TYPE_PROGRAM = 2;

	public static final int INTERACTION_STATUS_INTIME = 3;// 实时互动

	public static final int INTERACTION_STATUS_ROOM = 2;// 进入房间

	public static final int INTERACTION_STATUS_NO = 1;// 未开始

	public int id;

	public boolean isTop;

	public String title;

	public String endTime;

	public String currentTime;

	public String startTime;

	@SerializedName("status")
	public int interactStatus;// 实时互动、进入房间、未开始

	public String showTime;

	@SerializedName("joinQty")
	public int personCount;// 参与人数

	public int type;// 1-体育类，2-节目类

	public int topicId;

	@SerializedName("leftName")
	public String leftTeamName;

	@SerializedName("leftPic")
	public String leftTeamLogo;

	@SerializedName("leftSupportQty")
	public int leftSupportCount;

	public int leftPoint;

	@SerializedName("rightSupportQty")
	public int rightSupportCount;

	@SerializedName("rightName")
	public String rightTeamName;

	@SerializedName("rightPic")
	public String rightTeamLogo;

	public int rightPoint;

	public int userSupport;// 1-支持左，2-支持右，0-未参与

	/**
	 * 节目类信息
	 */
	public String programPhoto;

	public String programIntro;

}
