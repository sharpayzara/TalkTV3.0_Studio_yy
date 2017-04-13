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
public class UserDetailParser extends BaseJsonParser {
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
					JSONObject object = content.optJSONObject("user");
					UserNow.current().name = object.optString("name");
					UserNow.current().iconURL = object.optString("pic");
					UserNow.current().level = object.optInt("level")+"";
					UserNow.current().totalPoint = (int)object.optLong("point");
					UserNow.current().isVip = object.optInt("isVip")==1? true:false;
					UserNow.current().dayLoterry = object.optInt("dayLottery");
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
