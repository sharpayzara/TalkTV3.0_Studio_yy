package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessJoinParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveGuessJoinRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessJoinListener;

/**
 * 参与竞猜回调
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessJoinCallback extends BaseCallback {

	private OnInteractiveGuessJoinListener mListener;
	private int guessId;
	private int optionId;
	private long bet;

	/**
	 * @param errorListener
	 * @param mListener
	 * @param guessId
	 * @param optionId
	 * @param bet
	 */
	public InteractiveGuessJoinCallback(OnHttpErrorListener errorListener,
			OnInteractiveGuessJoinListener mListener, int guessId,
			int optionId, long bet) {
		super(errorListener);
		this.mListener = mListener;
		this.guessId = guessId;
		this.optionId = optionId;
		this.bet = bet;
	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveGuessJoinRequest(guessId, optionId, bet).make();
	}

	InteractiveGuessJoinParser mParser = new InteractiveGuessJoinParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.JoinGuessResult(mParser.errCode, mParser.guessResult);
		}

	}
}
