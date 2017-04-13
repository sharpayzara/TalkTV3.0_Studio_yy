package com.sumavision.talktv2.http.json;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 看视频加经验
 */
public class MyDebrisRequest extends BaseJsonRequest {

	private int first,count;

	public MyDebrisRequest(int first, int count) {
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.myDebris);
			holder.put("version", JSONMessageType.APP_VERSION_314);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("first",first);
			holder.put("count", count);
			if (UserNow.current().userID > 0) {
				holder.put("userId", UserNow.current().userID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
