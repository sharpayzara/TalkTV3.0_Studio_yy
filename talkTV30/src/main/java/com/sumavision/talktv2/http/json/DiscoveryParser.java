package com.sumavision.talktv2.http.json;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 发现页解析
 * 
 * @author cx
 * 
 */
public class DiscoveryParser extends BaseJsonParser {
	public ArrayList<RecommendData> listRecommend;
	public int haveShake;
	public int activityId;
	public String activityName,discoveryName,pic;
	public boolean hasDuanwu,hasLottery,hasInvite,dayLottery;
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		try {
			errCode = jsonObject.optInt("code",
					JSONMessageType.SERVER_CODE_ERROR);
			listRecommend = new ArrayList<RecommendData>();
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null) {
				haveShake = content.optInt("haveShake");
			}
			if (content != null && content.has("recommend")) {
				JSONArray recommend = content.optJSONArray("recommend");
				for (int i = 0; i < recommend.length(); i++) {
					RecommendData r = new RecommendData();
					JSONObject data = recommend.optJSONObject(i);
					r.id = data.optLong("objectId");
					r.name = data.optString("name");
					r.pic = Constants.picUrlFor + data.optString("pic") + Constants.PIC_SUFF;
					r.type = data.optInt("type");
					r.url = data.optString("url");
					r.otherId = data.optLong("otherId");
					r.videoPath = data.optString("videoPath");
					listRecommend.add(r);
				}
			}
			JSONObject activityObj = content.optJSONObject("activity");
			if (activityObj != null){
				hasDuanwu = true;
				activityId = activityObj.optInt("activityId");
				activityName = activityObj.optString("activityName");
				discoveryName = activityObj.optString("discoveryName");
				pic = activityObj.optString("pic");
			}else {
				hasDuanwu = false;
			}
			if (content.optString("lottery").equals("1")){
				hasLottery = true;
			} else {
				hasLottery = false;
			}
			if (content.optString("invite").equals("1")){
				hasInvite = true;
			} else {
				hasInvite = false;
			}
			if (content.optString("dayLottery").equals("1")){
				dayLottery = true;
			} else {
				dayLottery = false;
			}
			if (dayLottery){
				UserNow.current().dayLoterry = 1;
			} else {
				UserNow.current().dayLoterry = 0;
			}

		} catch (Exception e) {
			errCode = JSONMessageType.SERVER_CODE_ERROR;
			e.printStackTrace();
		}
	}

}
