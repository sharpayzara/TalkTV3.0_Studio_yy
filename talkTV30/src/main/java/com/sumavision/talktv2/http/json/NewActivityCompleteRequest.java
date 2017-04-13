package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;

public class NewActivityCompleteRequest extends BaseJsonRequest {
	private long activityId;
	private Context context;

	public NewActivityCompleteRequest(Context context, long activityId) {
		super();
		this.activityId = activityId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.newActivityComplete);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", JSONMessageType.VERSION_231);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("activityId", activityId);
			if (UserNow.current().userID != 0)
				jsonObject.put("userId", UserNow.current().userID);
			String imei = PreferencesUtils.getString(context,null,
					"imei");
			if (StringUtils.isNotEmpty(imei))
				UserNow.current().imei = imei;
			jsonObject.put("imei", UserNow.current().imei);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
