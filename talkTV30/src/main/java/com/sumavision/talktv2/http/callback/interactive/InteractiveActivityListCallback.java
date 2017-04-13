package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveActivityListParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveActivityListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityListListener;

/**
 * 互动列表回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityListCallback extends BaseCallback {

	private int first;
	private int count;
	private OnInteractiveActivityListListener mListener;

	/**
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public InteractiveActivityListCallback(OnHttpErrorListener errorListener,
			int first, int count, OnInteractiveActivityListListener mListener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.mListener = mListener;
	}

	InteractiveActivityListParser mParser = new InteractiveActivityListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (mListener != null) {
			mListener.OnInteractiveActivityListResult(mParser.errCode,
					mParser.activityList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveActivityListRequest(first, count).make();
	}

}
