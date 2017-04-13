package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.MyFollowParser;
import com.sumavision.talktv2.http.json.MyFollowRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnMyFollowListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 我的关注回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyFollowCallback extends BaseCallback {

	private Context context;
	private OnMyFollowListener listener;
	private int id;
	int first;
	int count;

	public MyFollowCallback(OnHttpErrorListener errorListener, Context context,
			int otherid, int first, int count, OnMyFollowListener listener) {
		super(errorListener);
		this.context = context;
		this.listener = listener;
		this.id = otherid;
		this.first = first;
		this.count = count;
	}

	MyFollowParser parser = new MyFollowParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
			PreferencesUtils.putInt(context, "userInfo", "friendCount",
					UserNow.current().friendCount);
		}
		if (listener != null) {
			listener.getMyFollow(parser.errCode, parser.guanzhuCont,
					parser.guanzhuList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new MyFollowRequest(id, first, count).make();
	}

}
