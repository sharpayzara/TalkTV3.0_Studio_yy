package com.sumavision.talktv2.http.callback.interactive;

import org.json.JSONObject;

import com.sumavision.talktv2.http.callback.BaseCallback;
import com.sumavision.talktv2.http.json.interactive.GuessProgramListParser;
import com.sumavision.talktv2.http.json.interactive.GuessProgramListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.interactive.OnGuessProgramListListener;

/**
 * 竞猜节目列表回调
 * 
 * @author suma-hpb
 * 
 */
public class GuessProgramListCallback extends BaseCallback {

	private int count;
	private int first;
	private int activityId;
	private OnGuessProgramListListener mListener;

	/**
	 * @param errorListener
	 * @param count
	 * @param first
	 * @param activityId
	 * @param mListener
	 */
	public GuessProgramListCallback(OnHttpErrorListener errorListener,
			int count, int first, int activityId,
			OnGuessProgramListListener mListener) {
		super(errorListener);
		this.count = count;
		this.first = first;
		this.activityId = activityId;
		this.mListener = mListener;
	}

	@Override
	public JSONObject makeRequest() {
		return new GuessProgramListRequest(activityId, first, count).make();
	}

	GuessProgramListParser mParser = new GuessProgramListParser();

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetGuessProgramlist(mParser.errCode,
					mParser.programList);
		}

	}

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}
}
