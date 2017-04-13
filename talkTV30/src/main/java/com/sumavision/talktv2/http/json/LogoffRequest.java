package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 注销请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class LogoffRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", "cancel");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("sessionId", UserNow.current().sessionID);
			holder.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0) {
				holder.put("userId", UserNow.current().userID);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
