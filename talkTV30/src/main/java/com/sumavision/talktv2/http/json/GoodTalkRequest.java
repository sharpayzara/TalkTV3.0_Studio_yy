package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 	好评请求
 * 
 * @author cx
 * @version
 * @description
 */
public class GoodTalkRequest extends BaseJsonRequest {

	private int objectType;

	public GoodTalkRequest(int objectType) {
		this.objectType = objectType;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.goodTalkAddPoint);
			jsonObject.put("version", "3.0.3");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID > 0) {
				jsonObject.put("userId", UserNow.current().userID);
			}
			jsonObject.put("objectType", objectType);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
