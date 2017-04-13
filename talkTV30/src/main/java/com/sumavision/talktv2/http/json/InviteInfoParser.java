package com.sumavision.talktv2.http.json;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 摇一摇解析
 * 
 * @author cx
 * @version
 * @description
 */
public class InviteInfoParser extends BaseJsonParser {
	public User userInfo;
	public String rules;
	public String vipPrivilege;
	public String inviteCode;
	@Override
	public void parse(JSONObject jsonObject) {
		try {
			Log.e("addex",jsonObject.toString());
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content != null){
					JSONObject userObj = content.optJSONObject("userInfo");
					userInfo = new User();
					userInfo.userId = userObj.optInt("id");
					userInfo.isVip = userObj.optInt("isVip") == 1 ? true:false;
					userInfo.inviteCount = userObj.optInt("inviteCount");
					userInfo.leftVipDay = userObj.optInt("hasDay");
					userInfo.getVipDay = userObj.optInt("getVipDay");
				}
				rules = content.optString("rules");
				vipPrivilege = content.optString("authority");
				inviteCode = content.optString("inviteCode");
				UserNow.current().isVip = userInfo.isVip;
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
