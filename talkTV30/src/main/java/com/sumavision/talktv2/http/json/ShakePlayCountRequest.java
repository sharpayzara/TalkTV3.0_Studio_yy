package com.sumavision.talktv2.http.json;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hpb
 * @version 3.0
 * @description 节目播放次数统计
 * @changeLog
 */
public class ShakePlayCountRequest extends BaseJsonRequest {
	int id;
	Context context;
	public ShakePlayCountRequest(Context context,int id) {
		this.id = id;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "shakePlayVideo");
			holder.put("version", JSONMessageType.APP_VERSION_310);
			holder.put("client", JSONMessageType.SOURCE);
			if (id != 0) {
				holder.put("progId", id);
			}
			holder.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0)
				holder.put("userId", UserNow.current().userID);
			holder.put("identify", AppUtil.getImei(context));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
