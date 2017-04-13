package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ForgetInitParser;
import com.sumavision.talktv2.http.json.ForgetInitRequest;
import com.sumavision.talktv2.http.listener.OnForgetInitListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 密码找回-邮箱验证
 * 
 * @author suma-hpb
 * 
 */
public class ForgetInitCallback extends BaseCallback {

	private String input;
	private OnForgetInitListener mListener;

	public ForgetInitCallback(OnHttpErrorListener errorListener, String input,
			OnForgetInitListener mListener) {
		super(errorListener);
		this.input = input;
		this.mListener = mListener;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onGetEmailInFindPasswd(mParser.errCode, mParser.errMsg,
					mParser.email);
		}
	};

	ForgetInitParser mParser = new ForgetInitParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new ForgetInitRequest(input).make();
	}

}
