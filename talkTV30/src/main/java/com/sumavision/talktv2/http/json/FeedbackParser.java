package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 反馈数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackParser extends BaseJsonParser {

	public UserNow user;

	@Override
	public void parse(JSONObject jsonObject) {
		user = new UserNow();
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
			if (errCode != JSONMessageType.SERVER_CODE_OK) {
				return;
			}
			if (jsonObject.has("newUserInfo")) {
				JSONObject info = jsonObject.getJSONObject("newUserInfo");
				setPointInfo(info);
				user = UserNow.current();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
