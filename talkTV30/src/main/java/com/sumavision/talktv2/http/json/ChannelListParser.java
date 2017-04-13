package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.style.ForegroundColorSpan;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.CommonUtils;

public class ChannelListParser extends BaseJsonParser {

	public ArrayList<ShortChannelData> list = new ArrayList<ShortChannelData>();
	public int channelCount;

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
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				channelCount = content.optInt("cCount");
				if (content.has("c")) {
					JSONArray shortChannelArray = content.getJSONArray("c");
					for (int i = 0; i < shortChannelArray.length(); ++i) {
						JSONObject shortChannelJsonObject = shortChannelArray
								.getJSONObject(i);
						ShortChannelData tempShortChannelData = new ShortChannelData();
						tempShortChannelData.channelId = shortChannelJsonObject
								.optInt("id");
						tempShortChannelData.channelName = shortChannelJsonObject
								.optString("name");
						tempShortChannelData.channelPicUrl = shortChannelJsonObject
								.optString("pic");
						tempShortChannelData.channelType = shortChannelJsonObject
								.optInt("channelType");
						tempShortChannelData.flagMyChannel = shortChannelJsonObject
								.optInt("isMyChannel") == 1 ? true : false;

						JSONObject channelProgramOjbect = shortChannelJsonObject
								.optJSONObject("cp");
						if (channelProgramOjbect != null) {
							tempShortChannelData.programId = channelProgramOjbect
									.optInt("programId");
							String startTime = channelProgramOjbect
									.optString("startTime");
							String endTime = channelProgramOjbect
									.optString("endTime");
							if (startTime != null && endTime != null) {
								tempShortChannelData.startTime = startTime;
								tempShortChannelData.endTime = endTime;
								setSpannelStyle(startTime, endTime,
										tempShortChannelData);
							}
							tempShortChannelData.endTime = channelProgramOjbect
									.optString("endTime");
							tempShortChannelData.topicId = channelProgramOjbect
									.optString("topicId");
							tempShortChannelData.programName = channelProgramOjbect
									.optString("cpName");
							tempShortChannelData.cpId = channelProgramOjbect
									.optInt("cpId");
						}

						if (shortChannelJsonObject.has("play")) {
							tempShortChannelData.livePlay = true;
							JSONArray play = shortChannelJsonObject
									.getJSONArray("play");
							ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
							for (int x = 0; x < play.length(); x++) {
								NetPlayData netPlayData = new NetPlayData();
								JSONObject playItem = play.optJSONObject(x);
								netPlayData.name = playItem.optString("name");
								netPlayData.pic = playItem.optString("pic");
								netPlayData.url = playItem.optString("url");

								netPlayData.videoPath = playItem
										.optString("videoPath");
								netPlayData.channelName = tempShortChannelData.channelName;

								netPlayData.platformId = playItem
										.optInt("platformId");
								netPlayDatas.add(netPlayData);
							}
							tempShortChannelData.netPlayDatas = netPlayDatas;
						}

						list.add(tempShortChannelData);
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	ForegroundColorSpan span;

	private void setSpannelStyle(String startTime, String endTime,
			ShortChannelData shortChannelData) {
		if (startTime == null)
			return;
		StringBuilder sb = new StringBuilder();
		sb.append(startTime).append("-").append(endTime);
		shortChannelData.spannableTimeString = CommonUtils.getSpannableString(
				sb.toString(), 0, startTime.length(), span);
	}
}
