package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 反馈请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackDetailRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.feedbackDetail);
			requestObj.put("version", JSONMessageType.APP_VERSION_311);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return requestObj;
	}

}
