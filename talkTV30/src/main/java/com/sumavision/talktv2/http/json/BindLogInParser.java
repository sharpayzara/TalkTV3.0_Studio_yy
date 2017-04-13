package com.sumavision.talktv2.http.json;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BindLogInParser extends BaseJsonParser {

	public UserNow user = new UserNow();

	@Override
	public void parse(JSONObject jsonObject) {
		JSONObject userObj = null;

		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			errMsg = jsonObject.optString("msg");
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				userObj = jsonObject.getJSONObject("content");
				setUserInfo(userObj);
				JSONObject userNowInfo = userObj.optJSONObject("newUserInfo");
				setPointInfo(userNowInfo);
				JSONArray newBadge = userObj.optJSONArray("newBadge");
				if (newBadge != null) {
					List<BadgeData> lb = new ArrayList<BadgeData>();
					for (int i = 0; i < newBadge.length(); i++) {
						JSONObject badge = newBadge.getJSONObject(i);
						BadgeData b = new BadgeData();
						b.picPath = badge.getString("pic");
						b.name = badge.getString("name");
						lb.add(b);
					}
					UserNow.current().newBadge = lb;
				}
				UserNow.current().isVip = userObj.optInt("isVip")==1? true:false;
				UserNow.current().isSelf = true;
				UserNow.current().isLogedIn = true;
				UserNow.current().showAlert = TextUtils.isEmpty(userObj.optString("alert"))?false:true;
				UserNow.current().dayLoterry = userObj.optInt("dayLottery");
			}
		} catch (JSONException e) {
		}
		user = UserNow.current();
	}

}
