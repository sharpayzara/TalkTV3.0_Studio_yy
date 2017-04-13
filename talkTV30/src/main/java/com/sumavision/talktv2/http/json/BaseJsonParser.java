package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Logcat;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseJsonParser {

	public int errCode = 1;
	public String errMsg;

	public UserNow userInfo = new UserNow();// 积分系统信息

	protected Logcat mLog = new Logcat();

	/**
	 * 解析json
	 * 
	 * @param jsonObject
	 */
	public abstract void parse(JSONObject jsonObject);

	/**
	 * 积分系统信息
	 * 
	 * @param userInfoObj
	 */
	public void setPointInfo(JSONObject userInfoObj) {
		if (userInfoObj != null && userInfoObj.has("point")) {
			UserNow.current().point = userInfoObj.optInt("point");
			userInfo.changeLevel = userInfoObj.optInt("changeLevel");
			userInfo.exp = userInfoObj.optInt("exp");
			userInfo.level = userInfoObj.optString("level");
			userInfo.point = userInfoObj.optInt("point");
			userInfo.exp = userInfoObj.optInt("totalExp");
			userInfo.totalPoint = userInfoObj.optInt("totalPoint");
			userInfo.vipIncPoint = userInfoObj.optInt("vipIncPoint");
			UserNow.current().action = userInfoObj.optString("action");
			if (userInfo.point>0){
				UserNow.current().setTotalPoint(userInfo.totalPoint,userInfo.vipIncPoint);
			}
		}
	}

	protected void setUserInfo(JSONObject user) throws JSONException {
		UserNow.current().name = user.getString("userName");
		UserNow.current().userID = user.optInt("userId");
		UserNow.current().gender = user.optInt("sex");
		UserNow.current().totalPoint = user.optInt("totalPoint");
		UserNow.current().diamond = user.optInt("totalDiamond");
		UserNow.current().exp = user.optInt("totalExp");
		UserNow.current().level = user.getString("level");
		UserNow.current().fansCount = user.optInt("fensiCount");
		UserNow.current().friendCount = user.optInt("guanzhuCount");
		UserNow.current().commentCount = user.optInt("talkCount");
		UserNow.current().chaseCount = user.optInt("chaseCount");
		UserNow.current().privateMessageAllCount = user.optInt("mailCount");
		UserNow.current().remindCount = user.optInt("remindCount");
		UserNow.current().badgesCount = user.optInt("badgeCount");
		UserNow.current().atMeCount = user.optInt("talkAtCount");
		UserNow.current().replyMeCount = user.optInt("replyByCount");
		if (user.has("sessionId")) {
			UserNow.current().sessionID = user.optString("sessionId");
		}
		String signature = user.optString("signature");
		if (signature.equals("")) {
			UserNow.current().signature = "这个家伙神马也木有留下";
		} else {
			UserNow.current().signature = signature;
		}
		UserNow.current().iconURL = user.optString("pic");
		UserNow.current().eMail = user.optString("email");
	}
}
