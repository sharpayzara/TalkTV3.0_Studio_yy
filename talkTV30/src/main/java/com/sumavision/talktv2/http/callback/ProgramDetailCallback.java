package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ProgramDetailParser;
import com.sumavision.talktv2.http.json.ProgramDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnProgramDetailListener;

/**
 * 节目详情回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ProgramDetailCallback extends BaseCallback {

	private long programId;
	private OnProgramDetailListener listener;

	public ProgramDetailCallback(OnHttpErrorListener errorListener,
			long programId, OnProgramDetailListener listener) {
		super(errorListener);
		this.programId = programId;
		this.listener = listener;
	}

	ProgramDetailParser parser = new ProgramDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getProgramDetail(parser.errCode, parser.programDetailInfo);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ProgramDetailRequest(programId).make();
	}

}
