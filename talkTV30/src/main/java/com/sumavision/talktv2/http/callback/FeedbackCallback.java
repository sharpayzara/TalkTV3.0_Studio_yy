package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.FeedbackParser;
import com.sumavision.talktv2.http.json.FeedbackRequest;
import com.sumavision.talktv2.http.listener.OnFeedbackListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 反馈回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackCallback extends BaseCallback {

	private FeedbackData feedBack;
	private OnFeedbackListener listener;

	public FeedbackCallback(OnHttpErrorListener errorListener,
			FeedbackData feedBack, OnFeedbackListener listener) {
		super(errorListener);
		this.feedBack = feedBack;
		this.listener = listener;
	}

	FeedbackParser parser = new FeedbackParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
				if (parser.userInfo.point > 0) {
					UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
				}
			}
			listener.feedbackResult(parser.errCode);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new FeedbackRequest(feedBack).make();
	}

}
