package com.sumavision.talktv2.http.json;

import android.text.TextUtils;

import com.sumavision.talktv2.bean.AppData;
import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.RecommendCommonData;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.RecommendPageNewData;
import com.sumavision.talktv2.bean.RecommendTag;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author
 * @version 3.0
 * @description 推荐解析类
 */
public class RecommendDetailParser extends BaseJsonParser {
	public RecommendPageNewData recommendData;
	public String recommendDataStr;
	public int activityId;

	public RecommendDetailParser() {
		recommendData = new RecommendPageNewData();
	}

	@Override
	public void parse(JSONObject jAData) {
		try {
			errCode = jAData.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (jAData.has("jsession")) {
				UserNow.current().jsession = jAData.optString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				recommendDataStr = jAData.toString();
				JSONObject content = jAData.optJSONObject("content");
				activityId = (int) content.optLong("activityId");
				Constants.NEEDAVOID_LIVE = content.optInt("skipWeb") == 1;
				// 推荐页面顶部焦点图
				JSONArray recommend = content.optJSONArray("recommend");
				List<RecommendData> lr = new ArrayList<RecommendData>();
				for (int i = 0; i < recommend.length(); ++i) {
					RecommendData r = new RecommendData();
					JSONObject data = recommend.optJSONObject(i);
					r.type = data.optInt("type");
					if (r.type <= 0) {
						continue;
					}
					if (r.type == RecommendData.TYPE_APP_RECOMMEND) {
						r.appName = data.optString("appName");
						r.name = data.optString("name");
						r.pic = data.optString("pic");
						r.url = data.optString("appUrl");
						r.identifyName = data.optString("identifyName");
					} else {
						r.name = data.optString("name");
						r.pic = data.optString("pic");
						r.url = data.optString("url");
					}
					r.id = data.optLong("objectId");
					r.videoPath = data.optString("videoPath");
					r.otherId = data.optLong("otherId");
                    if (data.has("play")){
                        JSONArray playArray = data.optJSONArray("play");
                        if (playArray != null && playArray.length()>0){
                            r.liveUrls = new ArrayList<NetPlayData>();
                            for (int a=0; a<playArray.length(); a++){
                                NetPlayData temp = new NetPlayData();
                                JSONObject obj = playArray.optJSONObject(a);
                                temp.url = obj.optString("url");
                                temp.videoPath = obj.optString("videoPath");
                                temp.channelIdStr = obj.optString("channelIdStr");
                                temp.showUrl = obj.optString("showUrl");
                                temp.platformId = obj.optInt("platformId");
								temp.webPage = obj.optString("webPage");
								temp.id = obj.optInt("id");
                                r.liveUrls.add(temp);
                            }
                        }
                    }
					lr.add(r);
				}
				recommendData.setRecommend(lr);
				recommendData.activityPicType = content.optInt(
						"activityPicType", -1);

				// 推荐页面电影，电视剧，新闻等节目列表
				if (content.has("columns")) {
					List<ColumnData> columnDatas = new ArrayList<ColumnData>();
					JSONArray columns = content.optJSONArray("columns");
					for (int i = 0; i < columns.length(); i++) {
						JSONObject jcd = columns.optJSONObject(i);
						ColumnData cd = new ColumnData();

						cd.id = jcd.optInt("id");
						cd.name = jcd.optString("name");
						cd.picType = jcd.optInt("picType");
						cd.type = jcd.optInt("type");
						cd.icon = jcd.optString("icon");
						if (!TextUtils.isEmpty(cd.icon)) {
							cd.icon = Constants.picUrlFor + cd.icon
									+ Constants.PIC_SUFF;
						}
						cd.parameter = jcd.optInt("parameter");
						cd.appName = jcd.optString("appName");
						cd.downloadUrl = jcd.optString("downloadUrl");
						cd.identifyName = jcd.optString("identifyName");
						cd.showPic = jcd.optInt("showPic");
						cd.parentId = jcd.optLong("parentId");
						cd.contentTypeId = jcd.optInt("contentTypeId");
						cd.programTypeId = jcd.optInt("programTypeId");
						if (jcd.has("vaultColumns")) {
							JSONArray vault = jcd.optJSONArray("vaultColumns");
							ArrayList<HotLibType> vaultColumns = new ArrayList<HotLibType>();
							for (int iVault = 0; iVault < vault.length(); iVault++) {
								HotLibType h = new HotLibType();
								JSONObject data = vault.optJSONObject(iVault);
								h.id = (long) data.optInt("id");
								h.name = data.optString("name");
								h.icon = Constants.picUrlFor
										+ data.optString("pic") + ".jpg";
								h.type = data.optInt("type");
								h.programType = data.optInt("programTypeId");
								vaultColumns.add(h);
							}
							cd.setVault(vaultColumns);
						}
						if (jcd.has("program")) {
							JSONArray programs = jcd.optJSONArray("program");
							List<RecommendCommonData> recommendCommonDatas = new ArrayList<RecommendCommonData>();
							for (int iProgram = 0; iProgram < programs.length(); iProgram++) {
								RecommendCommonData commonData = new RecommendCommonData();
								JSONObject data = programs
										.getJSONObject(iProgram);

								commonData.id = data.optInt("id");
								commonData.title = data.optString("title");
								if (ColumnData.TYPE_DIRECTLY_PLAY == cd.type
										|| cd.type == 20) {
									commonData.title = data
											.optString("shortIntro");
								}
								commonData.name = data.optString("name");
								commonData.evaluate = data.optInt("evaluate");
								commonData.userName = data.optString("userName");
								commonData.userPic = data.optString("userPic");
								commonData.type = data.optInt("type");
								commonData.pType = data.optInt("ptype");
								commonData.intro = data.optString("intro");
								commonData.pic = data.optString("pic");
								commonData.tempId = (int)data.optLong("programId");
								commonData.subId = data.optInt("subId");
								int sub = data.optInt("subject");
								commonData.subject = sub == 1;
								commonData.icon = data.optString("icon");
								commonData.webUrl = data.optString("webUrl");
								commonData.videoPath = data
										.optString("videoPath");
								commonData.videoPathHigh = data
										.optString("videoPathHigh");
								commonData.videoPathSuper = data
										.optString("videoPathSuper");
								commonData.columnPicType = cd.picType;
								commonData.columnType = cd.type;
								commonData.isAd = data.optString("jvxiao").equals("1")?true:false;
								commonData.adId = data.optString("adStr");
//								if (commonData.isAd){
//									commonData.adId = getRandomId();
//								}
								if (cd.type == 21) {
									commonData.id = (int) data.optLong("progId");
									commonData.intro = commonData.userName;
									commonData.type = cd.type;//摇一摇标示
									commonData.tvfanProgId = data.optInt("tvfanProgId",0);
									commonData.tvfanProgSubId = data.optInt("tvfanProgSubId",0);
								}
								commonData.skipToWeb = data.optInt("skipWeb",0) == 0 ? false:true;
								if (data.has("play")) {
									JSONArray netPlayAddrList = data
											.getJSONArray("play");
									ArrayList<NetPlayData> netPlayDatas = new ArrayList<NetPlayData>();
									for (int iplay = 0; iplay < netPlayAddrList
											.length(); iplay++) {

										JSONObject play = netPlayAddrList
												.optJSONObject(iplay);
										NetPlayData npd = new NetPlayData();
										npd.platformId = play
												.optInt("platformId");
										npd.name = play.optString("name");
										npd.url = play.optString("url");
										npd.videoPath = play
												.optString("videoPath");
										npd.channelName = commonData.name;
										npd.channelIdStr = play.optString("channelIdStr");
                                        npd.showUrl = play.optString("showUrl");
										npd.webPage = play.optString("webPage");
										npd.id = play.optInt("id");
										netPlayDatas.add(npd);
									}
									commonData.netPlayDatas = netPlayDatas;
								}
								recommendCommonDatas.add(commonData);
							}

							cd.setDatas(recommendCommonDatas);
						}
						if (jcd.has("contentType")){
							JSONArray typeArray = jcd.optJSONArray("contentType");
							cd.recommendTags = new ArrayList<RecommendTag>();
							int len = typeArray.length();
							len = (len >3) ? 3:len;
							for (int j=0; j<len; j++){
								JSONObject temp = typeArray.optJSONObject(j);
								RecommendTag tag = new RecommendTag();
								tag.code = temp.optString("code");
								tag.id = temp.optLong("id");
								tag.name = temp.optString("name");
								tag.sortOrder = temp.optInt("sortOrder");
								tag.type = temp.optInt("type");
								tag.columnId = temp.optLong("columnId");
								tag.columnName = temp.optString("columnName");
								tag.programTypeId = temp.optInt("programTypeId");
								cd.recommendTags.add(tag);
							}
							Collections.reverse(cd.recommendTags);
						}

						columnDatas.add(cd);
					}
					recommendData.setColumn(columnDatas);
				}

				if (content.has("recommendApp")) {
					ArrayList<AppData> apps = new ArrayList<AppData>();
					JSONArray arr = content.optJSONArray("recommendApp");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject obj = arr.optJSONObject(i);
						AppData data = new AppData();
						data.id = obj.optLong("id");
						data.name = obj.optString("name");
						data.shortIntro = obj.optString("shortIntro");
						data.pic = obj.optString("pic");
						if (!TextUtils.isEmpty(data.pic)) {
							data.pic = data.pic + ".png";
						}
						data.url = obj.optString("url");
						data.packageName = obj.optString("identifyName");
						apps.add(data);
					}
					recommendData.setApps(apps);
				}
			}
		} catch (Exception e) {
			errCode = JSONMessageType.SERVER_CODE_ERROR;
			e.printStackTrace();
		}

	}
	int times;
	public String getRandomId(){
		String result = "";
		if (times%2 == 0){
			result = "aPFGPIjeWs";
		} else {
			result = "PF5vad4erj";
		}
		times ++;
		return result;
	}
}
