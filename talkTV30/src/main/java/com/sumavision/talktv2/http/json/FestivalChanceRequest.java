package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 节日抽奖活动查询抽奖机会
 * 
 * @version
 * @description
 */
public class FestivalChanceRequest extends BaseJsonRequest {

	private int id;
	
	public FestivalChanceRequest(int id) {
		this.id = id;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.findShakeCount);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", "3.0.7");
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", UserNow.current().userID);
			holder.put("activityId", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
