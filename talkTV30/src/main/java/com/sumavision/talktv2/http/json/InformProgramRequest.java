package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 举报接口
 * 
 * @author cx
 * 
 */
public class InformProgramRequest extends BaseJsonRequest {
	
	private Context context;
	private int programId;
	private String content;

	public InformProgramRequest(Context context, int programId, String content) {
		this.programId = programId;
		this.content = content;
		this.context = context;
	}
	
	public InformProgramRequest(Context context, String programId, String content) {
		this.programId = Integer.parseInt(programId);
		this.content = content;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "informProgram");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("progId", programId);
			holder.put("content", content);
			holder.put("phoneIdentify", AppUtil.getImei(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
