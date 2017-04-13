package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 过期礼品详情json解析
 * 
 * @author suma-hpb
 * 
 */
public class PastDueGoodsDetailParser extends BaseJsonParser {
	public ExchangeGood good = new ExchangeGood();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject goodsObj = content.getJSONObject("goods");
				good.picDetail = Constants.picUrlFor
						+ goodsObj.optString("picDetail", "")
						+ Constants.PIC_BIG;
				good.name = goodsObj.optString("name", "");
				good.endTime = goodsObj.optString("endTime", "");
				if (good.endTime.length() > 10) {
					good.endTime = good.endTime.substring(0, 16);
					good.endTime = good.endTime.replaceAll("-", "/");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
