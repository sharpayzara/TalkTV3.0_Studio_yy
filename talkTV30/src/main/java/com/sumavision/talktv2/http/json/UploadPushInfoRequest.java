package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 上传推送信息请求
 * 
 * @author suma-hpb
 * 
 */
public class UploadPushInfoRequest extends BaseJsonRequest {
	String userId;
	String channelId;
	int tvfanuserId;

	public UploadPushInfoRequest(String userId, String channelId,
			int tvfanuserId) {
		this.userId = userId;
		this.channelId = channelId;
		this.tvfanuserId = tvfanuserId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", "baiduyunBindAdd");
			jsonObject.put("version", "1.0.5");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("deviceType", 3);
			jsonObject.put("userId", userId);
			jsonObject.put("channelId", channelId);
			if (tvfanuserId > 0) {
				jsonObject.put("tvfanUserId", tvfanuserId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
