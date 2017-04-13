package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.NewActivityCompleteRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnNewActivityCompleteListener;

public class NewActivityCompleteCallback extends BaseCallback {

	private Context context;
	private long activityId;
	private OnNewActivityCompleteListener listener;

	public NewActivityCompleteCallback(OnHttpErrorListener errorListener,
			Context context, long activityId,
			OnNewActivityCompleteListener listener) {
		super(errorListener);
		this.context = context;
		this.activityId = activityId;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
//		if (parser.userInfo.point > 0) {
//			UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//		}
		if (listener != null) {
			listener.newActivityCompleteResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new NewActivityCompleteRequest(context, activityId).make();
	}

}
