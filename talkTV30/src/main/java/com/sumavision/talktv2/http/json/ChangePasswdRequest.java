package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.utils.Constants;

/**
 * 修改密码json请求
 * 
 * @author suma-hpb
 * 
 */
public class ChangePasswdRequest extends BaseJsonRequest {
	private String checkCode;
	private String password;

	/**
	 * @param checkCode
	 * @param password
	 */
	public ChangePasswdRequest(String checkCode, String password) {
		this.checkCode = checkCode;
		this.password = password;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.pwdReset);
			jsonObject.put("version", "2.6.0");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("userId", UserModify.current().userId);
			jsonObject.put("password", password);
			jsonObject.put("checkCode", checkCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

}
