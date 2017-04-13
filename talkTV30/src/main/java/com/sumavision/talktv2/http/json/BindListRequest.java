package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class BindListRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.bindList);
			requestObj.put("version", "2.2");
			requestObj.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return requestObj;
	}

}
