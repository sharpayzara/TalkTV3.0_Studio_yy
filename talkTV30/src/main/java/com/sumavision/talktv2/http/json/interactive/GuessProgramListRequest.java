package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;

/**
 * 竞猜节目列表json请求
 * 
 * @author suma-hpb
 * 
 */
public class GuessProgramListRequest extends BaseJsonRequest {
	private int activityId;
	private int first;
	private int count;

	/**
	 * @param activityId
	 * @param first
	 * @param count
	 */
	public GuessProgramListRequest(int activityId, int first, int count) {
		super();
		this.activityId = activityId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", "interactiveGuessProgramList");
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("activityId", activityId);
			object.put("first", first);
			object.put("count", count);
		} catch (JSONException e) {
			Log.e("InteractiveGuessProgramListTask", e.toString());
		}
		return object;
	}
}
