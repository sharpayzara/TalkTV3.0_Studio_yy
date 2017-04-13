package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;


import com.sumavision.talktv2.bean.UserNow;

/**
 * 添加预约解析解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class AddRemindParser extends BaseJsonParser {
	
	public int remindId;

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
			errMsg = jsonObject.optString("msg");
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null) {
				JSONObject remind = content.optJSONObject("remind");
				if (remind != null) {
					remindId = remind.optInt("id");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
