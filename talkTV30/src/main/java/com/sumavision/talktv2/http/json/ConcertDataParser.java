package com.sumavision.talktv2.http.json;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ConcertData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 演唱会数据
 * */
public class ConcertDataParser extends BaseJsonParser {

	public ConcertData concertData;

	@Override
	public void parse(JSONObject jsonObject) {
		concertData = new ConcertData();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.optJSONObject("content");
					if (content != null && content.has("live")) {
						JSONObject live = jsonObject.optJSONObject("live");
						if (live != null) {
							concertData.id = live.optLong("id");
							concertData.payRuleId = live.optLong("payRuleId");
							concertData.title = live.optString("title");
							concertData.price = (float) live.optDouble("price");
							concertData.hasBuy = live.optBoolean("hasBuy");
							concertData.startTime = live.optLong("startTime");
							concertData.endTime = live.optLong("endTime");
							concertData.content = live.optString("content");
							JSONArray pic = live.optJSONArray("pic");
							JSONArray video = live.optJSONArray("video");
							for (int i = 0; i < pic.length(); i++) {
								JSONObject picObj = pic.optJSONObject(i);
								String text = picObj.optString("text");
								String url = picObj.optString("url");
								concertData.picName.add(text);
								concertData.picUrl.add(url);
							}
							for (int j = 0; j < video.length(); j++) {
								JSONObject videoObj = video.optJSONObject(j);
								NetPlayData net = new NetPlayData();
								net.title = videoObj.optString("title");
								net.pic = videoObj.optString("pic");
								net.url = videoObj.optString("webUrl");
								net.videoPath = videoObj.optString("videoPath");
								concertData.videoList.add(net);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
