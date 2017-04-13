package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ReplyByListParser;
import com.sumavision.talktv2.http.json.ReplyByListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnReplyByListListener;

/**
 * 被回复列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ReplyByListCallback extends BaseCallback {

	private int userId;
	private int first;
	private int count;
	private OnReplyByListListener listener;

	public ReplyByListCallback(OnHttpErrorListener errorListener, int userId,
			int first, int count, OnReplyByListListener listener) {
		super(errorListener);
		this.userId = userId;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	ReplyByListParser parser = new ReplyByListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getReplyByList(parser.errCode, parser.replyCount,
					parser.replyList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ReplyByListRequest(userId, first, count).make();
	}

}
