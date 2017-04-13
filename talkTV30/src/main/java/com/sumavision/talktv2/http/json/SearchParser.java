package com.sumavision.talktv2.http.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 搜索节目解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchParser extends BaseJsonParser {
	public ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();
	public int totalCount;

	@Override
	public void parse(JSONObject jsonObject) {
		try {
			programList.clear();
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.getInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.getJSONObject("content");
					totalCount = content.optInt("programCount", 0);
					if (content.has("program")) {
						JSONArray plays = content.getJSONArray("program");
						for (int i = 0; i < plays.length(); i++) {
							JSONObject o = plays.getJSONObject(i);
							VodProgramData vpd = new VodProgramData();
							vpd.name = o.optString("name");
							vpd.id = o.optString("id");
							vpd.ptype = o.optInt("pType");
							vpd.topicId = o.optString("topicId");
							vpd.pic = o.optString("pic");
							vpd.subSearchType = 0;
							programList.add(vpd);
						}
						if (plays != null && plays.length() > 0
								&& plays.length() >= 10) {
							VodProgramData moreMovie = new VodProgramData();
							moreMovie.subSearchType = 0;
							moreMovie.name = "点击查看更多影片";
							programList.add(moreMovie);
						}
					}
					if (content.has("sub")) {
						JSONArray subs = content.optJSONArray("sub");
						if (subs != null) {
							for (int j = 0; j < subs.length(); j++) {
								JSONObject sub = subs.optJSONObject(j);
								VodProgramData vpd = new VodProgramData();
								vpd.subId = sub.optString("id");
								vpd.subProgramId = sub.optString("programId");
								vpd.subName = sub.optString("name");
								vpd.name = sub.optString("programName");
								vpd.subUrl = sub.optString("url");
								vpd.subVideoPath = sub.optString("video");
								vpd.subHighPath = sub
										.optString("videoPathHigh");
								vpd.subSuperPath = sub
										.optString("videoPathSuper");
								vpd.subSearchType = 1;
								programList.add(vpd);
							}
							if (subs.length() > 0 && subs.length() >= 10) {
								VodProgramData moreMovie = new VodProgramData();
								moreMovie.subSearchType = 1;
								moreMovie.name = "点击查看更多视频";
								programList.add(moreMovie);
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
