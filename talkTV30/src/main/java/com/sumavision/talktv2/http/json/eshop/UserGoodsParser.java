package com.sumavision.talktv2.http.json.eshop;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 用户礼品列表解析
 * 
 * @author suma-hpb
 * 
 */
public class UserGoodsParser extends BaseJsonParser {
	public ArrayList<ExchangeGood> goodsList = new ArrayList<ExchangeGood>();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONArray userGoods = content.optJSONArray("userGoods");
				if (userGoods != null && userGoods.length() > 0) {
					for (int index = 0; index < userGoods.length(); index++) {
						ExchangeGood good = new ExchangeGood();
						JSONObject goodObj = userGoods.getJSONObject(index);
						good.userGoodsId = goodObj.optInt("userGoodsId", 0);
						good.id = goodObj.optInt("id");
						good.name = goodObj.optString("name");
						good.pic = goodObj.optString("pic");
						good.type = goodObj.optInt("type");
						good.status = goodObj.optInt("status");
						good.fetchType = goodObj.optInt("fetchType");
						good.isHolidayGoods = goodObj.optInt("holidayGoods") == 1 ? true:false;
						goodsList.add(good);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
