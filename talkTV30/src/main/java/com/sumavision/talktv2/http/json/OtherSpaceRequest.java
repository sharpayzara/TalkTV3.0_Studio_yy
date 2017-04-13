package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author
 * @version 3.0
 * @description 其他中心请求类
 * @changeLog
 */
public class OtherSpaceRequest extends BaseJsonRequest {

	private int userId;

	public OtherSpaceRequest(int userId) {
		this.userId = userId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.userSpace);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			if (UserNow.current().userID != 0)
				holder.put("userId", UserNow.current().userID);
			holder.put("otherUserId", userId);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
