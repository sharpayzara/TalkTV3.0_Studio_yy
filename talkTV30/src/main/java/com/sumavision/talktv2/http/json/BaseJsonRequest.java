package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Logcat;

public abstract class BaseJsonRequest {
	public abstract JSONObject make();

	protected Logcat mLog = new Logcat();
	protected JSONObject requestObj = new JSONObject();

	protected void addMethod(String name) throws JSONException {
		requestObj.put("method", name);
		requestObj.put("client", JSONMessageType.SOURCE);
		requestObj.put("jsession", UserNow.current().jsession);
	}
}
