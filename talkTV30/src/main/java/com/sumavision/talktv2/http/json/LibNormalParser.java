package com.sumavision.talktv2.http.json;

import android.util.Log;

import com.sumavision.talktv2.bean.ContentType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.RecommendModeData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 片库下每个频道精选页和筛选页数据解析
 * */
public class LibNormalParser extends BaseJsonParser {

	/**
	 * 根据request的值解析不同的数据 request = 0 表示是取标签 request = 1表示节目页精选数据 request =
	 * 2表示子节目页精选数据
	 * */
	public int request;

	public LibNormalParser(int request) {
		this.request = request;
	}

	public ArrayList<RecommendData> listRecommend;
	public ArrayList<ContentType> listContent;
	public ArrayList<VodProgramData> listProgram;
	public ArrayList<VodProgramData> listProgramSub;
	public ArrayList<RecommendModeData> recLabs;

	public boolean hasFilter;
	public int videoType;
	public int columnProgramType;
	public boolean hasJvxiaoAd;

	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		listRecommend = null;
		listContent = null;
		listProgram = null;
		listProgramSub = null;
		recLabs = null;
		listRecommend = new ArrayList<RecommendData>();
		listContent = new ArrayList<ContentType>();
		listProgram = new ArrayList<VodProgramData>();
		listProgramSub = new ArrayList<VodProgramData>();
		recLabs= new ArrayList<RecommendModeData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.optInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.optInt("errcode");
			} else if (jsonObject.has("errorCode")) {
				errCode = jsonObject.optInt("errorCode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				if (jsonObject.has("content")) {
					JSONObject content = jsonObject.optJSONObject("content");
					int filter = content.optInt("hasFilter", 0);
					if (filter == 0) {
						hasFilter = false;
					} else {
						hasFilter = true;
					}
					videoType = content.optInt("videoType");
					columnProgramType = content.optInt("columnProgramType");
					if (content.has("contentType")) {
						JSONArray contentType = content
								.optJSONArray("contentType");
						if (contentType != null && contentType.length() > 0) {
							for (int i = 0; i < contentType.length(); i++) {
								ContentType c = new ContentType();
								JSONObject obj = contentType.getJSONObject(i);
								c.id = obj.optInt("id");
								c.code = obj.optString("code");
								c.name = obj.optString("name");
								c.sortOrder = obj.optInt("sortOrder");
								c.type = obj.optString("type");
                                c.style = obj.optInt("style");
								listContent.add(c);
							}
						}
					}

					if (content.has("recommend")) {
						try {
						JSONArray recommend = content.optJSONArray("recommend");
						if (recommend != null && recommend.length() > 0) {
							for (int i = 0; i < recommend.length(); i++) {
								RecommendData r = new RecommendData();
								JSONObject obj = recommend.getJSONObject(i);
								r.id = obj.optLong("objectId");
								r.otherId = obj.optLong("otherId");
								r.type = obj.optInt("type");
								r.name = obj.optString("name");
								r.pic = Constants.picUrlFor
										+ obj.optString("pic")
										+ Constants.PIC_SUFF;
								r.url = obj.optString("url");
								r.videoPath = obj.optString("videoPath");
								listRecommend.add(r);
							}
						}
						} catch (Exception e) {}
					}

					if (content.has("program")) {
						JSONArray video = content.optJSONArray("program");
						if (video != null && video.length() > 0) {
							for (int i = 0; i < video.length(); i++) {
								VodProgramData v = new VodProgramData();
								JSONObject obj = video.getJSONObject(i);
								v.id = obj.optLong("id") + "";
								v.name = obj.optString("name", "");
								v.title = obj.optString("title", "");
								v.ptype = obj.optInt("type");
								v.shortIntro = obj.optString("shortIntro", "");
								v.pic = Constants.picUrlFor
										+ obj.optString("pic")
										+ Constants.PIC_SUFF;
								v.pcount = obj.optInt("pcount");
								v.updateName = obj.optString("updateName", "");
								v.point = obj.optString("doubanPoint", "");
								v.programId = obj.optLong("progId")+"";
								listProgram.add(v);
							}
						}
					}

					if (content.has("subProgram")) {
						JSONArray video = content.optJSONArray("subProgram");
						if (video != null && video.length() > 0) {
							for (int i = 0; i < video.length(); i++) {
								VodProgramData v = new VodProgramData();
								JSONObject obj = video.getJSONObject(i);
								v.id = obj.optLong("id") + "";
								v.programId = obj.optLong("progId")+"";
								v.name = obj.optString("name");
								v.title = obj.optString("title");
								v.shortIntro = obj.optString("shortIntro");
								v.pic = Constants.picUrlFor
										+ obj.optString("pic") + Constants.PIC_SUFF;
								v.playUrl = obj.optString("webUrl");
								v.subVideoPath = obj.optString("videoPath");
								v.playLength = obj.optInt("totalSecond");
								listProgramSub.add(v);
							}
						}
					}
					if (content.has("recommendChild")){
						JSONArray childArray = content.optJSONArray("recommendChild");
						if (childArray != null && childArray.length()>0){
							for (int i=0; i<childArray.length();i++){
								JSONObject child = childArray.optJSONObject(i);
								RecommendModeData recLab = new RecommendModeData();
								recLab.id = child.optInt("id");
								recLab.type = child.optInt("type");
								recLab.name = child.optString("name");
								recLab.objectId = child.optLong("objectId");
								recLab.objectType = child.optInt("objectType");
								recLab.picWide = child.optString("picWide");
								JSONArray temp = child.optJSONArray("hotLabelProgram");
								if (temp != null && temp.length()>0){
									for (int j=0; j<temp.length(); j++){
										JSONObject vodObj = temp.optJSONObject(j);
										VodProgramData vod = new VodProgramData();
										vod.id = vodObj.optLong("id") + "";
										vod.name = vodObj.optString("name");
										vod.shortIntro = vodObj.optString("shortIntro");
										vod.subName = vodObj.optString("subName");
										vod.pic = vodObj.optString("pic");
										vod.updateName = vodObj.optString("updateName");
										vod.playTimes = vodObj.optInt("playTimes");
										vod.point =  vodObj.optString("doubanPoint", "");
										vod.isAd = vodObj.optInt("jvxiao") == 1 ? true:false;
										vod.adId = vodObj.optString("adStr");
										if (!hasJvxiaoAd && vod.isAd){
											hasJvxiaoAd = true;
										}
										recLab.hotLabelPrograms.add(vod);
									}
								}
								recLabs.add(recLab);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
