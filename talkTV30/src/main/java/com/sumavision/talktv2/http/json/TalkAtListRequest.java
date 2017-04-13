package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author
 * @version 2.2
 * @description 被@列表请求类
 * @changLog
 */
public class TalkAtListRequest extends BaseJsonRequest {

	private int first;
	private int count;

	/**
	 * 原othercachedata中offset、pagecount
	 * 
	 * @param first
	 * @param count
	 */
	public TalkAtListRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.talkAtList);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("userId", UserNow.current().userID);
			holder.put("first", first);
			holder.put("count", count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
