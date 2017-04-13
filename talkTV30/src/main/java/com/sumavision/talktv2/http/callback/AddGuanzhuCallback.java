package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.AddGuanzhuRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.listener.OnAddGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;

/**
 * 添加关注回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class AddGuanzhuCallback extends BaseCallback {

	private int userId;
	private OnAddGuanzhuListener listener;

	public AddGuanzhuCallback(int userId, OnAddGuanzhuListener listener,
			OnHttpErrorListener errorListener) {
		super(errorListener);
		this.userId = userId;
		this.listener = listener;
	}

	ResultParser parser = new ResultParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);

	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
//			if (parser.userInfo.point > 0) {
//				UserNow.current().setTotalPoint(parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//			}
			listener.addGuanzhuResult(parser.errCode, parser.badgeList);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new AddGuanzhuRequest(userId).make();
	}

}
