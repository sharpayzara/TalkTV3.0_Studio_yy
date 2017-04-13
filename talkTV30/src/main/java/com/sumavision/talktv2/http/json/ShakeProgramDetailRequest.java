package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 摇一摇节目页(3.1.0)请求
 *
 * @author suma-hpb
 *
 */
public class ShakeProgramDetailRequest extends BaseJsonRequest {

	long programId;
	Context context;
	int subId;
	/**
	 * @param programId
	 */
	public ShakeProgramDetailRequest(long programId, Context context) {
		this.programId = programId;
		this.context = context;
	}

	public ShakeProgramDetailRequest(long programId, int subId, Context context) {
		this.programId = programId;
		this.context = context;
		this.subId = subId;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.shakeProgramDetail);
			requestObj.put("version", JSONMessageType.APP_VERSION_310);
			requestObj.put("progId", programId);
			requestObj.put("imei", AppUtil.getImei(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return requestObj;
	}
}
