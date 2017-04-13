package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.CommentListParser;
import com.sumavision.talktv2.http.json.CommentListRequest;
import com.sumavision.talktv2.http.json.TalkListParser;
import com.sumavision.talktv2.http.json.TalkListRequest;
import com.sumavision.talktv2.http.listener.OnCommentListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 评论列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentListCallback extends BaseCallback {

	private int topicId;
	private int cpId;
	private int first;
	private int count;
	private OnCommentListListener listener;

	public CommentListCallback(OnHttpErrorListener errorListener, int topicId,
			int cpId, int first, int count, OnCommentListListener listener) {
		super(errorListener);
		this.topicId = topicId;
		this.cpId = cpId;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	TalkListParser parser = new TalkListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.commentList(parser.errCode, parser.talkCount,
					parser.commentData);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new TalkListRequest(topicId, first, count).make();
	}

}
