package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.utils.Constants;

/**
 * 视频源子节目请求
 * 
 * @author suma-hpb
 * 
 */
public class PlatVideoRequest extends BaseJsonRequest {

	int platId;
	long programId;
	int first;
	int count;
	int subId;

	/**
	 * @param platId
	 * @param programId
	 */
	public PlatVideoRequest(int platId, long programId, int subId, int first, int count) {
		this.platId = platId;
		this.programId = programId;
		this.first = first;
		this.count = count;
		this.subId = subId;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.platFormProgramSubList);
			requestObj.put("version", "3.0.9");
			requestObj.put("platFormId", platId);
			requestObj.put("programId", programId);
			requestObj.put("first", first);
			requestObj.put("count", count);
			if (subId != 0) {
				requestObj.put("subId", subId);
			}
			Log.i("mylog", requestObj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestObj;
	}

}
