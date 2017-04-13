package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.DeleteRemindRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 取消预约回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class DeleteRemindCallback extends BaseCallback {

	private int userId;
	private String cpIds;
	private OnDeleteRemindListener listener;

	public DeleteRemindCallback(OnHttpErrorListener errorListener, int userId,
			String cpIds, OnDeleteRemindListener listener) {
		super(errorListener);
		this.userId = userId;
		this.cpIds = cpIds;
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
			listener.deleteRemindResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new DeleteRemindRequest(userId, cpIds).make();
	}

}
