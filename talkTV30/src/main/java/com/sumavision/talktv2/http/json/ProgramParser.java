package com.sumavision.talktv2.http.json;

import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目页(3.0)解析
 * 
 * @author suma-hpb
 * 
 */
public class ProgramParser extends BaseJsonParser {

	public ProgramData program;
	public int skipWeb;
	public int advertise;
	public int recommendCount;
	public String recVersion;//智能推荐版本号
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", "program parser:"+jsonObject.toString());
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			program = new ProgramData();
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null) {
				String progAd = content.optString("progAd");
				if (!TextUtils.isEmpty(progAd)) {
					advertise = Integer.parseInt(progAd);
				} else {
					advertise = 0;
				}
				
				JSONArray recommend = content.optJSONArray("recommend");
				program.recommendNumber = content.optInt("recommendCount");
				program.recommendPrograms = new ArrayList<VodProgramData>();
				recVersion = content.optString("recVersion");
				if (recommend != null) {
					for (int i = 0; i < recommend.length(); i++) {
						JSONObject recommendObj = recommend.optJSONObject(i);
						VodProgramData vdata = new VodProgramData();
						vdata.id = recommendObj.optInt("id") + "";
						vdata.name = recommendObj.optString("name");
						vdata.pic = recommendObj.optString("pic");
						program.recommendPrograms.add(vdata);
					}
				}
				
				JSONObject programObj = content.optJSONObject("program");
				if (programObj != null) {
					program.programId = programObj.optLong("id");
					program.topicId = programObj.optLong("topicId");
					program.pic = Constants.picUrlFor
							+ programObj.optString("pic") + Constants.PIC_SUFF;
					program.name = programObj.optString("name");
					program.director = programObj.optString("director");
					program.actors = programObj.optString("stager");
					program.region = programObj.optString("area");
					program.doubanPoint = programObj.optDouble("doubanPoint");
					program.pType = programObj.optInt("pType");
					program.time = programObj.optString("playYear");
					program.detail = programObj.optString("intro");
					program.update = programObj.optString("updateInfo");
					program.contentType = programObj.optString("contentType");
					program.skipWeb = programObj.optInt("skipWeb");
					skipWeb = programObj.optInt("skipWeb");
				}
				program.isZan = content.optInt("isPraise") == 1 ? true : false;
				program.isChased = content.optInt("isChased") == 1 ? true
						: false;
				program.isSigned = content.optInt("isSigned") == 1 ? true
						: false;
				program.evaluateCount = content.optInt("programEvaluateSum");
				program.subOrderType = content.optInt("subOrderType");

				JSONArray platArr = content.optJSONArray("platform");
				if (platArr != null && platArr.length() > 0) {
					ArrayList<SourcePlatform> platList = new ArrayList<SourcePlatform>();
					int len = platArr.length();
					for (int index = 0; index < len; index++) {
						SourcePlatform sp = new SourcePlatform();
						JSONObject pObj = platArr.optJSONObject(index);
						sp.id = pObj.optInt("id");
						sp.name = pObj.optString("name");
						sp.pic = pObj.optString("pic");
						sp.isNative = pObj.optInt("waShu") == 1 ? false:true;
						if (!sp.pic.startsWith("http")) {
							sp.pic = Constants.picUrlFor + sp.pic
									+ Constants.PIC_SUFF;
						}
						platList.add(sp);
					}
					program.platformList = platList;
				}

				//剧集相关信息
				program.subCount = content.optInt("subCount");
				program.subId = content.optInt("subId");
				program.stage = content.optInt("stage") -1;
				program.order = content.optInt("order");
//				if (program.pType == 1 || program.pType == 11){
//				}else {
//					program.order = 0;
//				}
				program.detailType = content.optInt("detailType");
				program.subType = content.optInt("subType");
				JSONArray stageTagArray = content.optJSONArray("stageTag");
				if (stageTagArray != null && stageTagArray.length()>0){
					for (int i=0; i<stageTagArray.length(); i++){
						program.stageTag.add(stageTagArray.optJSONObject(i).optString("tag"));
					}
				}else{
					program.stageTag.clear();
				}
				JSONArray subArr = content.optJSONArray("programSub");
				if (subArr != null && subArr.length() > 0) {
					ArrayList<JiShuData> subList = new ArrayList<JiShuData>();
					int sublen = subArr.length();
					for (int sindex = 0; sindex < sublen; sindex++) {
						JSONObject subObj = subArr.optJSONObject(sindex);
						JiShuData jishu = new JiShuData();
						jishu.id = subObj.optInt("id");
						jishu.name = subObj.optString("name");
						jishu.url = subObj.optString("url");
						jishu.topicId = (int) subObj.optLong("topicId");
						jishu.videoPath = subObj.optString("videoPath");
						jishu.hVideoPath = subObj.optString("videoPathHigh");
						jishu.superVideoPath = subObj
								.optString("videoPathSuper");
						subList.add(jishu);
					}
					program.platformList.get(0).jishuList = subList;
				}
				
				JSONArray comment = content.optJSONArray("talk");
				program.talkCount = content.optInt("talkCount");
				if (comment != null && comment.length() > 0) {
					ArrayList<CommentData> commentData = new ArrayList<CommentData>();
					for (int i = 0; i < comment.length(); i++) {
						CommentData c = new CommentData();
						JSONObject obj = comment.optJSONObject(i);
						c.talkId = (int) obj.optLong("id");
						c.content = obj.optString("content");
						c.actionType = obj.optInt("actionType");
						c.time = obj.optString("displayTime");
						c.pic = obj.optString("userPic");
						c.userName = obj.optString("userName");
						c.replyCount = obj.optInt("replyCount");
						c.rootId = (int) obj.optLong("rootId");
						c.userId = (int) obj.optLong("userId");
						c.isVip = obj.optInt("isVip") ==1 ? true:false;

						JSONArray forward = obj.optJSONArray("forward");
						List<CommentData> forwardData = new ArrayList<CommentData>();
						if (forward != null && forward.length() > 0) {
							for (int j = 0; j < forward.length(); j++) {
								CommentData f = new CommentData();
								JSONObject fobj = forward.optJSONObject(j);
								f.forwardId = fobj.optInt("Id");
								f.userName = fobj.optString("userNname");
								f.content = fobj.optString("content");
								forwardData.add(f);
							}
							c.forward = new ArrayList<CommentData>();
							c.forward = forwardData;
						}
						JSONArray reply = obj.optJSONArray("reply");
						List<CommentData> replyData = new ArrayList<CommentData>();
						if (reply != null && reply.length() > 0) {
							for (int k = 0; k < reply.length(); k++) {
								CommentData r = new CommentData();
								JSONObject robj = reply.optJSONObject(k);
								r.userName = robj.optString("username");
								r.content = robj.optString("content");
								replyData.add(r);
							}
							c.reply = new ArrayList<CommentData>();
							c.reply = replyData;
						}
						
						commentData.add(c);
					}
					program.comment = commentData;
				}
			}
		}

	}
}
