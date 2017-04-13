package com.sumavision.talktv2.bean;

import java.util.ArrayList;
import java.util.List;

public class ProgramData {

	public static final int TYPE_TV = 1;
	public static final int TYPE_MOVIE = 2;
	public static final int TYPE_ZONGYI = 3;
	public static final int TYPE_DONGMAN = 11;
	public static final int TYPE_MICRO_VIDEO = 16;
	public static final int TYPE_MICRO_MOVIE = 20;
	public long programId;
	public long cpId;
	public long topicId;
	public String title;
	public String name;
	public String pic;
	public String director;
	public String time;
	public String actors;
	public String region;
	public String detail;
	public String contentType;
	public String update;
	public double doubanPoint;
	public int showPattern;
	public int pType;
	public boolean isSigned;
	public boolean isChased;
	public boolean isZan;
	public int skipWeb;
	public int signCount;
	public String area;
	public String intro;
	// 1正序，2倒叙
	public int subOrderType=1;
	public ArrayList<User> signUsers;
	public ArrayList<ProgramVideoData> videoData;
	public ArrayList<NetPlayData> netPlayDatas = null;

	public ProgramDetailInfoData programDetailInfoData = null;
	public int evaluateCount;// 点赞数
	public int talkCount; //评论数
	public String userPic;
	public String userName;
	public List<SourcePlatform> platformList = new ArrayList<SourcePlatform>();
	
	public ArrayList<VodProgramData> recommendPrograms = new ArrayList<VodProgramData>();
	public int recommendNumber;
	public ArrayList<CommentData> comment = new ArrayList<CommentData>();

	//311加入 剧集相关信息
	public int subId,stage,subCount,order,detailType,subType;
	public ArrayList<String> stageTag = new ArrayList<String>();
	public boolean isVip;
}
