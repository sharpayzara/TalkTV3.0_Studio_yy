package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;


import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 添加预约请求
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class AddRemindRequest extends BaseJsonRequest {

	private int userId;
	private int programId;

	public AddRemindRequest(int userId, int programId) {
		this.userId = userId;
		this.programId = programId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.remindAdd);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (userId != 0)
				jsonObject.put("userId", userId);
			jsonObject.put("cpId", programId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
