package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessListParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessListListener;

public class InteractiveGuessListCallback extends BaseCallback {

	private int activityId;
	private int first;
	private int count;
	private OnInteractiveGuessListListener mListener;

	/**
	 * @param errorListener
	 * @param activityId
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public InteractiveGuessListCallback(OnHttpErrorListener errorListener,
			int activityId, int first, int count,
			OnInteractiveGuessListListener mListener) {
		super(errorListener);
		this.activityId = activityId;
		this.first = first;
		this.count = count;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveGuessListRequest(activityId, first, count).make();
	}InteractiveGuessListParser mParser = new InteractiveGuessListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
		
	}
	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetGuessingList(mParser.errCode, mParser.guessingList);
		}

	}
}
