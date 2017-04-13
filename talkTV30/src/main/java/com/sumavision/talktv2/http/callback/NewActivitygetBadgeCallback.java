package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.json.NewActivityGetBadgeRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnNewActivitygetBadgeListener;

public class NewActivitygetBadgeCallback extends BaseCallback {

	private long activityId;
	private Context context;
	private OnNewActivitygetBadgeListener listener;

	public NewActivitygetBadgeCallback(OnHttpErrorListener errorListener,
			long activityId, Context context,
			OnNewActivitygetBadgeListener listener) {
		super(errorListener);
		this.activityId = activityId;
		this.context = context;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.activityGetBadgeResult(parser.errCode, parser.badgeList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new NewActivityGetBadgeRequest(activityId, context).make();
	}

}
