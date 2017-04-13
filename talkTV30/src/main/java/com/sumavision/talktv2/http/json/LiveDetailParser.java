package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LiveDetailParser extends BaseJsonParser {

	public ChannelData channelData;
	public ArrayList<String> weekDate;
	public int playItemPos;
	private String channelName;
	
	public LiveDetailParser(String channelName) {
		this.channelName = channelName;
	}

	@Override
	public void parse(JSONObject jsonObject) {
		channelData = new ChannelData();
		weekDate = new ArrayList<String>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.optJSONObject("content");
				if (content.has("play")) {
					ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
					JSONArray play = content.getJSONArray("play");
					int length = play.length();
					for (int x = 0; x<length; x++) {
						NetPlayData netPlayData = new NetPlayData();
						JSONObject playItem = play.getJSONObject(x);
						netPlayData.channelName = channelName;
						netPlayData.pic = playItem.optString("pic");
						netPlayData.url = playItem.optString("url");
						netPlayData.videoPath = playItem
								.optString("videoPath");
						netPlayData.platformId = playItem
								.optInt("platformId");
						netPlayData.channelIdStr = playItem.optString("channelIdStr");
						netPlayData.showUrl = playItem.optString("showUrl");
						netPlayData.id = playItem.optInt("id");
						netPlayDatas.add(netPlayData);
					}
					channelData.netPlayDatas = netPlayDatas;
				}
				if (content.has("day")) {
					JSONArray day = content.optJSONArray("day");
					if (day != null) {
						ArrayList<CpData> cpList = new ArrayList<CpData>();
						for (int i = 0; i < day.length(); i++) {
							JSONObject dayObj = day.optJSONObject(i);
							String week = dayObj.optString("week");
							String date = dayObj.optString("date");
							weekDate.add(week);
							if (dayObj.has("program")) {
								JSONArray program = dayObj.optJSONArray("program");
								if (program != null) {
									for (int j = 0; j < program.length(); j++) {
										JSONObject programObj = program.optJSONObject(j);
										CpData p = new CpData();
										p.week = week;
										p.date = date;
										p.channelName = channelName;
										p.programId = programObj.optString("programId");
										p.name = programObj.optString("programName");
										p.topicId = programObj.optString("topicId");
										p.startTime = programObj.optString("cpTime");
										p.endTime = programObj.optString("endTime");
										p.type = programObj.optInt("type");
										p.id = programObj.optInt("cpId");
										p.remindId = programObj.optLong("remindId");
										p.isPlaying = initPlayingType(p.startTime, p.endTime, date);
										if (p.isPlaying == 0)
											playItemPos = i;
										p.order = programObj.optInt("isRemind");
										p.backUrl = programObj.optString("backUrl");
										cpList.add(p);
									}
								}
							}
						}
						channelData.cpList = cpList;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	private int whichDayType;

	// 0直播，1回看， 2尚未播放
	private int initPlayingType(String startS, String endS, String date) {
		int i = compareDate(date);
		if (i != 0) {
			return i;
		}
		if (whichDayType == -1) {
			return 1;
		} else if (whichDayType == 1) {
			return 2;
		} else {
			int start = Integer.parseInt(startS.replace(":", ""));
			int end = Integer.parseInt(endS.replace(":", ""));
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			int now = Integer.parseInt(sdf.format(new Date()));
			if (now >= start && now < end) {
				return 0;
			} else if (now < start) {
				return 2;
			} else if (now >= end) {
				return 1;
			}
		}
		return 3;
	}
	
	public static int compareDate(String date) {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(df.format(now));
			Date dt2 = df.parse(date);
			if (dt1.getTime() > dt2.getTime()) {
//				System.out.println("dt1是后一天");
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
//				System.out.println("dt1是前一天");
				return 2;
			} else {
				return 0;
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return -1;
	}
	
	private String getWeekDayString() {
		String weekString = "";
		final String dayNames[] = {"周日","周一","周二","周三","周四","周五","周六"}; 
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date); 
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		weekString = dayNames[dayOfWeek - 1];
		return weekString;
	}
}
