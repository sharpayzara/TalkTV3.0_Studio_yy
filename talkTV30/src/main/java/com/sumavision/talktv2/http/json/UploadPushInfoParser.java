package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadPushInfoParser extends BaseJsonParser {

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
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
