package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 摇一摇上传视频
 * 
 * @author cx
 * 
 */
public class ShakePlayVideoRequest extends BaseJsonRequest {
	
	private int programId;
	private Context context;

	public ShakePlayVideoRequest(Context context, String programId) {
		this.programId = Integer.parseInt(programId);
		this.context = context;
	}
	
	public ShakePlayVideoRequest(Context context, int programId) {
		this.programId = programId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "shakePlayVideo");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", UserNow.current().userID);
			holder.put("identify", AppUtil.getImei(context));
			holder.put("progId", programId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
