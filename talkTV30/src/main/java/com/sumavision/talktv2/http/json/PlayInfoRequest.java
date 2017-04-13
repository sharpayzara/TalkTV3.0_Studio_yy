package com.sumavision.talktv2.http.json;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImeiUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 统计播放信息
 */
public class PlayInfoRequest extends BaseJsonRequest {

	private Bundle bundle;
	private Context context;

	public PlayInfoRequest(Context context, Bundle bundle) {
		this.bundle = bundle;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.playInfo);
			holder.put("version", JSONMessageType.APP_VERSION_314);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("watchType", 1);
			holder.put("identify", ImeiUtil.getInstance(context).getUniqueId());
			if (UserNow.current().userID > 0) {
				holder.put("userId", UserNow.current().userID);
			}
			holder.put("progId",bundle.getLong("progId"));
			holder.put("progSubId",bundle.getLong("progSubId"));
			holder.put("progressTime",bundle.getLong("progressTime"));
			holder.put("videoTime",bundle.getLong("videoTime"));
			holder.put("watchTime",bundle.getLong("curPlayTime"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("tvfan_request",holder.toString());
		return holder;
	}

}
