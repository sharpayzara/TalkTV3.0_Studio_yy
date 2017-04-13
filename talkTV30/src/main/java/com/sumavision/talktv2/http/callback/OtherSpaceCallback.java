package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.OtherSpaceParser;
import com.sumavision.talktv2.http.json.OtherSpaceRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnOtherSpaceListener;

/**
 * 其他用户信息获取
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class OtherSpaceCallback extends BaseCallback {

	private int userId;
	private OnOtherSpaceListener listener;

	public OtherSpaceCallback(OnHttpErrorListener errorListener, int userId,
			OnOtherSpaceListener listener) {
		super(errorListener);
		this.userId = userId;
		this.listener = listener;
	}

	OtherSpaceParser parser = new OtherSpaceParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getUserInfo(parser.errCode, parser.uo);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new OtherSpaceRequest(userId).make();
	}

}
