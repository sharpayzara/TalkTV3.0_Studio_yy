package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 
 * @author suma-hpb
 * @version 
 * @description
 */
public class NewActivityGetBadgeRequest extends BaseJsonRequest {
	private long activityId;
	private Context context;

	public NewActivityGetBadgeRequest(long activityId, Context context) {
		super();
		this.activityId = activityId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.newActivityGetBadge);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", JSONMessageType.VERSION_231);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("activityId", activityId);
			if (UserNow.current().userID != 0)
				jsonObject.put("userId", UserNow.current().userID);
//			String imei = PreferencesUtils.getString(context,null,
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
