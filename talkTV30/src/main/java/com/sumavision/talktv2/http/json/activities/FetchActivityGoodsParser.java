package com.sumavision.talktv2.http.json.activities;

import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 活动奖品悄悄领取json解析
 * 
 * @author suma-hpb
 * 
 */
public class FetchActivityGoodsParser extends BaseJsonParser {
	public ExchangeGood exchangeGood = new ExchangeGood();

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
		errMsg = jsonObject.optString("msg", "");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null) {
				JSONObject userGoodsObj = content.optJSONObject("userGoods");
				exchangeGood.userGoodsId = userGoodsObj.optLong("id", 0);
				exchangeGood.hotGoodsId = userGoodsObj.optLong("hotGoodsId", 0);
				exchangeGood.type = userGoodsObj.optInt("goodsType", 0);
			}
		}

	}

}
