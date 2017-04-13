package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;

/**
 * 节目页ushow开关
 * 
 * @author suma-hpb
 * 
 */
public class UshowOnOffParser extends BaseJsonParser {

	public boolean isOpen;

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			JSONObject content = jsonObject.optJSONObject("content");
			isOpen = content.optBoolean("status");
		}

	}

}
