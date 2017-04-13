package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ForgetInitParser;
import com.sumavision.talktv2.http.json.ReSendEmailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnReSendEmailListener;

/**
 * 密码找回-重新发送验证码邮件 回调
 * 
 * @author suma-hpb
 * 
 */
public class ReSendEmailCallback extends BaseCallback {

	private OnReSendEmailListener mListener;

	/**
	 * @param errorListener
	 * @param mListener
	 */
	public ReSendEmailCallback(OnHttpErrorListener errorListener,
			OnReSendEmailListener mListener) {
		super(errorListener);
		this.mListener = mListener;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onSendEmail(mParser.errCode, mParser.errMsg);
		}
	};

	ForgetInitParser mParser = new ForgetInitParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);

	}

	@Override
	public JSONObject makeRequest() {
		return new ReSendEmailRequest().make();
	}

}
