package com.sumavision.talktv2.http.json;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 绑定登录请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class bindLogInRequest extends BaseJsonRequest {
	ThirdPlatInfo thirdInfo;

	/**
	 * @param thirdInfo
	 */
	public bindLogInRequest(ThirdPlatInfo thirdInfo) {
		this.thirdInfo = thirdInfo;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.bindLogIn);
			jsonObject.put("version", JSONMessageType.APP_VERSION);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);

			jsonObject.put("thirdType", thirdInfo.type);
			jsonObject.put("thirdToken", thirdInfo.token);
			jsonObject.put("thirdUserId",
					TextUtils.isEmpty(thirdInfo.userId) ? thirdInfo.openId
							: thirdInfo.userId);
			if (!TextUtils.isEmpty(UserNow.current().name)) {
				jsonObject.put("userName",
						StringUtils.AllStrTOUnicode(UserNow.current().name));
				jsonObject.put("userType", UserNow.current().userType);
				jsonObject.put("password", UserNow.current().passwd);
			}
			if (!TextUtils.isEmpty(thirdInfo.userName)){
				jsonObject.put("userName",
						StringUtils.AllStrTOUnicode(thirdInfo.userName));
			}
			if (!TextUtils.isEmpty(UserNow.current().eMail))
				jsonObject.put("email", UserNow.current().eMail);
			if (!TextUtils.isEmpty(thirdInfo.userIconUrl))
				jsonObject.put("thirdUserPic", thirdInfo.userIconUrl);
			if (!TextUtils.isEmpty(thirdInfo.userSignature))
				jsonObject.put("thirdSignature", thirdInfo.userSignature);
			if (!TextUtils.isEmpty(thirdInfo.expiresIn))
				jsonObject.put("validTime", thirdInfo.expiresIn);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
