package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;

/**
 * 某视频源视频列表解析
 * 
 * @author suma-hpb
 * 
 */
public class PlatVideoParser extends BaseJsonParser {
	public ArrayList<JiShuData> subList = new ArrayList<JiShuData>();

	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			subList.clear();
			JSONObject content = jsonObject.optJSONObject("content");
			JSONArray subArr = content.optJSONArray("programSub");
			if (subArr != null && subArr.length() > 0) {
				int sublen = subArr.length();
				for (int sindex = 0; sindex < sublen; sindex++) {
					JSONObject subObj = subArr.optJSONObject(sindex);
					JiShuData jishu = new JiShuData();
					jishu.id = subObj.optInt("id");
					jishu.name = subObj.optString("name");
					jishu.url = subObj.optString("url");
					jishu.videoPath = subObj.optString("videoPath");
					jishu.hVideoPath = subObj.optString("videoPathHigh");
					jishu.superVideoPath = subObj.optString("videoPathSuper");
					jishu.topicId = (int) subObj.optLong("topicId");
					subList.add(jishu);
				}
			}
		}

	}

}
