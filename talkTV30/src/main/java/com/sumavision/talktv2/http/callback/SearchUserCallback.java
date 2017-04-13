package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.SearchUserParser;
import com.sumavision.talktv2.http.json.SearchUserRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSearchUserListener;

/**
 * 搜索用户回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchUserCallback extends BaseCallback {

	private int first;
	private int count;
	private String name;
	private OnSearchUserListener listener;

	public SearchUserCallback(OnHttpErrorListener errorListener, int first,
			int count, String name, OnSearchUserListener listener) {
		super(errorListener);
		this.first = first;
		this.count = count;
		this.name = name;
		this.listener = listener;
	}

	SearchUserParser parser = new SearchUserParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getSearchUserList(parser.errCode, parser.count,
					parser.userList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new SearchUserRequest(first, count, name).make();
	}

}
