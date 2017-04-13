package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 密码找回json解析
 * 
 * @author suma-hpb
 * 
 */
public class ForgetInitParser extends BaseJsonParser {
	public String email;

	@Override
	public void parse(JSONObject jsonObject) {
		if (jsonObject.has("code")) {
			errCode = jsonObject.optInt("code");
		} else if (jsonObject.has("errcode")) {
			errCode = jsonObject.optInt("errcode");
		} else if (jsonObject.has("errorCode")) {
			errCode = jsonObject.optInt("errorCode");
		}
		if (jsonObject.has("jsession")) {
			UserNow.current().jsession = jsonObject.optString("jsession");
		}

		if (jsonObject.has("sessionId")) {
			UserNow.current().sessionID = jsonObject.optString("sessionId");
		}

		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			JSONObject user = jsonObject.optJSONObject("content");
			if (user != null) {
				UserModify.current().userId = user.optInt("userId");
				email = user.optString("email");
			} else {
				UserNow.current().isLogedIn = false;
			}
		} else {
			errMsg = jsonObject.optString("msg");
		}

	}

}
