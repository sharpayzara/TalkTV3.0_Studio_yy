package com.sumavision.talktv2.http.json;

import android.content.Context;
import android.os.Build;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.service.AppUpdateService;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @version 3.1.3
 * @description 反馈，发送反馈内容
 */
public class FeedbackAddRequest extends BaseJsonRequest {

	private int feedType;
	String content;
	String pic;
	Context context;

	public FeedbackAddRequest(Context context,int feedType, String content) {
		this.feedType = feedType;
		this.content = content;
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject holder = new JSONObject();
		try {
			holder.put("method", Constants.feedbackAdd);
			holder.put("version", JSONMessageType.APP_VERSION_313);
			holder.put("client", JSONMessageType.SOURCE);
			if (UserNow.current().userID>0){
				holder.put("userId", UserNow.current().userID);
			}
			holder.put("feedType",feedType);
			holder.put("imei", AppUtil.getImei(context));
			holder.put("content", content);
			holder.put("clientVersion", CommonUtils.getAppVersionName(context));
			holder.put("phoneType", Build.MODEL+"_"+Build.VERSION.RELEASE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return holder;
	}
}
