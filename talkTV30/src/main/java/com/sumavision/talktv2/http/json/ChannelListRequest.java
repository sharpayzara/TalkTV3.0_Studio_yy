package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 频道列表请求数据封装
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelListRequest extends BaseJsonRequest {

	private int userId;
	private int typeId;
	private int first;
	private int count;

	public ChannelListRequest(int userId, int typeId, int first, int count) {
		super();
		this.userId = userId;
		this.typeId = typeId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.channelTypeDetail);
			jsonObject.put("version", JSONMessageType.APP_VERSION_NINE);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (userId != 0)
				jsonObject.put("userId", userId);
			jsonObject.put("first", first);
			jsonObject.put("count", count);
			jsonObject.put("channelTypeId", typeId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
