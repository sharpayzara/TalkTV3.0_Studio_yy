package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveSupportParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveSupportRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveSupportListener;

/**
 * 互动支持参与回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveSupportCallback extends BaseCallback {

	private int activityId;
	private int support;
	private OnInteractiveSupportListener mListener;

	/**
	 * @param errorListener
	 * @param activityId
	 * @param support
	 * @param mListener
	 */
	public InteractiveSupportCallback(OnHttpErrorListener errorListener,
			int activityId, int support, OnInteractiveSupportListener mListener) {
		super(errorListener);
		this.activityId = activityId;
		this.support = support;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveSupportRequest(activityId, support).make();
	}

	InteractiveSupportParser mParser = new InteractiveSupportParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (mListener != null) {
			mListener.onInteractiveSupport(mParser.errCode);
		}

	}
}
