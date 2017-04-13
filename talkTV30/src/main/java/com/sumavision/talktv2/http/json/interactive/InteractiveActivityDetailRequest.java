package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 互动活动详情json请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityDetailRequest extends BaseJsonRequest {

	private int interactiveActivityId;

	/**
	 * @param interactiveActivityId
	 */
	public InteractiveActivityDetailRequest(int interactiveActivityId) {
		super();
		this.interactiveActivityId = interactiveActivityId;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveActivityDetail);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("activityId", interactiveActivityId);
			object.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			Log.e("interactActivityDetailTask-request", e.toString());
		}
		return object;
	}

}
