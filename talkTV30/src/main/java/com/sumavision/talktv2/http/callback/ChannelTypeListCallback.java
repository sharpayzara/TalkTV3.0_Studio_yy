package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChannelTypeListParser;
import com.sumavision.talktv2.http.json.ChannelTypeListRequest;
import com.sumavision.talktv2.http.listener.OnChannelTypeListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class ChannelTypeListCallback extends BaseCallback {

	private OnChannelTypeListListener listener;
	String umengValue;

	public ChannelTypeListCallback(OnHttpErrorListener errorListener,
			OnChannelTypeListListener listener, String umengValue) {
		super(errorListener);
		this.listener = listener;
		this.umengValue = umengValue;
		parser = new ChannelTypeListParser(umengValue);
	}

	ChannelTypeListParser parser = null;

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null && parser != null) {
			listener.channelTypeList(parser.errCode, parser.channelTypeList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ChannelTypeListRequest().make();
	}

}
