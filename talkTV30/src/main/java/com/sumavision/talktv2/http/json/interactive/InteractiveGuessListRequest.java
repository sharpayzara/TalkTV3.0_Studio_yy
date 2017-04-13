package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 竞猜列表json请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessListRequest extends BaseJsonRequest {

	private int activityId;
	private int first;
	private int count;

	/**
	 * @param activityId
	 * @param first
	 * @param count
	 */
	public InteractiveGuessListRequest(int activityId, int first, int count) {
		super();
		this.activityId = activityId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveGuessList);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("activityId", activityId);
			object.put("userId", UserNow.current().userID);
			object.put("first", first);
			object.put("count", count);
		} catch (JSONException e) {
			Log.e("InteractiveGuessListTask-request", e.toString());
		}
		return object;
	}

}
