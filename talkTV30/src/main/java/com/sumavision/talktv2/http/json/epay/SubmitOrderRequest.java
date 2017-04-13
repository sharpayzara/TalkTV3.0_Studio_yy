package com.sumavision.talktv2.http.json.epay;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 提交订单json请求
 * 
 * @author suma-hpb
 * 
 */
public class SubmitOrderRequest extends BaseJsonRequest {
	private String mPayRuleId;

	public SubmitOrderRequest(String mPayRuleId) {
		this.mPayRuleId = mPayRuleId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("method", Constants.submitOrder);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("payRuleId", mPayRuleId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
