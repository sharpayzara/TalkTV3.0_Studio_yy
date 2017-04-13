package com.sumavision.talktv2.http.json.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.Good;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 刮奖解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityShakeParser extends BaseJsonParser {
	public Good good = new Good();

	@Override
	public void parse(JSONObject jsonObject) {
		good = new Good();
		Log.i("mylog", "shake parser:"+jsonObject.toString());
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
				JSONObject content = jsonObject.getJSONObject("content");
				if (content.has("goods")) {
					JSONObject picsArray = content.getJSONObject("goods");
					good.id = picsArray.optLong("id", 0);
					good.name = picsArray.optString("name");
					good.pic = picsArray.optString("pic");
					good.level = picsArray.optInt("rewardLevel");
					good.goodsType = picsArray.optInt("goodsType");
					if (good.goodsType == 3) {
						good.point = picsArray.optInt("point");
					}
				}
			}
		} catch (JSONException e) {
		}

	}

}
