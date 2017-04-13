package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 电视粉账号登录解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class LogInParser extends BaseJsonParser {
	public String errMsg;

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
				UserNow.current().isVip = user.optInt("isVip")==1 ? true:false;

				if (user != null) {
					setUserInfo(user);
					JSONObject userNowInfo = user.optJSONObject("newUserInfo");
					setPointInfo(userNowInfo);
					UserNow.current().isSelf = true;
					UserNow.current().isLogedIn = true;
					UserNow.current().dayLoterry = user.optInt("dayLottery");
				} else {
					UserNow.current().isLogedIn = false;
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
