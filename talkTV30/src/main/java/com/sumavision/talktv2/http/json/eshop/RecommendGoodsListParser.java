package com.sumavision.talktv2.http.json.eshop;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 商城详情解析
 * 
 * @author suma-hpb
 * 
 */
public class RecommendGoodsListParser extends BaseJsonParser {
//	public ArrayList<ShoppingHomeBean> shoplist = new ArrayList<ShoppingHomeBean>();
	public ArrayList<HotGoodsBean> hotGoodsList = new ArrayList<HotGoodsBean>();
	
	@Override
	public void parse(JSONObject jAData) {
		try {
			if (jAData.has("code")) {
				errCode = jAData.optInt("code");
			} else if (jAData.has("errcode")) {
				errCode = jAData.optInt("errcode");
			} else if (jAData.has("errorCode")) {
				errCode = jAData.optInt("errorCode");
			}
			if (jAData.has("jsession")) {
				UserNow.current().jsession = jAData.optString("jsession");
			}

			if (jAData.has("sessionId")) {
				UserNow.current().sessionID = jAData.optString("sessionId");
			}

			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jAData.optJSONObject("content");
				JSONArray recommandGood = content.optJSONArray("group");
				for (int i = 0; i < recommandGood.length(); i++) {
					JSONObject recommand = recommandGood.optJSONObject(i);
					if (recommand.has("hotGoods")) {
						JSONArray hotGoods = recommand.optJSONArray("hotGoods");
						for (int j = 0; j < hotGoods.length(); j++) {
							HotGoodsBean hotbean = new HotGoodsBean();
							JSONObject hotgood = hotGoods.getJSONObject(j);
							hotbean.id = hotgood.optLong("id");
							hotbean.goodsId = hotgood.optLong("goodsId");
							hotbean.name = hotgood.optString("name");
							hotbean.point = hotgood.optInt("point");
							hotbean.pic = hotgood.optString("pic");
							hotGoodsList.add(hotbean);
						}
					}
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
