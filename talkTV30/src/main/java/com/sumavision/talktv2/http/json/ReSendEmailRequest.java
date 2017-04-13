package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.utils.Constants;

/**
 * 密码找回--重新发送邮件
 * 
 * @author suma-hpb
 * 
 */
public class ReSendEmailRequest extends BaseJsonRequest {
	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.pwdRecovery);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("userId", UserModify.current().userId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
