package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChangePasswdParser;
import com.sumavision.talktv2.http.json.ChangePasswdRequest;
import com.sumavision.talktv2.http.listener.OnChangePasswdListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 修改密码回调
 * 
 * @author suma-hpb
 * 
 */
public class ChangePasswdCallback extends BaseCallback {

	private String checkCode;
	private String password;
	private OnChangePasswdListener mListener;

	/**
	 * @param errorListener
	 * @param checkCode
	 * @param password
	 * @param mListener
	 */
	public ChangePasswdCallback(OnHttpErrorListener errorListener,
			String checkCode, String password, OnChangePasswdListener mListener) {
		super(errorListener);
		this.checkCode = checkCode;
		this.password = password;
		this.mListener = mListener;
	}

	protected void onResponseDelegate() {
		if (mListener != null) {
			mListener.onChangePasswd(mParser.errCode, mParser.errMsg);
		}
	};

	ChangePasswdParser mParser = new ChangePasswdParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		mParser.parse(jsonObject);
	}

	@Override
	public JSONObject makeRequest() {
		return new ChangePasswdRequest(checkCode, password).make();
	}
}
