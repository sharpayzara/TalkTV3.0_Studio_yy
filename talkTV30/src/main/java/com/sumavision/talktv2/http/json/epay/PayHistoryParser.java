package com.sumavision.talktv2.http.json.epay;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PayHistoryData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 充值记录json解析
 * 
 * @author suma-hpb
 * 
 */
public class PayHistoryParser extends BaseJsonParser {
	public ArrayList<PayHistoryData> historyList = new ArrayList<PayHistoryData>();

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
				JSONArray payrecod = content.getJSONArray("payRecord");
				for (int i = 0; i < payrecod.length(); i++) {
					JSONObject json = payrecod.getJSONObject(i);
					PayHistoryData temp = new PayHistoryData();
					temp.id = json.getLong("id");
					temp.rmb = json.optInt("rmb");
					temp.diamond = json.optInt("diamond");
					temp.date = json.optString("date");
					historyList.add(temp);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
