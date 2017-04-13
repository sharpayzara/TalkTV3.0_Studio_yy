package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessDetailParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessDetailListener;

/**
 * 竞猜详情回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessDetailCallback extends BaseCallback {

	private OnInteractiveGuessDetailListener mListener;
	private int guessId;

	/**
	 * @param errorListener
	 * @param mListener
	 * @param guessId
	 */
	public InteractiveGuessDetailCallback(OnHttpErrorListener errorListener,
			OnInteractiveGuessDetailListener mListener, int guessId) {
		super(errorListener);
		this.mListener = mListener;
		this.guessId = guessId;
	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveGuessDetailRequest(guessId).make();
	}

	InteractiveGuessDetailParser mParser = new InteractiveGuessDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.guessDetailResult(mParser.errCode, mParser.guess);
		}

	}
}
