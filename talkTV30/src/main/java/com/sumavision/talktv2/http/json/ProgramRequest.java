package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 节目页(3.0)请求
 * 
 * @author suma-hpb
 * 
 */
public class ProgramRequest extends BaseJsonRequest {

	long programId;
	Context context;
	int subId;
	int point;
	/**
	 * @param programId
	 */
	public ProgramRequest(long programId, Context context) {
		this.programId = programId;
		this.context = context;
	}
	
	public ProgramRequest( Context context, long programId, int subId) {
		this.programId = programId;
		this.context = context;
		this.subId = subId;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.programDetail);
			requestObj.put("version", JSONMessageType.APP_VERSION_311);
			requestObj.put("programId", programId);
			if (UserNow.current().userID > 0) {
				requestObj.put("userId", UserNow.current().userID);
			}
			requestObj.put("imei", AppUtil.getImei(context));
			if (subId != 0) {
				requestObj.put("subId", subId);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return requestObj;
	}
}
