package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.DeleteGuanzhuRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnDeleteGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 取消关注回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DeleteGuanzhuCallback extends BaseCallback {

	private int userId;
	private OnDeleteGuanzhuListener listener;

	public DeleteGuanzhuCallback(OnHttpErrorListener errorListener, int userId,
			OnDeleteGuanzhuListener listener) {
		super(errorListener);
		this.userId = userId;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.deleteGuanzhuResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new DeleteGuanzhuRequest(userId).make();
	}

}
