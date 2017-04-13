package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 推荐软件下载解析
 * 
 * @author suma-hpb
 * 
 */
public class DownloadRecommendAppParser extends BaseJsonParser {
	public int totalPoint;

	@Override
	public void parse(JSONObject jsonObject) {
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
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content")
						.getJSONObject("newUserInfo");
				totalPoint = content.getInt("totalPoint");
				UserNow.current().exp = content.getInt("exp");
				UserNow.current().point = content.optInt("point");
				UserNow.current().level = content.optString("level");
				UserNow.current().changeLevel = content.getInt("changeLevel");
			}
		} catch (JSONException e) {
			Log.e("DownloadRecommendAppParser", e.toString());
		}

	}

}
