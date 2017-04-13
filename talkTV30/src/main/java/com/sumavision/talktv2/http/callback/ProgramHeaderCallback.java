package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ProgramHeaderParser;
import com.sumavision.talktv2.http.json.ProgramHeaderRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnProgramHeaderListener;

public class ProgramHeaderCallback extends BaseCallback {

	private long programId;
	private long cpId;
	private OnProgramHeaderListener listener;

	public ProgramHeaderCallback(OnHttpErrorListener errorListener,
			long programId, long cpId, OnProgramHeaderListener listener) {
		super(errorListener);
		this.programId = programId;
		this.cpId = cpId;
		this.listener = listener;
	}

	ProgramHeaderParser parser = new ProgramHeaderParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getProgramHeader(parser.errCode, parser.programData);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ProgramHeaderRequest(programId, cpId).make();
	}

}
