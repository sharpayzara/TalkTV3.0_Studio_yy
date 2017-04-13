package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.utils.Constants;

/**
 * 徽章抽奖详情页解析
 * 
 * @author suma-hpb
 * 
 */
public class BadgeLotteryDetailParser extends BaseJsonParser {

	public ActivityData activityData;
	public ArrayList<String> picTitles;

	@Override
	public void parse(JSONObject jsonObject) {
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		picTitles = new ArrayList<String>();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			activityData = new ActivityData();
			JSONObject content = jsonObject.optJSONObject("content");
			JSONObject loObj = content.optJSONObject("badgeLotteryActivity");
			activityData.activityId = loObj.optLong("id");
			activityData.activityName = loObj.optString("name");
			activityData.totalTimes = loObj.optInt("totalTimes");
			activityData.joinedTimes = loObj.optInt("shakeCount");
			activityData.taskIntro = loObj.optString("intro");
			activityData.state = loObj.optInt("status");
			activityData.announcement = loObj.optString("notice");
			activityData.videoPath = content.optString("videoPath");
			activityData.joinStatus = content.optInt("joinStatus");
			JSONArray picArr = loObj.optJSONArray("pic");
			if (picArr != null && picArr.length() > 0) {
				ArrayList<String> picList = new ArrayList<String>();
				int len = picArr.length();
				for (int i = 0; i < len; i++) {
					JSONObject singlePicObg = picArr.optJSONObject(i);
					picList.add(Constants.picUrlFor
							+ singlePicObg.optString("picPath")
							+ Constants.PIC_BIG);
					picTitles.add(singlePicObg.optString("intro"));
				}
				if (picList.size() > 0) {
					activityData.activityPic = picList.get(0);
					activityData.playPic = picList.get(0);
				}
				activityData.pics = picList;
			}
			JSONArray userArr = content.optJSONArray("resultActivityGoodsUser");
			if (userArr != null && userArr.length() > 0) {
				ArrayList<ReceiverInfo> userList = new ArrayList<ReceiverInfo>();
				int len = userArr.length();
				for (int i = 0; i < len; i++) {
					JSONObject obj = userArr.optJSONObject(i);
					ReceiverInfo info = new ReceiverInfo();
					info.name = obj.optString("userName");
					if (!TextUtils.isEmpty(info.name)) {
						info.remark = obj.optString("rewardLevel");
						userList.add(info);
					}
				}
				activityData.receiverList = userList;
			}
		}

	}
}
