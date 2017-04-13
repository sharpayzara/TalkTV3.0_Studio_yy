package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 频道详情请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelDetailRequest extends BaseJsonRequest {

	private int userId;
	private String date;
	private int channelId;

	public ChannelDetailRequest(int userId, String date, int channelId) {
		super();
		this.userId = userId;
		this.date = date;
		this.channelId = channelId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.channelContent);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (userId != 0)
				jsonObject.put("userId", userId);
			jsonObject.put("date", date);
			jsonObject.put("style", 0);
			jsonObject.put("channelId", channelId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
