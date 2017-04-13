package com.sumavision.talktv2.http.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 频道详情解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelDetailParser extends BaseJsonParser {
	public ChannelData channelData = new ChannelData();

	public int playItemPos;

	public ChannelDetailParser(int whichDayType) {
		this.whichDayType = whichDayType;
	}

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			errMsg = jsonObject.optString("msg");
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				ArrayList<CpData> list = new ArrayList<CpData>();
				JSONArray content = jsonObject.getJSONArray("content");
				if (jsonObject.has("webPlay")) {
					JSONObject webPlay = jsonObject.getJSONObject("webPlay");
					JSONArray play = webPlay.getJSONArray("play");
					ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
					int len = play.length();
					if (len > 0) {
						for (int x = len - 1; x >= 0; x--) {
							NetPlayData netPlayData = new NetPlayData();
							JSONObject playItem = play.getJSONObject(x);
							netPlayData.name = playItem.optString("name");
							netPlayData.pic = playItem.optString("pic");
							netPlayData.url = playItem.optString("url");
							netPlayData.videoPath = playItem
									.optString("videoPath");
							netPlayData.platformId = playItem
									.optInt("platformId");
							netPlayData.webPage = playItem.optString("webPage");
							netPlayDatas.add(netPlayData);
						}
					}
					channelData.netPlayDatas = netPlayDatas;
				}
				for (int i = 0; i < content.length(); i++) {
					CpData p = new CpData();
					JSONObject item = content.getJSONObject(i);
					p.programId = item.optString("programId");
					p.name = item.optString("programName");
					p.topicId = item.optString("topicId");
					p.startTime = item.optString("cptime");
					p.endTime = item.optString("endtime");
					p.type = item.optInt("type");
					p.id = item.optInt("cpid");
					p.remindId = item.optLong("remindId");
					p.isPlaying = initPlayingType(p.startTime, p.endTime);

					if (p.isPlaying == 0)
						playItemPos = i;

					p.order = item.optInt("isRemind");
					list.add(p);
				}
				channelData.cpList = list;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private int whichDayType;

	// 0直播，1回看， 2尚未播放
	private int initPlayingType(String startS, String endS) {
		if (whichDayType == -1) {
			return 1;
		} else if (whichDayType == 1) {
			return 2;
		} else {
			int start = Integer.parseInt(startS.replace(":", ""));
			int end = Integer.parseInt(endS.replace(":", ""));
			SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
			int now = Integer.parseInt(sdf.format(new Date()));
			if (now > start && now < end) {
				return 0;
			} else if (now < start) {
				return 2;
			} else if (now > end) {
				return 1;
			}
		}
		return 3;
	}
}
