package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.CommentDetailParser;
import com.sumavision.talktv2.http.json.CommentDetailRequest;
import com.sumavision.talktv2.http.listener.OnCommentDetailListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 评论详情回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentDetailCallback extends BaseCallback {
	private OnCommentDetailListener listener;

	public CommentDetailCallback(OnHttpErrorListener errorListener,
			OnCommentDetailListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	CommentDetailParser parser = new CommentDetailParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.commenetDetailResult(parser.errCode, parser.replyCount,
					parser.replyList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new CommentDetailRequest().make();
	}

}
