package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author suma-hpb
 * @version 3.0
 * @description 徽章详情接口
 * @changeLog
 */
public class BadgeDetailRequest extends BaseJsonRequest {

	private int badgeId;

	public BadgeDetailRequest(int badgeId) {
		this.badgeId = badgeId;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.badgeDetail);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", UserNow.current().userID);
			holder.put("badgeId", badgeId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
