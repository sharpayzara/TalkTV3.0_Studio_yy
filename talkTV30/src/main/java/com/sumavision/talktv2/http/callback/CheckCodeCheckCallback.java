package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.CheckCodeCheckParser;
import com.sumavision.talktv2.http.json.CheckCodeCheckRequest;
import com.sumavision.talktv2.http.listener.OnCheckCodeCheckListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 密码找回-校验验证码 回调
 * 
 * @author suma-hpb
 * 
 */
public class CheckCodeCheckCallback extends BaseCallback {

	private String checkCode;
	private OnCheckCodeCheckListener mListener;

	/**
	 * @param errorListener
	 * @param checkCode
	 * @param mListener
	 */
	public CheckCodeCheckCallback(OnHttpErrorListener errorListener,
			String checkCode, OnCheckCodeCheckListener mListener) {
		super(errorListener);
		this.checkCode = checkCode;
		this.mListener = mListener;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onCheckCode(mParser.errCode, mParser.errMsg);
		}
	}

	CheckCodeCheckParser mParser = new CheckCodeCheckParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);

	}

	@Override
	public JSONObject makeRequest() {
		return new CheckCodeCheckRequest(checkCode).make();
	}

}
