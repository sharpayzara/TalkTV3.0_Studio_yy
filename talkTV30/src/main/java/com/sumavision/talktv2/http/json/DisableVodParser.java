package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

/**
 * 点播源过滤解析
 * 
 * 
 */
public class DisableVodParser extends BaseJsonParser {
	
	public boolean result;

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		JSONObject content = jsonObject.optJSONObject("content");
		if (content != null) {
			result = content.optBoolean("result");
		}
	}

}
