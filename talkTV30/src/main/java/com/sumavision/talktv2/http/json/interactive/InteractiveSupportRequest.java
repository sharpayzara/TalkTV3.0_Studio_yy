package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 互动支持参与json请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveSupportRequest extends BaseJsonRequest {

	private int activityId;
	private int support;

	public InteractiveSupportRequest(int activityId, int support) {
		super();
		this.activityId = activityId;
		this.support = support;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveUserSupport);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("activityId", activityId);
			object.put("support", support);
			object.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			Log.e("InteractiveSupportRequest", e.toString());
		}
		return object;
	}

}
