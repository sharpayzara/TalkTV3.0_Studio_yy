package com.sumavision.talktv2.http.json;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 看视频加经验
 */
public class ChannelStatRequest extends BaseJsonRequest {

	private String array;
	private Context context;

	public ChannelStatRequest(Context context, String array) {
		this.context = context;
		this.array = array;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.channelStat);
			holder.put("version", JSONMessageType.APP_VERSION_314);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("content",new JSONArray(array));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("mylog", holder.toString());
		return holder;
	}

}
