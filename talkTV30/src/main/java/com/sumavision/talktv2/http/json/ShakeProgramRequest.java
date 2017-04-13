package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 摇一摇
 * 
 * @author cx
 * 
 */
public class ShakeProgramRequest extends BaseJsonRequest {
	
	private String tagIds;
	private Context context;

	public ShakeProgramRequest(Context context, String tagIds) {
		this.tagIds = tagIds;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "shakeProgram");
			holder.put("version", "3.1.0");
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("tagIds", tagIds);
			holder.put("identify", AppUtil.getImei(context));
			if (UserNow.current().userID > 0) {
				holder.put("userId", UserNow.current().userID);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("mylog", holder.toString());
		return holder;
	}

}
