package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.UserUpdateParser;
import com.sumavision.talktv2.http.json.UserUpdateRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnUserUpdateListener;

/**
 * 用户信息更新回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class UserUpdateCallback extends BaseCallback {
	private OnUserUpdateListener listener;
	private UserModify userModify;

	public UserUpdateCallback(OnHttpErrorListener errorListener,
			OnUserUpdateListener listener, UserModify userModify) {
		super(errorListener);
		this.listener = listener;
		this.userModify = userModify;
	}

	UserUpdateParser parser = new UserUpdateParser();

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
			listener.updateUserResult(parser.errCode, parser.errMsg);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new UserUpdateRequest(userModify).make();
	}

}
