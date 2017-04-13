package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 账号绑定请求封装
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindAccountRequest extends BaseJsonRequest {

	ThirdPlatInfo thirdInfo;

	/**
	 * @param thirdToken
	 */
	public BindAccountRequest(ThirdPlatInfo thirdInfo) {
		this.thirdInfo = thirdInfo;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {

			holder.put("method", Constants.bindUser);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("jsession", UserNow.current().jsession);

			holder.put("thirdType", thirdInfo.type);
			holder.put("thirdToken", thirdInfo.token);
			holder.put("thirdUserId",
					TextUtils.isEmpty(thirdInfo.userId) ? thirdInfo.openId
							: thirdInfo.userId);
			if (!TextUtils.isEmpty(UserNow.current().name)) {
				holder.put("userName",
						StringUtils.AllStrTOUnicode(UserNow.current().name));
				holder.put("userType", UserNow.current().userType);
				holder.put("password", UserNow.current().passwd);
			}
			if (!TextUtils.isEmpty(UserNow.current().eMail))
				holder.put("email", UserNow.current().eMail);
			if (!TextUtils.isEmpty(thirdInfo.userIconUrl))
				holder.put("thirdUserPic", thirdInfo.userIconUrl);
			if (!TextUtils.isEmpty(thirdInfo.userSignature))
				holder.put("thirdSignature", thirdInfo.userSignature);
			if (!TextUtils.isEmpty(thirdInfo.expiresIn))
				holder.put("validTime", thirdInfo.expiresIn);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
