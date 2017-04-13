package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.RemindListParser;
import com.sumavision.talktv2.http.json.RemindListRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRemindListListener;
import com.sumavision.talktv2.utils.PreferencesUtils;

/**
 * 预约列表回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RemindListCallback extends BaseCallback {

	private Context context;
	private OnRemindListListener listener;
	private int userId;
	private int first;
	private int count;

	public RemindListCallback(OnHttpErrorListener errorListener,
			Context context, OnRemindListListener listener, int userId,
			int first, int count) {
		super(errorListener);
		this.context = context;
		this.listener = listener;
		this.userId = userId;
		this.first = first;
		this.count = count;
	}

	RemindListParser parser = new RemindListParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (parser.errCode == 0 && userId == UserNow.current().userID) {
			UserNow.current().remindCount = parser.remindCount;
			PreferencesUtils.putInt(context, "userInfo", "remindCount",
					UserNow.current().remindCount);
		}
		if (listener != null) {
			listener.getRemindList(parser.errCode, parser.remindCount,
					parser.remindProgramList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new RemindListRequest(userId, first, count).make();
	}

}
