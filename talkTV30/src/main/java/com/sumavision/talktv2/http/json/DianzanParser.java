package com.sumavision.talktv2.http.json;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;

/**
 * 点赞解析
 * 
 * @author suma-hpb
 * 
 */
public class DianzanParser extends BaseJsonParser {

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		UserNow.current().jsession = jsonObject.optString("jsession");
		errMsg = jsonObject.optString("msg");
	}

}
