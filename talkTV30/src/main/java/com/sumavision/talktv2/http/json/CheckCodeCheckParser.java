package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 密码找回-校验验证码json解析
 * 
 * @author suma-hpb
 * 
 */
public class CheckCodeCheckParser extends BaseJsonParser {

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}

			if (jsonObject.has("sessionId")) {
				UserNow.current().sessionID = jsonObject.getString("sessionId");
			}

			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject user = jsonObject.optJSONObject("content");
				if (user != null) {
					UserModify.current().isCode = user.optBoolean("success");
				} else {
					UserModify.current().isCode = false;
				}
			} else {
				errMsg = jsonObject.getString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
