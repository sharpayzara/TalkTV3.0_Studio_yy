package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 分享后请求
 * 
 * @author suma-hpb
 * 
 */
public class ShareRequest extends BaseJsonRequest {
	public static final int TYPE_PROGRAM = 1;
	public static final int TYPE_ACTIVITY = 13;
	public static final int TYPE_LIVE = 15;

	int objectType;
	int objectId;

	/**
	 * @param objectType
	 * @param objectId
	 */
	public ShareRequest(int objectType, int objectId) {
		this.objectType = objectType;
		this.objectId = objectId;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.shareAnything);
			requestObj.put("version", "2.6.0");
			requestObj.put("objectType", objectType);
			requestObj.put("objectId", objectId);
			requestObj.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
