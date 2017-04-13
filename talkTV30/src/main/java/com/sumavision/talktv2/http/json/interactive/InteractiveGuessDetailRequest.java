package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 
 * 竞猜详情请求
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessDetailRequest extends BaseJsonRequest {

	private int guessId;

	public InteractiveGuessDetailRequest(int guessId) {
		super();
		this.guessId = guessId;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveGuessDetail);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("guessId", guessId);
			object.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			Log.e("InteractiveGuessDetailTask-request", e.toString());
		}
		return object;
	}

}
