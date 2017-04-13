package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveActivityDetailParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveActivityDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveActivityDetailListener;

/**
 * 互动活动详情回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityDetailCallback extends BaseCallback {

	private OnInteractiveActivityDetailListener mListener;

	private InteractiveActivity mInteractiveActivity;

	/**
	 * @param errorListener
	 * @param interactiveActivityId
	 * @param mListener
	 */
	public InteractiveActivityDetailCallback(OnHttpErrorListener errorListener,
			InteractiveActivity mInteractiveActivity,
			OnInteractiveActivityDetailListener mListener) {
		super(errorListener);
		this.mInteractiveActivity = mInteractiveActivity;
		this.mListener = mListener;
		mParser = new InteractiveActivityDetailParser(mInteractiveActivity);
	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveActivityDetailRequest(mInteractiveActivity.id)
				.make();
	}

	InteractiveActivityDetailParser mParser;

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onInteractiveActivityDetail(mParser.errCode);
		}
	}
}
