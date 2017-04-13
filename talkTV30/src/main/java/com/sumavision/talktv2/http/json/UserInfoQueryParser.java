package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 用户信息查询json解析
 * 
 * @author suma-hpb
 * 
 */
public class UserInfoQueryParser extends BaseJsonParser {
	public int diamond;

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject userObj = content.optJSONObject("user");
				if (userObj != null) {
					UserNow.current().totalPoint = userObj.optInt("point", 0);
					diamond = UserNow.current().diamond;
					UserNow.current().diamond = userObj.optInt("diamond", 0);
				}
			}
		} catch (JSONException e) {
			Log.e("UserInfoQueryParser", e.toString());
		}
	}

}
