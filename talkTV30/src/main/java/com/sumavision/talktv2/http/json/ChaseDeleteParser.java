package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

/**
 * @author suma-hpb
 * @version 3.0
 * @description 取消收藏
 */
public class ChaseDeleteParser extends BaseJsonParser {

	@Override
	public void parse(JSONObject object) {
		if (object.has("code")) {
			errCode = object.optInt("code");
		} else if (object.has("errcode")) {
			errCode = object.optInt("errcode");
		} else if (object.has("errorCode")) {
			errCode = object.optInt("errorCode");
		}
		errMsg = object.optString("msg");
	}
}
