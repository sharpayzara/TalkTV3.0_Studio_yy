package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class ProgramHeaderRequest extends BaseJsonRequest {
	long programId;
	long cpId;

	public ProgramHeaderRequest(long programId, long cpId) {
		super();
		this.programId = programId;
		this.cpId = cpId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.programVideoDetail);
			jsonObject.put("version", "2.3.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			int userId = UserNow.current().userID;
			if (userId != 0) {
				jsonObject.put("userId", userId);
			}
			jsonObject.put("programId", programId);
			if (cpId != 0) {
				jsonObject.put("cpId", cpId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
