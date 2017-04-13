package com.sumavision.talktv2.http.json.eshop;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 虚拟物品详情json解析
 * 
 * @author suma-hpb
 * 
 */
public class VirtualGoodDetailParser extends BaseJsonParser {
	private ExchangeGood good;

	public VirtualGoodDetailParser(ExchangeGood good) {
		this.good = good;
	}

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject goodsObj = content.getJSONObject("goods");
				good.code = goodsObj.optString("code", "");
				good.picDetail = goodsObj.optString("picDetail", "");
				good.picDetail = Constants.picUrlFor + good.picDetail
						+ Constants.PIC_BIG;
				good.name = goodsObj.optString("name", "");
				good.endTime = goodsObj.optString("endTime", "");
				if (good.endTime.length() >= 10) {
					good.endTime = good.endTime.substring(0, 16);
					good.endTime = good.endTime.replaceAll("-", "/");
				}
				good.useIntro = goodsObj.optString("useIntro", "");
				JSONArray userArr = content.optJSONArray("user");
				if (userArr != null && userArr.length() > 0) {
					ArrayList<ReceiverInfo> nameList = new ArrayList<ReceiverInfo>();
					for (int pos = 0; pos < userArr.length(); pos++) {
						ReceiverInfo info = new ReceiverInfo();
						info.name = userArr.getJSONObject(pos).optString(
								"userName");
						nameList.add(info);
					}
					good.userList = nameList;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
