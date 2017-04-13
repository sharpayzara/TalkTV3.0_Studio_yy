package com.sumavision.talktv2.http.json;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.tencent.connect.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 摇一摇解析
 * 
 * @author cx
 * @version
 * @description
 */
public class AddExpParser extends BaseJsonParser {
	private User userInfo;
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
					userInfo.name = userObj.optString("name");
					userInfo.iconURL = userObj.optString("pic");
					userInfo.level = userObj.optInt("level")+"";
					if (!TextUtils.isEmpty(userInfo.level) && !userInfo.level.equals("0")){
						UserNow.current().level = userInfo.level;
						UserNow.current().exp = userObj.optInt("exp");
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
