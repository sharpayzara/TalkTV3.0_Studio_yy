package com.sumavision.talktv2.http.json.epay;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.DiamondData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 充值规则列表解析
 * 
 * @author suma-hpb
 * 
 */
public class PayRuleListParser extends BaseJsonParser {
	public ArrayList<DiamondData> payList = new ArrayList<DiamondData>();

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
				JSONArray events = content.optJSONArray("payRule");
				if (events != null) {
					for (int i = 0; i < events.length(); i++) {
						DiamondData temp = new DiamondData();
						JSONObject event = events.getJSONObject(i);
						temp.id = event.optLong("id");
						temp.intro = event.optString("shortIntro");
						temp.price = event.getDouble("rmb");
						temp.num = event.optInt("diamond");
						temp.canPayCount = event.optInt("canPayCount");
						temp.alreadyPayCount = event.optInt("alreadyPayCount");
						payList.add(temp);
					}
				}
			}
		} catch (JSONException e) {
			Log.e("PayRuleListParser", e.toString());
		}

	}

}
