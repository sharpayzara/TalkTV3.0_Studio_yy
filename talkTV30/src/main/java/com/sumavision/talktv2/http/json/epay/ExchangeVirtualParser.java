package com.sumavision.talktv2.http.json.epay;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.ExchangeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 兑换虚拟货币json解析
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeVirtualParser extends BaseJsonParser {
	public ExchangeData exchangeData = new ExchangeData();

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
				exchangeData.diamondEnough = content
						.optBoolean("diamondEnough");
				exchangeData.success = content.optBoolean("success");
				exchangeData.point = content.optInt("point");
				exchangeData.diamond = content.optInt("diamond");
				exchangeData.show = content.optInt("show");
			}
		} catch (JSONException e) {
			Log.e("ExchangeVirtualParser", e.toString());
		}

	}

}
