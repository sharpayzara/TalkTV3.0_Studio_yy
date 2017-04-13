package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;

public class ExchangeGoodsParser extends BaseJsonParser {
	ExchangeGood good;

	public ExchangeGoodsParser(ExchangeGood good) {
		this.good = good;
	}

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			errMsg=obj.optString("msg");
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject goodsObj = content.getJSONObject("userGoods");
				JSONObject userInfo = content.optJSONObject("newUserInfo");
				good.userGoodsId = goodsObj.optInt("id", 0);
				good.type = goodsObj.optInt("goodsType", 0);
				
				setPointInfo(userInfo);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
