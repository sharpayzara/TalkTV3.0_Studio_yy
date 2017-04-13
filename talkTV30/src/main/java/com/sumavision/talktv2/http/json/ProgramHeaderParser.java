package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.ProgramVideoData;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

public class ProgramHeaderParser extends BaseJsonParser {

	public ProgramData programData = new ProgramData();

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
				JSONObject programJSON = content.getJSONObject("program");
				programData.programId = programJSON.optLong("id");
				programData.topicId = programJSON.optLong("topicId");
				programData.name = programJSON.optString("name");
				programData.pic = programJSON.optString("pic");
				programData.director = programJSON.optString("director");
				programData.actors = programJSON.optString("stager");
				programData.region = programJSON.optString("area");
				programData.doubanPoint = programJSON.optDouble("doubanPoint");
				programData.pType = programJSON.optInt("pType");
				programData.isSigned = content.getInt("isSigned") == 1 ? true
						: false;
				programData.isChased = content.getInt("isChased") == 1 ? true
						: false;
				programData.signCount = content.getInt("signCount");
				if (content.has("signUser")) {
					JSONArray signUserJSONS = content.getJSONArray("signUser");
					ArrayList<User> signUsers = new ArrayList<User>();
					for (int i = 0; i < signUserJSONS.length(); ++i) {
						JSONObject signUserJSON = signUserJSONS
								.getJSONObject(i);
						User user = new User();
						user.userId = signUserJSON.optInt("id");
						user.iconURL = signUserJSON.optString("pic");
						signUsers.add(user);
					}
					programData.signUsers = signUsers;
				}
				if (content.has("labelList")) {
					JSONArray labelList = content.getJSONArray("labelList");
					int size = labelList.length();
					if (size != 0) {
						ArrayList<ProgramVideoData> videos = new ArrayList<ProgramVideoData>();
						for (int i = 0; i < size; i++) {
							JSONObject videoJSON = labelList.getJSONObject(i);
							ProgramVideoData video = new ProgramVideoData();
							video.id = videoJSON.optInt("id");
							video.name = videoJSON.optString("name");
							videos.add(video);
						}
						programData.videoData = videos;
					}
				}
				if (content.has("playChannel")) {
					JSONArray channelList = content.getJSONArray("playChannel");
					int size = channelList.length();
					if (size != 0) {
						ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
						for (int i = 0; i < size; ++i) {
							NetPlayData netPlayData = new NetPlayData();
							JSONObject playItem = channelList.getJSONObject(i);
							netPlayData.name = playItem.optString("name");
							netPlayData.pic = playItem.optString("pic");
							netPlayData.url = playItem.optString("url");
							netPlayData.videoPath = playItem.optString("video");
							netPlayData.programChannelId = playItem
									.optLong("channelId");
							netPlayData.platformId = playItem
									.optInt("platformId");

							netPlayData.playType = 1;
							netPlayDatas.add(netPlayData);
						}
						programData.netPlayDatas = netPlayDatas;
					}
				}

			}
		} catch (JSONException e) {
		}

	}

}
