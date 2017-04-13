package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * */
public class FestivalParser extends BaseJsonParser {
	
	public ArrayList<HotGoodsBean> listGoods;
	public int count;
	public String tips;
	public String rule;
	
	@Override
	public void parse(JSONObject jsonObject) {
		listGoods = null;
		listGoods = new ArrayList<HotGoodsBean>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.optJSONObject("content");
					count = content.optInt("totalCount");
					tips = content.optString("tips");
					rule = content.optString("ruleIntro");
					if (content.has("goods")) {
						JSONArray goods = content.optJSONArray("goods");
						for (int i = 0; i < goods.length(); i++) {
							JSONObject obj = goods.optJSONObject(i);
							HotGoodsBean good = new HotGoodsBean();
							good.id = obj.optLong("id");
							good.goodsId = obj.optLong("hotGoodsId");
							good.goodsType = obj.optInt("goodsType");
							good.name = obj.optString("name");
							good.pic = obj.optString("pic");
							good.intro = obj.optString("shortIntro");
							listGoods.add(good);
						}
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
