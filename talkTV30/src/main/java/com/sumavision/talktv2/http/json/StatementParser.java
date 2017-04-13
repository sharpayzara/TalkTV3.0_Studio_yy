package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

/**
 * 版权声明解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StatementParser extends BaseJsonParser {
	
	public String statement;

	@Override
	public void parse(JSONObject jsonObject) {
		if (jsonObject.has("code")) {
			errCode = jsonObject.optInt("code");
		} else if (jsonObject.has("errcode")) {
			errCode = jsonObject.optInt("errcode");
		} else if (jsonObject.has("errorCode")) {
			errCode = jsonObject.optInt("errorCode");
		}
		if (jsonObject.has("content")) {
			JSONObject obj = jsonObject.optJSONObject("content");
			if (obj != null) {
				statement = obj.optString("detail");
			}
		}
	}

}
