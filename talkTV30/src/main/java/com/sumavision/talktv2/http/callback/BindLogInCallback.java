package com.sumavision.talktv2.http.callback;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BindLogInParser;
import com.sumavision.talktv2.http.json.bindLogInRequest;
import com.sumavision.talktv2.http.listener.OnBindLogInListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.utils.DialogUtil;

import de.greenrobot.event.EventBus;

/**
 * 绑定登录回调
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindLogInCallback extends BaseCallback {

	private OnBindLogInListener listener;
	ThirdPlatInfo thirdInfo;

	public BindLogInCallback(ThirdPlatInfo thirdInfo,
			OnHttpErrorListener errorListener, OnBindLogInListener listener) {
		super(errorListener);
		this.listener = listener;
		this.thirdInfo = thirdInfo;

	}

	BindLogInParser parser = new BindLogInParser();

	@Override
	public void parseNetworkRespose(JSONObject jsonObject) {
		parser.parse(jsonObject);

	}

	@Override
	protected void onResponseDelegate() {
//		if (UserNow.current().point > 0) {
//			if (UserNow.current().action == null) {
//				UserNow.current().action = "";
//			}
//			String vipStr="";
//			if (UserNow.current().vipIncPoint>0){
//				vipStr = "VIP加成"+UserNow.current().vipIncPoint+"积分";
//			}
//			DialogUtil.updateScoreToast(UserNow.current().action + " +"
//					+ (UserNow.current().point) + "积分"+vipStr);
//		}
		if (parser.userInfo.totalPoint>0){
			UserNow.current().totalPoint = parser.userInfo.totalPoint;
		}
		EventBus.getDefault().post(new UserInfoEvent());
		if (listener != null) {
			listener.bindLogInResult(parser.errCode, parser.errMsg, parser.user);
		}
	}

	@Override
	public JSONObject makeRequest() {
		return new bindLogInRequest(thirdInfo).make();
	}

}
