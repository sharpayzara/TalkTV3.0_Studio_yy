package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.AddRemindParser;
import com.sumavision.talktv2.http.json.AddRemindRequest;
import com.sumavision.talktv2.http.listener.OnAddRemindListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 添加预约回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class AddRemindCallback extends BaseCallback {

	private int userId;
	private int programId;
	private OnAddRemindListener listener;

	public AddRemindCallback(OnHttpErrorListener errorListener, int userId,
			int programId, OnAddRemindListener listener) {
		super(errorListener);
		this.userId = userId;
		this.programId = programId;
		this.listener = listener;
	}

	AddRemindParser parser = new AddRemindParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (listener != null) {
			listener.addRemindResult(parser.errCode, parser.errMsg, parser.remindId);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new AddRemindRequest(userId, programId).make();
	}

}
