package com.sumavision.talktv2.http.callback.activities;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.activities.ActivityListParser;
import com.sumavision.talktv2.http.json.activities.ActivityListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.activities.OnActivityListListener;

/**
 * 活动列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityListcallback extends BaseCallback {

	private OnActivityListListener listener;
	private Context context;
	private int first;
	private int count;

	public ActivityListcallback(OnHttpErrorListener errorListener,
			OnActivityListListener listener, Context context, int first,
			int count) {
		super(errorListener);
		this.listener = listener;
		this.context = context;
		this.first = first;
		this.count = count;
	}

	ActivityListParser parser = new ActivityListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);

	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getActivityList(parser.errCode, parser.activityList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ActivityListRequest(context, first, count).make();
	}

}
