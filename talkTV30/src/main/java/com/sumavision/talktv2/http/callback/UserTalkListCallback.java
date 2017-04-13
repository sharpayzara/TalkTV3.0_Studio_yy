package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.UserTalkListParser;
import com.sumavision.talktv2.http.json.UserTalkListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnUserTalkListListener;

/**
 * 用户评论列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class UserTalkListCallback extends BaseCallback {

	private int userId;
	private OnUserTalkListListener listener;
	private int first;
	private int count;

	public UserTalkListCallback(OnHttpErrorListener errorListener, int userId,
			OnUserTalkListListener listener, int first, int count) {
		super(errorListener);
		this.userId = userId;
		this.listener = listener;
		this.first = first;
		this.count = count;
	}

	UserTalkListParser parser = new UserTalkListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getUserTalkList(parser.errCode, parser.talkCount,
					parser.talkList,parser.isVip);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new UserTalkListRequest(userId, first, count).make();
	}

}
