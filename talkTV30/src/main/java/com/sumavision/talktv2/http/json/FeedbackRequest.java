package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

/**
 * 反馈请求数据
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackRequest extends BaseJsonRequest {

	private FeedbackData fback;

	public FeedbackRequest(FeedbackData fback) {
		this.fback = fback;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.feedbackAdd);
			jsonObject.put("version", JSONMessageType.APP_VERSION_THREE);
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			if (UserNow.current().userID != 0)
				jsonObject.put("userId", UserNow.current().userID);
			jsonObject.put("content",
					StringUtils.AllStrTOUnicode(fback.content));
			jsonObject.put("contactNumber", fback.contactNum);
			if (fback.programId > 0) {
				jsonObject.put("programId", fback.programId);
			}
			if (fback.programSubId > 0) {
				jsonObject.put("programSubId", fback.programSubId);
			}
			if (fback.channelId > 0) {
				jsonObject.put("channelId", fback.channelId);
			}
			if (!TextUtils.isEmpty(fback.programName)) {
				jsonObject.put("programName",
						StringUtils.AllStrTOUnicode(fback.programName));
			}
			if (!TextUtils.isEmpty(fback.source)) {
				jsonObject.put("phoneType",
						StringUtils.AllStrTOUnicode(fback.source));
			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}

}
