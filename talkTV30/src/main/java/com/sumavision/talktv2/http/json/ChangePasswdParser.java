package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 修改密码json解析
 * 
 * @author suma-hpb
 * 
 */
public class ChangePasswdParser extends BaseJsonParser {
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
				UserNow.current().sessionID = jsonObject.optString("sessionId");
			}

			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject user = jsonObject.getJSONObject("content");
				if (user != null) {
					UserNow.current().name = user.optString("userName");
					UserNow.current().eMail = UserModify.current().eMail;
					UserModify.current().passwdNewFlag = 0;
					UserModify.current().eMailFlag = 0;
					UserModify.current().genderFlag = 0;
					UserModify.current().introflag = 0;
					UserModify.current().nameNewFlag = 0;
				} else {
					UserNow.current().isLogedIn = false;
					errMsg = jsonObject.getString("msg");
				}
			} else {
				errMsg = jsonObject.getString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
