package com.sumavision.talktv2.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目数据
 * 
 * @version
 * @createTime 2012-12-25
 * @changeLog
 */
public class VodProgramData {

	// 正序，倒序,1小到大，2大到小
	public int subOrderType;
	public String id;
	public String topicId;
	public int activityId;
	public String name;
	public String shortIntro = "";
	public String channelId;
	public String channelLogo;
	public String channelName;
	public String startTime;
	public String endTime;
	public String title;
	// 节目图片绝对路径
	public String pic;
	// 竖版节目图片绝对路径
	public String picv;
	// 横版节目图片绝对路径
	public String pich;
	// 播放链接：直接播放时为直接播放链接，网页播放时为网址
	public String playUrl;
	public int picType;
	public int playTimes = 0;
	// 是否正在播放：0=未开始播放，1=正在播放
	public int isPlaying;
	public int playType;
	public String updateName = "";
	public long remindId;
	// 节目单名称
	public String cpName;
	// 是否追剧0否1是
	public int isChased;
	// 视频总数
	public int videoCount;
	public String cpDate;
	// 周边新闻数量
	public int aroundCount;
	public List<ProgramAroundData> around;
	public List<StarData> star;
	public List<PlayNewData> activity;
	// 周边明星数量
	public int starCount;
	// 剧照数量
	public int photoCount;
	public String[] photos;
	public String stagerName;
	// 节目类型名称
	public String contentTypeName;
	public String intro;
	public long cpId = 0;
	// 豆瓣评分
	public String point = null;
	// 来自
	public String fromString;
	public int pcount = 0;
	public int ptype = 0;
	public ArrayList<NetPlayData> netPlayDatas = null;
	public int playLength;
	public int playVideoActivityId = 0;
	public String subId;// 子节目ID
	public String subName;// 节目名称
	public String subProgramId;// 节目ID
	public String subUrl;// 播放路径
	public String subVideoPath;// 播放地址
	public String subHighPath;// 高清地址
	public String subSuperPath;// 超清地址
	public int subSearchType;// 0节目1视频
	public static final int SEARCH_TYPE_PROGRAM = 0;
	public static final int SEARCH_TYPE_VIDEO = 1;
	public boolean selected;
	public String programId;
	public boolean isAd;//jvxiaoAd
	public Object adObj;
	public String adId;

	public int monthGoodCount; // 点赞数

	public enum ProgramType {
		TV("电视剧", 1), MOVIE("电影", 2), ZONGYI("综艺", 3), CAIJING("财经", 4), SCIENCE(
				"科学自然", 5), EDU("教育", 6), LAW("法制", 7), ARMY("军事", 8), NEWS(
				"新闻综合", 9), LIFE("生活人文", 10), CARTOON("动漫", 11), ART("体育健康", 12), OPERA(
				"戏曲", 13), RECORD("纪录片", 14), OLYMPIC("奥运", 15), WEIMOVIE(
				"微影视", 16), GAME("游戏", 17), SHORTVIDEO("短视频", 22);

		private String name;
		private int type;

		private ProgramType(String name, int type) {
			this.name = name;
			this.type = type;
		}

		public static String getTypeName(int type) {
			for (ProgramType p : ProgramType.values()) {
				if (p.getType() == type) {
					return p.getName();
				}
			}
			return null;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
}
