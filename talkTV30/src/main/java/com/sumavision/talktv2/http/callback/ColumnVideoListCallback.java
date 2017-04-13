package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.http.json.ColumnVideoListParser;
import com.sumavision.talktv2.http.json.ColumnVideoListRequest;
import com.sumavision.talktv2.http.listener.OnColumnVideoListLitener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 回调:推荐页的2 3 4 标签
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ColumnVideoListCallback extends BaseCallback {

	private int columnId;
	private int first;
	private int count;
	private OnColumnVideoListLitener listLitener;

	public ColumnVideoListCallback(OnHttpErrorListener errorListener, int id,
			int first, int count, OnColumnVideoListLitener listLitener) {
		super(errorListener);
		this.columnId = id;
		this.first = first;
		this.count = count;
		this.listLitener = listLitener;
	}

	ColumnVideoListParser parser = new ColumnVideoListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listLitener != null) {
			listLitener.columnVideoList(parser.errCode, parser.totalCount,parser.programList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new ColumnVideoListRequest(columnId, first, count).make();
	}

}
