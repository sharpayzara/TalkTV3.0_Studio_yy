package com.sumavision.talktv2.http.json;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * @author 
 * @version 2.2
 * @description 用户注册请求组装类
 */
public class RegisterRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("userName",
					StringUtils.AllStrTOUnicode(UserNow.current().name));
			holder.put("password", UserNow.current().passwd);
			holder.put("method", Constants.register);
			holder.put("email", UserNow.current().eMail);
			if (!TextUtils.isEmpty(UserNow.current().inviteCode)){
				holder.put("inviteCode",UserNow.current().inviteCode);
			}
			holder.put("imei",UserNow.current().imei);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("jsession", UserNow.current().jsession);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
