package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class LiveDetailRequest extends BaseJsonRequest{
	
	private int channelId;
	
	public LiveDetailRequest(int channelId) {
		this.channelId = channelId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.channelContent);
			jsonObject.put("version", "3.0.7");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("channelId", channelId);
			jsonObject.put("p2pType", 2);
			if (UserNow.current().userID > 0) {
				jsonObject.put("userId", UserNow.current().userID);
			}
			Log.i("mylog", jsonObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
