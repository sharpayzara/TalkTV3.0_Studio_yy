package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * 节日抽奖活动中奖名单
 * 
 * @version
 * @description
 */
public class FestivalListRequest extends BaseJsonRequest {

	private int id;
	
	public FestivalListRequest(int id) {
		this.id = id;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.activityWinnerList);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", "3.0.7");
			holder.put("jsession", UserNow.current().jsession);
			holder.put("userId", UserNow.current().userID);
			holder.put("activityId", id);
			holder.put("first", 0);
			holder.put("count", 0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
