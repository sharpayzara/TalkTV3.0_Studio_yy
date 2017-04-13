package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 用户竞猜列表json请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveUserGuessListRequest extends BaseJsonRequest {
	int first;
	int count;

	public InteractiveUserGuessListRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.userInteractiveGuessList);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("userId", UserNow.current().userID);
			object.put("first", first);
			object.put("count", count == 0 ? 20 : count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
