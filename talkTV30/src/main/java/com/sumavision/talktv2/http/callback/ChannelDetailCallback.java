package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ChannelDetailParser;
import com.sumavision.talktv2.http.json.ChannelDetailRequest;
import com.sumavision.talktv2.http.listener.OnChannelDetailListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 频道详情回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelDetailCallback extends BaseCallback {

	private int userId;
	private String date;
	private int channelId;
	private OnChannelDetailListener listener;

	public ChannelDetailCallback(OnHttpErrorListener errorListener, int userId,
			String date, int channelId, int whichDayType,
			OnChannelDetailListener listener) {
		super(errorListener);
		this.userId = userId;
		this.date = date;
		this.channelId = channelId;
		this.listener = listener;
		parser = new ChannelDetailParser(whichDayType);
	}

	ChannelDetailParser parser = null;

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {

		if (listener != null) {
			listener.channelDetailResult(parser.errCode, parser.errMsg,
					parser.channelData, parser.playItemPos);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ChannelDetailRequest(userId, date, channelId).make();
	}

}
