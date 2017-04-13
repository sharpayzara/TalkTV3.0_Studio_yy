package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 账号绑定请求封装
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HandleAdvertiseRequest extends BaseJsonRequest {

	Context context;
	int type;
	int adType;//0展示、1点击

	public HandleAdvertiseRequest(Context context,int type) {
		this.context = context;
		this.type = type;
		adType = 0;
	}
	public HandleAdvertiseRequest(Context context,int type, int adType) {
		this.context = context;
		this.type = type;
		this.adType = adType;
	}

	@Override
	public JSONObject make() {

		try {
			addMethod(Constants.handleAdvertise);
			requestObj.put("version", JSONMessageType.APP_VERSION_ONE);
			requestObj.put("advertiseTypeId",type);
			requestObj.put("imei", AppUtil.getImei(context));
			requestObj.put("type",adType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
