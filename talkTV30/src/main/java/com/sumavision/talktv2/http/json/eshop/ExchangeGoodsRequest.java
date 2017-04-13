package com.sumavision.talktv2.http.json.eshop;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import comsumavision.utils.ExchangeSecureUtils;

/**
 * 物品兑换json请求
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeGoodsRequest {
	private long hotGoodsId;
	private String key;

	/**
	 * @param hotGoodsId
	 * @param key
	 */
	public ExchangeGoodsRequest(long hotGoodsId, String key) {
		super();
		this.hotGoodsId = hotGoodsId;
		this.key = key;
	}

	public String make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.exchangeGoods);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("hotGoodsId", hotGoodsId);
			object.put("userId", UserNow.current().userID);
			object.put("useDiamond", false);// 后期待完善
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String request =  new ExchangeSecureUtils()
				.textEncrypt(object.toString(), key);
		return request;
	}

}
