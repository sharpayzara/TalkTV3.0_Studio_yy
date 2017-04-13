package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

/**
 * 密码找回json请求
 * 
 * @author suma-hpb
 * 
 */
public class ForgetInitRequest extends BaseJsonRequest {
	private String input;

	public ForgetInitRequest(String input) {
		this.input = input;
	}

	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			if (AppUtil.isEmail(input)) {
				jsonObject.put("email", input);
			} else {
				jsonObject.put("username", input);
			}
			jsonObject.put("method", Constants.pwdRecovery);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	};

}
