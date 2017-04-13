package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChannelListParser;
import com.sumavision.talktv2.http.json.ChannelListRequest;
import com.sumavision.talktv2.http.listener.OnChannelListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class ChannelListCallback extends BaseCallback {

	private int userId;
	private int typeId;
	private int first;
	private int count;
	private OnChannelListListener listener;

	public ChannelListCallback(OnHttpErrorListener errorListener, int userId,
			int typeId, int first, int count, OnChannelListListener listener) {
		super(errorListener);
		this.userId = userId;
		this.typeId = typeId;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	ChannelListParser parser = new ChannelListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (listener != null) {
			listener.channelList(parser.errCode, parser.channelCount,
					parser.list);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ChannelListRequest(userId, typeId, first, count).make();
	}

}
