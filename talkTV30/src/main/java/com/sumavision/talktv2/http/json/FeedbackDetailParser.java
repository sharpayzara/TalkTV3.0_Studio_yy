package com.sumavision.talktv2.http.json;

import com.sumavision.talktv2.bean.FeedbackQuestionData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.UserNow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 反馈数据解析
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class FeedbackDetailParser extends BaseJsonParser {

	public List<FeedbackQuestionData> fqs = new ArrayList<FeedbackQuestionData>();
	public ArrayList<RecommendData> listRecommend = new ArrayList<RecommendData>();

	@Override
	public void parse(JSONObject jsonObject) {
		try {
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
			if (errCode != JSONMessageType.SERVER_CODE_OK) {
				return;
			}
			JSONObject content = jsonObject.optJSONObject("content");
			if(content != null){
				JSONArray recommendArray = content.optJSONArray("recommend");
				if (recommendArray != null && recommendArray.length()>0){
					JSONObject objTemp;
					for (int i=0; i<recommendArray.length();i++){
						objTemp = recommendArray.optJSONObject(i);
						RecommendData tempData = new RecommendData();
						tempData.id = objTemp.optInt("id");
						tempData.type = objTemp.optInt("type");
						tempData.name = objTemp.optString("name");
						tempData.pic = objTemp.optString("pic");
						tempData.url = objTemp.optString("url");
						listRecommend.add(tempData);
					}
				}
				JSONArray qArray = content.optJSONArray("question");
				if (qArray != null && qArray.length()>0){
					JSONObject qTemp;
					for (int i=0;i<qArray.length();i++){
						qTemp = qArray.optJSONObject(i);
						FeedbackQuestionData tempData = new FeedbackQuestionData();
						tempData.id = qTemp.optInt("id");
						tempData.question = qTemp.optString("question");
						tempData.answer = qTemp.optString("answer");
						fqs.add(tempData);
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
