package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.utils.Constants;

/**
 * 用户事件请求
 * 
 * @author suma-hpb
 * 
 */
public class UserEventRequest extends BaseJsonRequest {

	int userId;
	int first;
	int count;

	public UserEventRequest(int userId, int first, int count) {
		super();
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.userEventList);
			requestObj.put("version", "2.2");
			requestObj.put("userId", userId);
			requestObj.put("first", first);
			requestObj.put("count", count);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
