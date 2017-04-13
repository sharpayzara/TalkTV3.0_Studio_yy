package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChaseProgramRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnChaseProgramListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 追剧回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChaseProgramCallback extends BaseCallback {

	private int programId;
	private OnChaseProgramListener listener;

	public ChaseProgramCallback(OnHttpErrorListener errorListener, int programId,
			OnChaseProgramListener listener) {
		super(errorListener);
		this.programId = programId;
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
			listener.chaseResult(parser.errCode);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new ChaseProgramRequest(programId).make();
	}

}
