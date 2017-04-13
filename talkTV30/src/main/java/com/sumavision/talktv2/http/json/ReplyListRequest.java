package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

public class ReplyListRequest extends BaseJsonRequest {

	private int first;
	private int count;

	public ReplyListRequest(int first, int count) {
		super();
		this.first = first;
		this.count = count;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.replyList);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("version", JSONMessageType.APP_VERSION_309);
			holder.put("talkId", CommentData.current().talkId);
			holder.put("first", first);
			holder.put("count", count);
			holder.put("jsession", UserNow.current().jsession);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}

}
