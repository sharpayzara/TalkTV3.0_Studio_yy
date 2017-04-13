package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 过期礼品详情json请求
 * 
 * @author suma-hpb
 * 
 */
public class PastDueGoodsDetailRequest extends BaseJsonRequest {

	private long userGoodsId;

	public PastDueGoodsDetailRequest(long userGoodsId) {
		this.userGoodsId = userGoodsId;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.pastDueGoodsDetail);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("userId", UserNow.current().userID);
			object.put("userGoodsId", userGoodsId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
