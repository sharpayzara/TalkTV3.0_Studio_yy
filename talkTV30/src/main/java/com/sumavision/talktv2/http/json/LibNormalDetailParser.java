package com.sumavision.talktv2.http.json;

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
 * 片库下每个频道标签下详细数据解析
 * */
public class LibNormalDetailParser extends BaseJsonParser {
	
	public ArrayList<VodProgramData> listProgram = new ArrayList<VodProgramData>();
	public ArrayList<VodProgramData> listSub = new ArrayList<VodProgramData>();
	public ArrayList<RecommendModeData> recLabs = new ArrayList<RecommendModeData>();
    public ArrayList<RecommendData> recommendDatas = new ArrayList<RecommendData>();

	public int subCount;
	public boolean hasJvxiaoAd;
	
	@Override
	public void parse(JSONObject jsonObject) {
		listProgram = null;
		listSub = null;
        recLabs = null;
        recommendDatas = null;
		listProgram = new ArrayList<VodProgramData>();
		listSub = new ArrayList<VodProgramData>();
        recLabs = new ArrayList<RecommendModeData>();
        recommendDatas = new ArrayList<RecommendData>();
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
					subCount = content.optInt("count");
					if (content.has("columnVideo")) {
						JSONArray columnVideo = content.optJSONArray("columnVideo");
						if (columnVideo != null && columnVideo.length() > 0) {
							for (int i = 0; i < columnVideo.length(); i++) {
								VodProgramData v = new VodProgramData();
								JSONObject obj = columnVideo.getJSONObject(i);
								v.id = obj.optLong("id") + "";
								v.ptype = obj.optInt("pType");
								v.name = obj.optString("name", "");
								v.title = obj.optString("title", "");
								v.shortIntro = obj.optString("shortIntro", "");
								v.updateName = obj.optString("updateName", "");
								v.subName = obj.optString("subName","");
								v.playTimes = obj.optInt("playTimes");
								v.pic = obj.optString("pic");
								v.playType = obj.optInt("playType");
								v.pcount = obj.optInt("pcount");
								v.playUrl = obj.optString("playUrl");
								v.playLength = obj.optInt("playLength");
								v.point = obj.optString("doubanPoint", "");
								v.isAd = obj.optInt("jvxiao") == 1? true:false;
								v.adId = obj.optString("adStr");
								if (!hasJvxiaoAd && v.isAd){
									hasJvxiaoAd = true;
								}
								listProgram.add(v);
							}
						}
					}
					
//					if (content.has("programSub")) {
//						JSONArray subProgram = content
//								.optJSONArray("programSub");
//						if (subProgram != null && subProgram.length() > 0) {
//							for (int i = 0; i < subProgram.length(); i++) {
//								VodProgramData v = new VodProgramData();
//								JSONObject obj = subProgram.getJSONObject(i);
//								v.id = obj.optLong("id") + "";
//								v.name = obj.optString("title");
//								v.pic = Constants.picUrlFor + obj.optString("pic") + ".jpg";
//								v.subVideoPath = obj.optString("videoPath");
//								v.playUrl = obj.optString("webUrl");
//								v.playLength = obj.optInt("playLength");
//								listSub.add(v);
//							}
//						}
//					}
			
					if (content.has("program")) {
						JSONArray program = content.optJSONArray("program");
						if (program != null && program.length() > 0) {
							for (int i = 0; i < program.length(); i++) {
								JSONObject obj1 = program.optJSONObject(i);
								if (obj1 == null) {
									continue;
								}
								JSONArray programSub = obj1.optJSONArray("programSub");
								if (programSub != null && programSub.length() > 0) {
									for (int j = 0; j < programSub.length(); j++) {
										JSONObject obj = programSub.optJSONObject(j);
										if (obj == null) {
											continue;
										}
										VodProgramData v = new VodProgramData();
										v.id = obj.optLong("id") + "";
										v.name = obj.optString("title");
										v.pic = obj.optString("pic");
										v.subVideoPath = obj.optString("videoPath");
										v.playUrl = obj.optString("webUrl");
										v.playLength = obj.optInt("playLength");
										v.programId = obj.optLong("progId")+"";
										listSub.add(v);
									}
								}
							}
						}
					}
                    if (content.has("recommend")){
                        JSONArray recPicArray = content.optJSONArray("recommend");
                        if (recPicArray != null && recPicArray.length()>0){
                            for (int i=0; i<recPicArray.length(); i++){
                                RecommendData r = new RecommendData();
                                JSONObject obj = recPicArray.getJSONObject(i);
                                r.id = obj.optLong("objectId");
                                r.otherId = obj.optLong("otherId");
                                r.type = obj.optInt("type");
                                r.name = obj.optString("name");
                                r.pic = Constants.picUrlFor
                                        + obj.optString("pic")
                                        + Constants.PIC_SUFF;
                                r.url = obj.optString("url");
                                r.videoPath = obj.optString("videoPath");
                                recommendDatas.add(r);
                            }
                        }
                    }
					if (content.has("child")){
						JSONArray childArray = content.optJSONArray("child");
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
                                JSONArray temp = child.optJSONArray("program");
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
										vod.point = vodObj.optString("doubanPoint", "");
										vod.isAd = vodObj.optInt("jvxiao") == 1 ?true:false;
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
