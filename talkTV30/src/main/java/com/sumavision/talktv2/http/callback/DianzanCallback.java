package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.http.json.DianzanParser;
import com.sumavision.talktv2.http.json.DianzanRequest;
import com.sumavision.talktv2.http.listener.OnDianzanListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 点赞回调
 * 
 * @author suma-hpb
 * 
 */
public class DianzanCallback extends BaseCallback {

	private int type;
	private int programId;
	private Context context;
	private OnDianzanListener mOnDianzanListener;

	/**
	 * @param errorListener
	 * @param type
	 * @param programSubId
	 * @param context
	 * @param mOnDianzanListener
	 */
	public DianzanCallback(OnHttpErrorListener errorListener, int type,
			int programId, Context context, OnDianzanListener mOnDianzanListener) {
		super(errorListener);
		this.type = type;
		this.programId = programId;
		this.context = context;
		this.mOnDianzanListener = mOnDianzanListener;
	}

	protected void onResponseDelegate() {
		if (mOnDianzanListener != null) {
			mOnDianzanListener.onDianzan(mParser.errCode);
		}

	};

	DianzanParser mParser = new DianzanParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);

	}

	@Override
	public JSONObject makeRequest() {
		return new DianzanRequest(context, programId, type).make();
	}
}
