package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.http.json.BindAccountParser;
import com.sumavision.talktv2.http.json.BindAccountRequest;
import com.sumavision.talktv2.http.listener.OnBindAccountListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 账号绑定回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindAccountCallback extends BaseCallback {

	private OnBindAccountListener listener;
	ThirdPlatInfo thirdInfo;

	public BindAccountCallback(OnHttpErrorListener errorListener,
			ThirdPlatInfo thirdInfo, OnBindAccountListener listener) {
		super(errorListener);
		this.listener = listener;
		this.thirdInfo = thirdInfo;
	}

	BindAccountParser parser = new BindAccountParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.bindAccountResult(parser.errCode, parser.userInfo.point,
					parser.errMsg);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new BindAccountRequest(thirdInfo).make();
	}

}
