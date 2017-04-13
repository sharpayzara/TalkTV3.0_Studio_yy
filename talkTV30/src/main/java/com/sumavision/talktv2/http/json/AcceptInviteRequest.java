package com.sumavision.talktv2.http.json;

import android.app.Activity;
import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImeiUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 提交邀请码
 */
public class AcceptInviteRequest extends BaseJsonRequest {
	String code;
	Context context;
	public AcceptInviteRequest(Context context,String code){
		this.code = code;
		this.context = context;
	}
	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.acceptInvite);
			holder.put("version", JSONMessageType.APP_VERSION_314);
			holder.put("client", JSONMessageType.SOURCE);
			if (UserNow.current().userID > 0) {
				holder.put("userId", UserNow.current().userID);
			}
			holder.put("imei", ImeiUtil.getInstance(context).getUniqueId());
			holder.put("inviteCode",code);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
