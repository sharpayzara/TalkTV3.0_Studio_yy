package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * @author hpb
 * @version v2.2
 * @description 用户资料更新请求
 */
public class UserUpdateRequest extends BaseJsonRequest {
	UserModify userModify;

	public UserUpdateRequest(UserModify userModify) {
		this.userModify = userModify;
	}

	@Override
	public JSONObject make() {

		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.userUpdate);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("userId", UserNow.current().userID);
			holder.put("sessionId", UserNow.current().sessionID);
			if (userModify.passwdNewFlag == 1) {
				holder.put("password", userModify.passwdNew);
				holder.put("oldPassword", userModify.passwdOld);
			}
			if (userModify.eMailFlag == 1) {
				holder.put("email", userModify.eMail);
				holder.put("oldPassword", userModify.passwdOld);
			}
			if (userModify.nameNewFlag == 1)
				holder.put("userName",
						StringUtils.AllStrTOUnicode(userModify.nameNew));
			if (userModify.signFlag == 1)
				holder.put("signature",
						StringUtils.AllStrTOUnicode(userModify.sign));
			if (userModify.genderFlag == 1)
				holder.put("sex", userModify.gender);
			if (userModify.picFlag == 1)
				holder.put("pic", userModify.pic_Base64);
			holder.put("jsession", UserNow.current().jsession);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
