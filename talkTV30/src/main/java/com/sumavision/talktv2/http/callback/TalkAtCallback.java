package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.TalkAtListParser;
import com.sumavision.talktv2.http.json.TalkAtListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnTalkAtListener;

/**
 * 用户被@列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class TalkAtCallback extends BaseCallback {

	private int first;
	private int count;
	private OnTalkAtListener listener;

	public TalkAtCallback(OnHttpErrorListener errorListener, int first,
			int count, OnTalkAtListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	TalkAtListParser parser = new TalkAtListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getTalkAtList(parser.errCode, parser.talkCount,
					parser.userTalkList);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new TalkAtListRequest(first, count).make();
	}

}
