package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class EmergencyDetailRequest extends BaseJsonRequest {

	private long objectId;
	private long zoneId;
	private int type;
	private int way;

	public EmergencyDetailRequest(long objectId, long zoneId, int type, int way) {
		super();
		this.objectId = objectId;
		this.zoneId = zoneId;
		this.type = type;
		this.way = way;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.newsDetail);
			// 2.3.2新版本推荐页面使用
			holder.put("version", "2.3.8");
			holder.put("client", JSONMessageType.SOURCE);
			// 2.3.2新版本推荐页面使用
			holder.put("jsession", UserNow.current().jsession);
			holder.put("objectId", objectId);
			holder.put("zoneId", zoneId);
			holder.put("type", type);
			holder.put("way", way);
			Log.i("mylog", holder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
