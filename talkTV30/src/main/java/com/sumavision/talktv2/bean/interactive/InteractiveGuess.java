package com.sumavision.talktv2.bean.interactive;

import java.io.Serializable;
import java.util.ArrayList;

public class InteractiveGuess implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int PRIZE_TYPE_DIAMOND = 1;
	public static final int PRIZE_TYPE_POINT = 2;
	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_INTIME = 2;
	public int id;
	public String title;
	public int prizeType;// 奖励类型：1=宝石，2=积分
	public int prizeCount;
	public String endTime;
	public boolean status;// 是否进行中
	public boolean userJoin;// 是否已参加
	public int type;// 竞猜类型:1-常规,2-实时

	public String picAd;
	public ArrayList<GuessOption> option;
	public boolean userWin;// true-赢
	public GuessOption userOption;
	public GuessOption answerOption;// 实时
	public int activityId;
	public String startTime;
	// public int intimeChoseOptionId;
	public int interactiveDuration;// 参与实时互动页停留时间 (单位s)
}
