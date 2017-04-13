package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

/**
 * 预约列表解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RemindListParser extends BaseJsonParser {
	public ArrayList<VodProgramData> remindProgramList = new ArrayList<VodProgramData>();
	public int remindCount;

	public void parse(JSONObject JSONObject) {
		try {
			if (JSONObject.has("code")) {
				errCode = JSONObject.getInt("code");
			} else if (JSONObject.has("errcode")) {
				errCode = JSONObject.getInt("errcode");
			} else if (JSONObject.has("errorCode")) {
				errCode = JSONObject.getInt("errorCode");
			}
			if (JSONObject.has("jsession")) {
				UserNow.current().jsession = JSONObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = JSONObject.getJSONObject("content");
				remindCount = content.optInt("remindCount", 0);
				JSONArray chases = content.getJSONArray("remind");
				for (int i = 0; i < chases.length(); ++i) {
					JSONObject chase = chases.optJSONObject(i);
					VodProgramData u = new VodProgramData();
					u.remindId = chase.optLong("id");
					u.cpId = chase.optLong("cpId");
					u.id = chase.optLong("programId") + "";
					u.pic = chase.optString("programPic");
					u.cpName = chase.optString("cpName");
					u.channelName = chase.optString("channelName");
					u.cpDate = chase.optString("cpDate");
					u.channelId = chase.optString("channelId");
					u.channelLogo = chase.optString("channelIcon");
					if (!TextUtils.isEmpty(u.channelLogo)) {
						u.channelLogo = Constants.picUrlFor + u.channelLogo
								+ ".png";
					}
					u.startTime = chase.optString("cpStartTime");
					u.endTime = chase.optString("cpEndTime");
					remindProgramList.add(u);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
