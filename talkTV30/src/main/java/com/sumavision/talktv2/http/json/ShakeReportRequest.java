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
public class ShakeReportRequest extends BaseJsonRequest {
	int programId;
	String content;
	Context context;
	public ShakeReportRequest(Context context,int programId,String content) {
		this.programId = programId;
		this.content = content;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		try {
			addMethod(Constants.informProgram);
			requestObj.put("version", JSONMessageType.APP_VERSION_310);
			requestObj.put("progId", programId);
			requestObj.put("content", content);
			requestObj.put("phoneIdentify", AppUtil.getImei(context));
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
