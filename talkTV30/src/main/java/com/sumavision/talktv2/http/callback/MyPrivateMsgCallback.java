package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.MyPrivateMsgParser;
import com.sumavision.talktv2.http.json.MyPrivateMsgRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnMyPrivateMsgListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 我的私信回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyPrivateMsgCallback extends BaseCallback {

	private Context context;
	private OnMyPrivateMsgListener listener;
	private int first;
	private int count;

	public MyPrivateMsgCallback(OnHttpErrorListener errorListener,
			Context context, int first, int count,
			OnMyPrivateMsgListener listener) {
		super(errorListener);
		this.context = context;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	MyPrivateMsgParser parser = new MyPrivateMsgParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == 0) {
			PreferencesUtils.putInt(context, "userInfo", "messageCount",
					UserNow.current().mailCount);
		}
		if (listener != null) {
			listener.getMyPrivateMessage(parser.errCode, parser.mailCount,
					parser.mailList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new MyPrivateMsgRequest(first, count).make();
	}

}
