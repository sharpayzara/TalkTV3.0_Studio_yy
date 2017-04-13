package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 互动支持参与json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveSupportParser extends BaseJsonParser {

	@Override
	public void parse(JSONObject obj) {
		errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
	}

}
