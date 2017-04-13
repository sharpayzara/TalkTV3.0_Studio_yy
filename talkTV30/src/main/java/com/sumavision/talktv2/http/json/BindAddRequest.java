package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 添加帐号绑定
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindAddRequest extends BaseJsonRequest {

	ThirdPlatInfo thirdInfo;

	/**
	 * @param thirdInfo
	 */
	public BindAddRequest(ThirdPlatInfo thirdInfo) {
		this.thirdInfo = thirdInfo;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.bindAdd);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("thirdType", thirdInfo.type);
			holder.put("thirdToken", thirdInfo.token);
			holder.put("thirdUserId", thirdInfo.userId);
			holder.put("userId", UserNow.current().userID);
			if (!TextUtils.isEmpty(thirdInfo.expiresIn)) {
				holder.put("validTime", thirdInfo.expiresIn);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
