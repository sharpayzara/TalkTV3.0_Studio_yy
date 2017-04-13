package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;

/**
 * 商品详情（限时及正常）
 * 
 * @author suma-hpb
 * 
 */
public class GoodsLimitDetailRequest extends BaseJsonRequest {

	long goodsId;

	public GoodsLimitDetailRequest(long goodsId) {
		this.goodsId = goodsId;
	}

	@Override
	public JSONObject make() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("method", "shopGoodsLimitTimeDetail");
			obj.put("version", JSONMessageType.APP_VERSION_THREE);
			obj.put("client", JSONMessageType.SOURCE);
			obj.put("jsession", UserNow.current().jsession);
			obj.put("hotGoodsId", goodsId);
			if (UserNow.current().userID > 0) {
				obj.put("userId", UserNow.current().userID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

}
