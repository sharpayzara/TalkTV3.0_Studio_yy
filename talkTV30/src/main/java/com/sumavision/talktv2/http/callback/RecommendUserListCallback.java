package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.RecommendUserListParser;
import com.sumavision.talktv2.http.json.RecommendUserListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRecommendUserListListener;

/**
 * 推荐好友列表回调
 * 
 * @author suma-hpb
 * @version
 * @description 推荐好友列表 好友列表第二页
 */
public class RecommendUserListCallback extends BaseCallback {

	private OnRecommendUserListListener listener;

	public RecommendUserListCallback(OnHttpErrorListener errorListener,
			OnRecommendUserListListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	RecommendUserListParser parser = new RecommendUserListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getRecommendUserList(parser.errCode, parser.userList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new RecommendUserListRequest().make();
	}

}
