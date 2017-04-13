package com.sumavision.talktv2.http.json.epay;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.suamvision.data.Product;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 提交订单解析
 * 
 * @author suma-hpb
 * 
 */
public class SubmitOrderParser extends BaseJsonParser {
	public Product product = new Product();

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				product.name = content.optString("name");
				product.body = content.optString("intro");
				product.price = content.optString("money");
				product.orderId = content.optLong("orderId");
			}
		} catch (JSONException e) {
			Log.e("submitOrderParser", e.toString());
		}

	}

}
