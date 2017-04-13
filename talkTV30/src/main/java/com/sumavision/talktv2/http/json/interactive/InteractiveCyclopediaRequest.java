package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.Interactive;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 百科请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveCyclopediaRequest extends BaseJsonRequest {

	private int first;
	private int count;

	public InteractiveCyclopediaRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveBaikeList);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("interactiveId", Interactive.getInstance().id);
			object.put("first", first);
			object.put("count", count == 0 ? 20 : count);
		} catch (JSONException e) {
			Log.e("InteractiveCyclopediaListTask-request", e.toString());
		}
		return object;
	}

}
