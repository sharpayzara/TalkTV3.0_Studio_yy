package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 徽章抽奖(3.0)详情请求
 * @author suma-hpb
 *
 */
public class BadgeLotteryDetailRequest extends BaseJsonRequest {

	private long activityId;
	private Context context;

	public BadgeLotteryDetailRequest(long activityId, Context context) {
		this.activityId = activityId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod("badgeLotteryActivityDetail");
			requestObj.put("version", "3.0.0");
			requestObj.put("activityId", activityId);
			if (UserNow.current().userID != 0)
				requestObj.put("userId", UserNow.current().userID);
			String imei = AppUtil.getImei(context);
			requestObj.put("imei", imei);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
