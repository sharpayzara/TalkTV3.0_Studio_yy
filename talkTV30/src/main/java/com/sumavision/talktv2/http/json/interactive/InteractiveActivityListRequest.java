package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.Interactive;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 互动列表json请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityListRequest extends BaseJsonRequest {

	private int first;
	private int count;

	public InteractiveActivityListRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveActivityList);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("interactiveId", Interactive.getInstance().id);
			object.put("first", first);
			object.put("count", count);
		} catch (JSONException e) {
			Log.e("InteractiveActivityListTask", e.toString());
		}
		return object;
	}

}
