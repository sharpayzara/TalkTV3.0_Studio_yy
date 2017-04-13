package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.InteractiveUserGuessListParser;
import com.sumavision.talktv2.http.json.interactive.InteractiveUserGuessListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnUserGuessListListener;

/**
 * 用户竞猜列表
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveUserGuessListCallback extends BaseCallback {

	private int first;
	private int count;
	private OnUserGuessListListener mListener;

	public InteractiveUserGuessListCallback(OnHttpErrorListener errorListener,
			int first, int count, OnUserGuessListListener mListener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.mListener = mListener;
	}

	InteractiveUserGuessListParser mParser = new InteractiveUserGuessListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.userGuessList(mParser.errCode, mParser.userGuessList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new InteractiveUserGuessListRequest(first, count).make();
	}

}
