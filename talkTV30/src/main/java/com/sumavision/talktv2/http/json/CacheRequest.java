package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;


import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 离线下载后请求
 * 
 * @author suma-hpb
 * 
 */
public class CacheRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.offlineDownload);
			requestObj.put("version", "2.6.0");
			requestObj.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
