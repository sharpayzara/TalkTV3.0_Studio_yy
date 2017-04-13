package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 互动活动详情json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveActivityDetailParser extends BaseJsonParser {

	private InteractiveActivity mInteractiveActivity;

	public InteractiveActivityDetailParser(
			InteractiveActivity mInteractiveActivity) {
		this.mInteractiveActivity = mInteractiveActivity;
	}

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				mInteractiveActivity.userSupport = content.optInt(
						"userSupport", 0);
				JSONObject interactiveObj = content.getJSONObject("activity");
				mInteractiveActivity.leftSupportCount = interactiveObj.optInt(
						"leftSupportQty", 0);
				mInteractiveActivity.rightSupportCount = interactiveObj.optInt(
						"rightSupportQty", 0);
				mInteractiveActivity.leftPoint = interactiveObj.optInt(
						"leftPoint", 0);
				mInteractiveActivity.rightPoint = interactiveObj.optInt(
						"rightPoint", 0);
				mInteractiveActivity.endTime = interactiveObj.optString(
						"endTime", "");
				mInteractiveActivity.currentTime = interactiveObj.optString(
						"currentTime", "");
				String pic = interactiveObj.optString("programPic", "");
				if (pic.length() > 0) {
					mInteractiveActivity.programPhoto = Constants.picUrlFor
							+ pic + Constants.PIC_BIG;
				}
				mInteractiveActivity.programIntro = interactiveObj.optString(
						"programIntro", "");
			}
		} catch (JSONException e) {
			Log.e("interactActivityDetail-parse", e.toString());
		}

	}

}
