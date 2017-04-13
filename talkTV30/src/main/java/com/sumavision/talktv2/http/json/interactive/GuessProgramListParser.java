package com.sumavision.talktv2.http.json.interactive;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.utils.Constants;

/**
 * 竞猜节目列表json解析
 * 
 * @author suma-hpb
 * 
 */
public class GuessProgramListParser extends BaseJsonParser {

	public ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONArray liveProgramArr = content.optJSONArray("channel");
				if (liveProgramArr != null && liveProgramArr.length() > 0) {
					int size = liveProgramArr.length();
					for (int index = 0; index < size; index++) {
						VodProgramData program = new VodProgramData();
						JSONObject pObj = liveProgramArr.getJSONObject(index);
						program.playType = 1;
						program.channelName = pObj.optString("name", "");
						program.pic = Constants.picUrlFor
								+ pObj.optString("pic", "")
								+ Constants.PIC_SMALL;
						program.cpName = pObj.optString("cpName", "");
						program.startTime = pObj.optString("startTime", "");
						program.endTime = pObj.optString("endTime", "");
						NetPlayData playInfo = new NetPlayData();
						playInfo.url = pObj.optString("webUrl", "");
						playInfo.videoPath = pObj.optString("videoPath", "");
						ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
						playList.add(playInfo);
						program.netPlayDatas = playList;
						programList.add(program);
					}
				}
				JSONArray vodProgramArr = content.optJSONArray("program");
				if (vodProgramArr != null && vodProgramArr.length() > 0) {
					int size = vodProgramArr.length();
					for (int index = 0; index < size; index++) {
						VodProgramData program = new VodProgramData();
						JSONObject pObj = vodProgramArr.getJSONObject(index);
						program.id = pObj.optString("id", "");
						program.name = pObj.optString("name", "");
						program.topicId = pObj.optString("topicId", "");
						program.updateName = pObj.optString("updateName", "");
						program.shortIntro = pObj.optString("shortIntro", "");
						program.playTimes = pObj.optInt("playTimes", 0);
						program.pic = Constants.picUrlFor
								+ pObj.optString("pic", "")
								+ Constants.PIC_SMALL;
						programList.add(program);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
