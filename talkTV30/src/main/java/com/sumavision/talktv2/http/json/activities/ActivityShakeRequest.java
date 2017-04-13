package com.sumavision.talktv2.http.json.activities;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 刮奖请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityShakeRequest extends BaseJsonRequest {

	private Context context;
	private int activityId;

	public ActivityShakeRequest(Context context, int activityId) {
		super();
		this.context = context;
		this.activityId = activityId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.newActivityShake);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", JSONMessageType.VERSION_231);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("activityId", activityId);
			if (UserNow.current().userID != 0)
				jsonObject.put("userId", UserNow.current().userID);
//			String imei = PreferencesUtils.getString(context, null,
//					"imei");
//			if (StringUtils.isNotEmpty(imei))
//				UserNow.current().imei = imei;
			jsonObject.put("imei", AppUtil.getImei(context));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
