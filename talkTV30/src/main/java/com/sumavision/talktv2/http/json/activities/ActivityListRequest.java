package com.sumavision.talktv2.http.json.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 
 * @author suma-hpb
 * @version 3.0
 * @description 活动请求组装类
 * @changLog
 */
public class ActivityListRequest extends BaseJsonRequest {

	private Context context;
	private int first;
	private int count;

	public ActivityListRequest(Context context, int first, int count) {
		super();
		this.context = context;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.activityList);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.VERSION_231);
			holder.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0)
				holder.put("userId", UserNow.current().userID);
			holder.put("first", first);
			holder.put("count", count);
			String imei = PreferencesUtils.getString(context, null, "imei");
			if (StringUtils.isNotEmpty(imei))
				UserNow.current().imei = imei;
			holder.put("imei", UserNow.current().imei);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
