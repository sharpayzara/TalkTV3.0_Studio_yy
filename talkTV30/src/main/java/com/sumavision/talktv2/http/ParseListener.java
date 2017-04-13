package com.sumavision.talktv2.http;

import com.android.volley.Response.Listener;
import com.sumavision.talktv2.http.json.BaseJsonParser;

import org.json.JSONObject;

/**
 * mainThread中数据响应解析及传递
 * 
 * @author suma-hpb
 * 
 */
public abstract class ParseListener implements Listener<JSONObject>  {
	BaseJsonParser parser;

	public ParseListener(BaseJsonParser parser) {
		this.parser = parser;
	}

	@Override
	public void onResponse(JSONObject arg0) {
		parser.parse(arg0);
		onParse(parser);
	}

	public abstract void onParse(BaseJsonParser parser);
}
