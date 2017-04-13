package com.sumavision.talktv2.http.json.activities;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 活动奖品悄悄领取json请求
 * 
 * @author suma-hpb
 * 
 */
public class FetchActivityGoodsRequest extends BaseJsonRequest {
	private long activityId;
	private long goodsId;
	private String imei;

	public FetchActivityGoodsRequest(long activityId, long goodsId,String imei) {
		super();
		this.activityId = activityId;
		this.goodsId = goodsId;
		this.imei = imei;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.fetchActivityGoods);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("userId", UserNow.current().userID);
			object.put("activityId", activityId);
			object.put("goodsId", goodsId);
			object.put("imei",imei);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
