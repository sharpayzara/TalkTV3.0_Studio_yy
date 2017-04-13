package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.EventRoomParser;
import com.sumavision.talktv2.http.json.EventRoomRequest;
import com.sumavision.talktv2.http.listener.OnEventRoomListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

public class EventRoomCallback extends BaseCallback {

	private int first;
	private int count;
	private OnEventRoomListener listener;

	public EventRoomCallback(OnHttpErrorListener errorListener, int first,
			int count, OnEventRoomListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	EventRoomParser parser = new EventRoomParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.eventRoomResult(parser.errCode, parser.eventCount,
					parser.evenList);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new EventRoomRequest(first, count).make();
	}

}
