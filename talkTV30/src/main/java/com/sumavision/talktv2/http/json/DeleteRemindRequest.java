package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 取消预约
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DeleteRemindRequest extends BaseJsonRequest {

	private int userId;
	private String remindIds;

	public DeleteRemindRequest(int userId, String remindIds) {
		this.userId = userId;
		this.remindIds = remindIds;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.remindDelete);
			jsonObject.put("version", "3.0.1");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (userId != 0)
				jsonObject.put("userId", userId);
			jsonObject.put("remindIds", remindIds);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
