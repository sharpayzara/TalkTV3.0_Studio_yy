package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 摇一摇节目点赞
 * 
 * @author cx
 * 
 */
public class ShakeEvaluateRequest extends BaseJsonRequest {
	
	private int programId;
	private Context context;
	
	public ShakeEvaluateRequest(Context context, int programId) {
		this.programId = programId;
		this.context = context;
	}
	
	public ShakeEvaluateRequest(Context context, String programId) {
		this.programId = Integer.parseInt(programId);
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "shakeEvaluate");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("progId", programId);
			holder.put("identify", AppUtil.getImei(context));
			holder.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
