package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

/**
 * 获取新版本请求封装
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GetAppNewVersionRequest extends BaseJsonRequest {

	private Context context;

	public GetAppNewVersionRequest(Context context) {
		super();
		this.context = context;
	}

	@Override
	public JSONObject make() {
		String vid = AppUtil.getAppVersionId(context);
		JSONObject holder = new JSONObject();
		try {
			if (vid != null) {
				holder.put("vid", vid);
			}
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("method", Constants.versionLatest);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("jsession", UserNow.current().jsession);

		} catch (JSONException e) {
		}
		return holder;
	}

}
