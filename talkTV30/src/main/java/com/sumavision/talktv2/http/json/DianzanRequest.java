package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

/**
 * 点赞json请求
 * 
 * @author suma-hpb
 * 
 */
public class DianzanRequest extends BaseJsonRequest {
	private Context context;
	int programId;
	int type;

	public DianzanRequest(Context context, int programId, int type) {
		this.context = context;
		this.programId = programId;
		this.type = type;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.programEvaluateAdd);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", "3.0.0");
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("phoneIdentify", AppUtil.getImei(context));
			jsonObject.put("programId", programId);
			jsonObject.put("evaluate", type);
			if (UserNow.current().userID > 0) {
				jsonObject.put("userId", UserNow.current().userID);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
