package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ReplyListRequest;
import com.sumavision.talktv2.http.json.ReplyParser;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnReplyListListener;

/**
 * 回复列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ReplyListCallback extends BaseCallback {

	private int first;
	private int count;
	private OnReplyListListener listener;

	public ReplyListCallback(OnHttpErrorListener errorListener, int first,
			int count, OnReplyListListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	ReplyParser parser = new ReplyParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getReplyList(parser.errCode, parser.replyCount,
					parser.commentList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ReplyListRequest(first, count).make();
	}

}
