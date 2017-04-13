package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.MyFansParser;
import com.sumavision.talktv2.http.json.MyFansRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnMyFansListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 我的粉丝回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyFansCallback extends BaseCallback {

	private Context context;
	private OnMyFansListener listener;
	private int first;
	private int count;

	public MyFansCallback(OnHttpErrorListener errorListener, Context context,
			int first, int count, OnMyFansListener listener) {
		super(errorListener);
		this.context = context;
		this.first = first;
		this.count = count;
		this.listener = listener;
	}

	MyFansParser parser = new MyFansParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
			PreferencesUtils.putInt(context, "userInfo", "fansCount",
					UserNow.current().fansCount);
		}
		if (listener != null) {
			listener.getMyFans(parser.errCode, parser.userList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new MyFansRequest(first, count).make();
	}

}
