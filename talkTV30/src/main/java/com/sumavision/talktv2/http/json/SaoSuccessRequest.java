package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 扫码成功后对应请求
 * 
 * @author cx
 * 
 */
public class SaoSuccessRequest extends BaseJsonRequest {
	
	private Context context;
	private String tag;

	public SaoSuccessRequest(Context context, String tag) {
		this.context = context;
		this.tag = tag;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "saoSuccess");
			holder.put("version", "3.1.0");
			holder.put("tag", tag);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("identify", AppUtil.getImei(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
