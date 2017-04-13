package com.sumavision.talktv2.http.json;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 摇一摇解析
 * 
 * @author cx
 * @version
 * @description
 */
public class ShakeProgramParser extends BaseJsonParser {
	public String scrollText;
	public int point;
	public int historyId;
	
	public ArrayList<ProgramData> listProgram;
	
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", jsonObject.toString());
		listProgram = new ArrayList<ProgramData>();
		try {
			if (jsonObject.has("code")) {
				errCode = jsonObject.getInt("code");
			} else if (jsonObject.has("errcode")) {
				errCode = jsonObject.getInt("errcode");
			}
			if (jsonObject.has("jsession")) {
				UserNow.current().jsession = jsonObject.getString("jsession");
			}
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = jsonObject.getJSONObject("content");
				if (content != null) {
					scrollText = content.optString("rollStr");
					point = content.optInt("point");
					historyId = (int) content.optLong("historyId");
					JSONArray programs = content.optJSONArray("program");
					if (programs != null) {
						for (int i = 0; i < programs.length(); i++) {
							JSONObject program = programs.optJSONObject(i);
							ProgramData programData = new ProgramData();
							programData.programId = program.optLong("progId");
							programData.name = program.optString("name");
							programData.userName = program.optString("userName");
							programData.userPic = Constants.picUrlFor + program.optString("userPic") + Constants.PIC_SUFF;;
							programData.pic = Constants.picUrlFor + program.optString("pic") + Constants.PIC_SUFF;
							//点赞数量
							programData.signCount = program.optInt("evaluateCount");
							programData.cpId = program.optInt("tvfanProgId");//用作普通半屏节目id
							programData.topicId = program.optInt("tvfanProgSubId");//用作子节目id
							programData.isVip = program.optInt("isVip")==1 ? true:false;
							listProgram.add(programData);
						}
					}
				}
			} else {
				errMsg = jsonObject.optString("msg");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
