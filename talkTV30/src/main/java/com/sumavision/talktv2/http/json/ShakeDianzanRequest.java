package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 点赞json请求
 * 
 * @author suma-hpb
 * 
 */
public class ShakeDianzanRequest extends BaseJsonRequest {
	int programId;
	Context context;
	public ShakeDianzanRequest(Context context,int programId) {
		this.programId = programId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.shakeEvaluate);
			requestObj.put("version", JSONMessageType.APP_VERSION_310);
			requestObj.put("progId", programId);
			requestObj.put("identify", AppUtil.getImei(context));
			if (UserNow.current().userID > 0) {
				requestObj.put("userId", UserNow.current().userID);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return requestObj;
	}

}
