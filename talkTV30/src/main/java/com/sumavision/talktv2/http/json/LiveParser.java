package com.sumavision.talktv2.http.json;

import android.text.style.ForegroundColorSpan;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LiveParser extends BaseJsonParser {

	public ArrayList<String> channelList;
	public ArrayList<ShortChannelData> channelInfo;
	public ArrayList<NetPlayData> livePlayData;
	public Map<Long,String> leftChannel;

	private String umengValue;

	public LiveParser(String umengValue) {
		this.umengValue = umengValue;
	}

	@Override
	public void parse(JSONObject jsonObject) {
		channelList = new ArrayList<String>();
		channelInfo = new ArrayList<ShortChannelData>();
		livePlayData = new ArrayList<NetPlayData>();
		leftChannel = new HashMap<Long,String>();

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
				if (content.has("type")) {
					JSONArray channelTypes = content.getJSONArray("type");
					int size = channelTypes.length();
					for (int i = 0; i < size; i++) {
						JSONObject channelType = channelTypes.getJSONObject(i);
						String name = channelType.optString("name");
						long cId = channelType.optLong("id");
						if ("anzhuo".equalsIgnoreCase(umengValue) && "港澳台".equals(name)) {
							continue;
						} else {
							channelList.add(name);
							leftChannel.put(cId,name);
						}
						if (channelType.has("channel")) {
							JSONArray channelInfo = channelType.optJSONArray("channel");
							for (int j = 0; j < channelInfo.length(); j++) {
								JSONObject channelData = channelInfo.optJSONObject(j);
								ShortChannelData data = new ShortChannelData();
								data.channelId = channelData.optInt("id");
								data.channelName = channelData.optString("name");
								data.channelPicUrl = channelData.optString("pic");
								data.channelType = channelData
										.optInt("channelType");
								data.programId = channelData
										.optInt("cpProgramId");
								data.flagMyChannel = channelData
										.optInt("isMyChannel") == 1 ? true : false;
								data.programName = channelData
										.optString("cpName");
								data.cpId = channelData
										.optInt("cpId");
								data.typeName = name;
								data.haveProgram = channelData.optBoolean("haveProgram", true);
                                data.toWeb = channelData.optInt("skipWeb")==1?true:false;
								String startTime = channelData
										.optString("cpStartTime");
								String endTime = channelData
										.optString("cpEndTime");
								if (startTime != null && endTime != null) {
									data.startTime = startTime;
									data.endTime = endTime;
									setSpannelStyle(startTime, endTime,
											data);
								}
								if (channelData.has("play")) {
									data.livePlay = true;
									JSONArray play = channelData.optJSONArray("play");
									livePlayData = new ArrayList<NetPlayData>();
									for (int k = 0; k < play.length(); k++) {
										JSONObject obj = play.getJSONObject(k);
										NetPlayData playData = new NetPlayData();
										playData.url = obj.optString("url");
										playData.videoPath = obj.optString("videoPath");
										playData.platformId = (int) obj.optLong("platformId");
										playData.channelName = data.channelName;
										playData.channelIdStr = obj.optString("channelIdStr");
										playData.showUrl = obj.optString("showUrl");
										playData.webPage = obj.optString("webPage");
										playData.id = obj.optInt("id");
										livePlayData.add(playData);
									}
								}
								data.netPlayDatas = livePlayData;
								this.channelInfo.add(data);
							}
						}
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
