package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.PhotoData;
import com.sumavision.talktv2.bean.ProgramDetailInfoData;
import com.sumavision.talktv2.bean.StarData;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 节目详情解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ProgramDetailParser extends BaseJsonParser {

	public ProgramDetailInfoData programDetailInfo = new ProgramDetailInfoData();

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

				programDetailInfo.intro = programJSON.optString("intro");
				programDetailInfo.pic = programJSON.optString("pic");

				if (content.has("star")) {
					JSONArray starJSONS = content.getJSONArray("star");
					ArrayList<StarData> stars = new ArrayList<StarData>();
					for (int i = 0; i < starJSONS.length(); ++i) {
						JSONObject signUserJSON = starJSONS.getJSONObject(i);
						StarData star = new StarData();
						star.stagerID = signUserJSON.optInt("id");
						star.name = signUserJSON.optString("name");
						star.photoBig_V = signUserJSON.optString("pic");
						stars.add(star);
					}
					programDetailInfo.stars = stars;
				}
				if (content.has("stagePhoto")) {
					JSONArray starJSONS = content.getJSONArray("stagePhoto");
					ArrayList<PhotoData> photos = new ArrayList<PhotoData>();
					for (int i = 0; i < starJSONS.length(); ++i) {
						JSONObject signUserJSON = starJSONS.getJSONObject(i);
						PhotoData photoData = new PhotoData();
						photoData.url = signUserJSON.optString("pic");
						photos.add(photoData);
					}
					programDetailInfo.photos = photos;
				}
				if (content.has("channel")) {
					JSONArray channels = content.getJSONArray("channel");
					ArrayList<ChannelData> lc = new ArrayList<ChannelData>();
					for (int i = 0; i < channels.length(); ++i) {
						ChannelData c = new ChannelData();
						JSONObject channel = channels.getJSONObject(i);
						c.channelName = channel.getString("name");
						CpData cp = new CpData();
						cp.id = channel.optInt("cpId");
						cp.name = channel.optString("cpName");
						cp.startTime = channel.optString("startTime");
						cp.endTime = channel.optString("endTime");
						cp.isPlaying = channel.optInt("playing");
						cp.playUrl = channel.optString("playUrl");
						cp.order = channel.optInt("isRemind");
						cp.remindId = channel.optLong("remindId");
						if (channel.has("play")) {
							JSONArray play = channel.getJSONArray("play");
							c.netPlayDatas = new ArrayList<NetPlayData>();
							for (int x = 0; x < play.length(); x++) {
								NetPlayData netPlayData = new NetPlayData();
								JSONObject playItem = play.getJSONObject(x);
								netPlayData.name = playItem.optString("name");
								netPlayData.pic = playItem.optString("pic");
								netPlayData.url = playItem.optString("url");
								netPlayData.videoPath = playItem
										.optString("video");
								netPlayData.platformId = playItem
										.optInt("platformId");
								c.netPlayDatas.add(netPlayData);
							}
						}
						c.now = cp;
						lc.add(c);
					}
					programDetailInfo.channels = lc;
				}
			}
		} catch (JSONException e) {
		}

	}

}
