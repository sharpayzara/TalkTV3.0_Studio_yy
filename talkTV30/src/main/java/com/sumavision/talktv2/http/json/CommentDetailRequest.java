package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

/**
 * @author hpb
 * @version 3.0
 * @description 评论详情请求类
 * @changLog
 */
public class CommentDetailRequest extends BaseJsonRequest {

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();

		try {
			holder.put("method", Constants.talkDetail);
			holder.put("version", JSONMessageType.APP_VERSION);
			holder.put("client", JSONMessageType.SOURCE);
			holder.put("jsession", UserNow.current().jsession);
			holder.put("talkId", CommentData.current().talkId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
