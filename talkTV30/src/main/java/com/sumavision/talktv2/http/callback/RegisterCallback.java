package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.LogInParser;
import com.sumavision.talktv2.http.json.RegisterRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRegisterListener;
import com.sumavision.talktv2.utils.DialogUtil;

import de.greenrobot.event.EventBus;

/**
 * 注册回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RegisterCallback extends BaseCallback {

	private OnRegisterListener listener;

	public RegisterCallback(OnHttpErrorListener errorListener,
			OnRegisterListener listener) {
		super(errorListener);
		this.listener = listener;
	}

	LogInParser parser = new LogInParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);
	}

	@Override
	protected void onResponseDelegate() {
		if (listener != null) {
			if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
				if (UserNow.current().point > 0) {
					if (UserNow.current().action == null) {
						UserNow.current().action = "";
					}
					String vipStr="";
					if (UserNow.current().vipIncPoint>0){
						vipStr = "VIP加成"+UserNow.current().vipIncPoint+"积分";
					}
					DialogUtil.updateScoreToast(UserNow.current().action + " +" + (UserNow.current().point) + "积分"+vipStr);
				}
				UserNow.current().totalPoint = parser.userInfo.totalPoint;
				EventBus.getDefault().post(new UserInfoEvent());
				UserNow.current().inviteCode = "";//置空邀请码
			}
			listener.registerResult(parser.errCode, parser.errMsg);
		}

	}

	@Override
	public JSONObject makeRequest() {
		return new RegisterRequest().make();
	}

}
