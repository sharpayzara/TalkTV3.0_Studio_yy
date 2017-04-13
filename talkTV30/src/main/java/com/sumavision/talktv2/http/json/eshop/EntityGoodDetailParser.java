package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 实体物品详情json解析
 * 
 * @author suma-hpb
 * 
 */
public class EntityGoodDetailParser extends BaseJsonParser {
	private ExchangeGood good;

	public EntityGoodDetailParser(ExchangeGood goods) {
		this.good = goods;
	}

	public ReceiverInfo info = new ReceiverInfo();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject goodsObj = content.getJSONObject("goods");
				good.picDetail = goodsObj.optString("picDetail", "");
				good.picDetail = Constants.picUrlFor + good.picDetail
						+ Constants.PIC_BIG;
				good.name = goodsObj.optString("name", "");
				good.endTime = goodsObj.optString("endTime", "");
				if (good.endTime.length() >= 10) {
					good.endTime = good.endTime.substring(0, 16);
					good.endTime = good.endTime.replaceAll("-", "/");
				}
				JSONObject postObj = content.optJSONObject("post");
				if (postObj != null) {
					info.name = postObj.optString("realName", "");
					info.address = postObj.optString("address", "");
					info.phone = postObj.optString("phone", "");
					info.remark = postObj.optString("remark");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
