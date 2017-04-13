package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;

/**
 * 领取摇奖积分
 * 
 * @author cx
 * 
 */
public class GetShakePointRequest extends BaseJsonRequest {
	
	private int historyId;
	private Context context;
	
	public GetShakePointRequest(Context context, int historyId) {
		this.historyId = historyId;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "getShakePoint");
			holder.put("version", JSONMessageType.APP_VERSION_310);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("historyId", historyId);
			holder.put("identify", AppUtil.getImei(context));
			holder.put("userId", UserNow.current().userID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
