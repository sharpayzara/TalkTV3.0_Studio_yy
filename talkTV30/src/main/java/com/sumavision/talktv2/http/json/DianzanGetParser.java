package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 点赞信息获取解析
 * 
 * @author suma-hpb
 * 
 */
public class DianzanGetParser extends BaseJsonParser {

	public int zanType;

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
				JSONObject content = jsonObject.getJSONObject("content");
				boolean evaluated = content.getBoolean("evaluated");
				if (evaluated) {
					zanType = content.getInt("evaluateType");
				}
			}
			errMsg = jsonObject.optString("msg");
		} catch (JSONException e) {
			Log.e("DianzanGetParser", e.toString());
		}

	}

}
