package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 推荐软件下载json请求
 * 
 * @author suma-hpb
 * 
 */
public class DownloadRecommendAppRequest extends BaseJsonRequest {
	private long appId;

	public DownloadRecommendAppRequest(long appId) {
		this.appId = appId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", "downloadRecommendApp");
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("recommendAppId", appId);
		} catch (JSONException e) {
			e.printStackTrace();
			return jsonObject;
		}
		return jsonObject;
	}

}
