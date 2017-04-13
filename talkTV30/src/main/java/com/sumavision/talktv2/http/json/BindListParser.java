package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;

public class BindListParser extends BaseJsonParser {

	public ArrayList<ThirdPlatInfo> bindlist;

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		bindlist = new ArrayList<ThirdPlatInfo>();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			JSONObject content = jsonObject.optJSONObject("content");
			JSONArray cArr = content.optJSONArray("client");
			if (cArr != null) {
				for (int i = 0, len = cArr.length(); i < len; i++) {
					ThirdPlatInfo data = new ThirdPlatInfo();
					JSONObject obj = cArr.optJSONObject(i);
					data.bindId = obj.optInt("id");
					data.token = obj.optString("token");
					data.openId = obj.optString("userId");
					data.expiresIn = obj.optString("validTime");
					data.type = obj.optInt("type");
					bindlist.add(data);
				}
			}
		}

	}

}
