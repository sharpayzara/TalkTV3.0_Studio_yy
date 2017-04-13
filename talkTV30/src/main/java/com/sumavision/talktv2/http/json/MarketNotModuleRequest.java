package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;


import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 不可用模块请求
 * 
 * 
 */
public class MarketNotModuleRequest extends BaseJsonRequest {
	
	private String marketCode;
	
	public MarketNotModuleRequest(String marketCode) {
		this.marketCode = marketCode;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.marketNotModule);
			requestObj.put("version", "3.0.5");
			requestObj.put("userId", UserNow.current().userID);
			requestObj.put("marketCode", marketCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
