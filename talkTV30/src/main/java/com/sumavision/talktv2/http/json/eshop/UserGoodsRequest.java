package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 用户礼品请求json
 * 
 */
public class UserGoodsRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.userGoodsList);
			object.put("version", "3.0.7");
			object.put("client", JSONMessageType.SOURCE);
			object.put("jsession", UserNow.current().jsession);
			object.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

}
