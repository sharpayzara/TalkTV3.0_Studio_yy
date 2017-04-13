package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 
 */
public class ProgramHalfRecommendRequest extends BaseJsonRequest {
	int programId;

	public ProgramHalfRecommendRequest(int programId) {
		this.programId = programId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.programRecommend);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", "3.0.9");
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("programId", programId);
			if (UserNow.current().userID>0){
				jsonObject.put("userId",UserNow.current().userID);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
