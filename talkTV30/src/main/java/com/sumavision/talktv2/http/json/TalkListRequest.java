package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 */
public class TalkListRequest extends BaseJsonRequest {

	private int first;
	private int count;
	private int topicId;

	public TalkListRequest(int topicId, int first, int count) {
		super();
		this.topicId = topicId;
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", "talkList");
			holder.put("version", "3.0.9");
			holder.put("jsession", UserNow.current().jsession);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("topicId", topicId);
			holder.put("first", first);
			holder.put("count", count);
			Log.e("msg_getcomment", holder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
