package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveCyclopediaParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveCyclopediaRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveCyclopediaListener;

/**
 * 百科回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveCyclopediaCallback extends BaseCallback {

	private int first;
	private int count;

	private OnInteractiveCyclopediaListener mListener;

	/**
	 * @param errorListener
	 * @param first
	 * @param count
	 * @param mListener
	 */
	public InteractiveCyclopediaCallback(OnHttpErrorListener errorListener,
			int first, int count, OnInteractiveCyclopediaListener mListener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.mListener = mListener;
	}

	InteractiveCyclopediaParser mParser = new InteractiveCyclopediaParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.cyclopediaKeywords(mParser.errCode, mParser.keyWordsList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveCyclopediaRequest(first, count).make();
	}

}
