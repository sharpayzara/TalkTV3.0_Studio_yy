package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.MyBadgeParser;
import com.sumavision.talktv2.http.json.MyBadgeRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnMyBadgeListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 我的徽章回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyBadgeCallback extends BaseCallback {
	private Context context;
	private OnMyBadgeListener listener;
	private int first;
	private int count;

	public MyBadgeCallback(OnHttpErrorListener errorListener, Context context,
			OnMyBadgeListener listener, int first, int count) {
		super(errorListener);
		this.context = context;
		this.listener = listener;
		this.first = first;
		this.count = count;
	}

	MyBadgeParser parser = new MyBadgeParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}
	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == 0) {
			PreferencesUtils.putInt(context, "userInfo", "badgeCount",
					UserNow.current().badgeCount);
		}
		if (listener != null) {
			listener.myBadgeResult(parser.errCode,parser.badgeList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new MyBadgeRequest(first, count).make();
	}

}
