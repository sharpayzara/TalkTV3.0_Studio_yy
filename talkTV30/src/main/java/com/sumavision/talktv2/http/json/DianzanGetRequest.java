package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 点赞信息获取json请求
 * 
 * @author suma-hpb
 * 
 */
public class DianzanGetRequest extends BaseJsonRequest {
	int programSubId;
	private Context context;

	public DianzanGetRequest(Context context, int programSubId) {
		this.context = context;
		this.programSubId = programSubId;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", "checkProgramEvaluate");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("version", "2.5.4");
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("phoneIdentify", AppUtil.getImei(context));
			jsonObject.put("programSubId", programSubId);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
