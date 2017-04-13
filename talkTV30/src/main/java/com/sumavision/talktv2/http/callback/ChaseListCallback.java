package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.ChaseListParser;
import com.sumavision.talktv2.http.json.ChaseListRequest;
import com.sumavision.talktv2.http.listener.OnChaseListListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 追剧列表
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChaseListCallback extends BaseCallback {

	private Context context;
	private OnChaseListListener listener;
	private int userId;
	private int first;
	private int count;

	public ChaseListCallback(OnHttpErrorListener errorListener,
			Context context, OnChaseListListener listener, int userId,
			int first, int count) {
		super(errorListener);
		this.context = context;
		this.listener = listener;
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	ChaseListParser parser = new ChaseListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == 0 && UserNow.current().userID == userId) {
			UserNow.current().chaseCount = parser.chaseCount;
			PreferencesUtils.putInt(context, "userInfo", "chaseCount",
					UserNow.current().chaseCount);
		}
		if (listener != null) {
			listener.getChaseList(parser.errCode, parser.chaseCount,
					parser.chaseList);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new ChaseListRequest(userId, first, count).make();
	}

}
