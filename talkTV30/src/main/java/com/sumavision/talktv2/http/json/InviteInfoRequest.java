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
 *  提交邀请码
 */
public class InviteInfoRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.inviteInfo);
			holder.put("version", JSONMessageType.APP_VERSION_314);
			holder.put("client", JSONMessageType.SOURCE);
			if (UserNow.current().userID > 0) {
				holder.put("userId", UserNow.current().userID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
