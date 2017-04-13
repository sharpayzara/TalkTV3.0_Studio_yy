package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 演唱会验证数据
 * */
public class ConcertTicketParser extends BaseJsonParser {

	public boolean result;
	public String videoPath;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.optJSONObject("content");
					if (content != null) {
						result = content.optBoolean("result");
						videoPath = content.optString("videoPath");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
