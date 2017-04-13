package com.sumavision.talktv2.http.json;

import android.content.Context;
import android.text.TextUtils;

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
public class PlayCountRequest extends BaseJsonRequest {
	int id;
	int subId;
	int channelId;
	Context context;
	String version;

	public PlayCountRequest(Context context, int id, int subId, int channelId,String version) {
		this.id = id;
		this.subId = subId;
		this.channelId = channelId;
		this.context = context;
		this.version = version;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "playVideo");
			holder.put("version", JSONMessageType.APP_VERSION_309);
			holder.put("client", JSONMessageType.SOURCE);
			if (id != 0) {
				holder.put("programId", id);
				holder.put("progSubId",subId);
			}
			holder.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0)
				holder.put("userId", UserNow.current().userID);
			if (channelId != 0)
				holder.put("channelId", channelId);
			holder.put("imei", AppUtil.getImei(context));
			if (!TextUtils.isEmpty(version)){
				holder.put("recVersion",version);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
