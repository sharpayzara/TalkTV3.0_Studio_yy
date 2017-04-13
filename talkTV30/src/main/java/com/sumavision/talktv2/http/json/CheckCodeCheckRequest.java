package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.utils.Constants;

/**
 * 密码找回-验证码校验json请求
 * 
 * @author suma-hpb
 * 
 */
public class CheckCodeCheckRequest extends BaseJsonRequest {
	private String checkCode;

	public CheckCodeCheckRequest(String checkCode) {
		this.checkCode = checkCode;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.checkCheckCode);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", "1");
			jsonObject.put("userId", UserModify.current().userId);
			jsonObject.put("checkCode", checkCode);

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
