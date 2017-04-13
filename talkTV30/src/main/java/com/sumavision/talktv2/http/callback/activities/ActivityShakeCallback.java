package com.sumavision.talktv2.http.callback.activities;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.activities.ActivityShakeParser;
import com.sumavision.talktv2.http.json.activities.ActivityShakeRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.activities.OnActivityShakeListener;

/**
 * 刮奖回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityShakeCallback extends BaseCallback {

	private Context context;
	private int activityId;
	private OnActivityShakeListener listener;

	public ActivityShakeCallback(OnHttpErrorListener errorListener,
			Context context, int activityId, OnActivityShakeListener listener) {
		super(errorListener);
		this.context = context;
		this.activityId = activityId;
		this.listener = listener;
	}

	ActivityShakeParser parser = new ActivityShakeParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (listener != null) {
			listener.getActivityGood(parser.errCode, parser.errMsg, parser.good);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ActivityShakeRequest(context, activityId).make();
	}

}
