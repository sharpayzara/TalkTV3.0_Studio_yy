package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.json.DianzanGetParser;
import com.sumavision.talktv2.http.json.DianzanGetRequest;
import com.sumavision.talktv2.http.listener.OnDianzanGetListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 点赞信息获取回调
 * 
 * @author suma-hpb
 * 
 */
public class DianzanGetCallback extends BaseCallback {
	private Context context;
	private int programSubId;
	private OnDianzanGetListener mListener;

	/**
	 * @param errorListener
	 * @param programSubId
	 * @param mListener
	 */
	public DianzanGetCallback(Context context,
			OnHttpErrorListener errorListener, int programSubId,
			OnDianzanGetListener mListener) {
		super(errorListener);
		this.context = context;
		this.programSubId = programSubId;
		this.mListener = mListener;
	}

	@Override
	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetDianzan(mParser.errCode, mParser.zanType);
		}

	}

	DianzanGetParser mParser = new DianzanGetParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new DianzanGetRequest(context, programSubId).make();
	}

}
