package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.SearchParser;
import com.sumavision.talktv2.http.json.SearchRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnSearchListener;

/**
 * 搜索节目回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchCallback extends BaseCallback {

	private String searchKeyWords;
	private int first;
	private int count;
	private OnSearchListener listener;

	public SearchCallback(OnHttpErrorListener errorListener,
			String searchKeyWords, int first, int count,
			OnSearchListener listener) {
		super(errorListener);
		this.searchKeyWords = searchKeyWords;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	SearchParser parser = new SearchParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			listener.getSearchProgramList(parser.errCode, parser.totalCount,
					parser.programList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new SearchRequest(searchKeyWords, first, count).make();
	}

}
