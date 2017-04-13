package com.sumavision.talktv2.http.json;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ShakeProgramData;
import com.sumavision.talktv2.bean.ShakeSub;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 摇一摇节目页(3.1.0)解析
 *
 * @author suma-hpb
 *
 */
public class ShakeProgramDetailParser extends BaseJsonParser {

	public ShakeProgramData program;
	@Override
	public void parse(JSONObject jsonObject) {
		Log.i("mylog", "program parser:"+jsonObject.toString());
		errCode = jsonObject.optInt("code");
		errMsg = jsonObject.optString("msg");
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			program = new ShakeProgramData();
			JSONObject content = jsonObject.optJSONObject("content");
			if (content != null) {
				JSONObject info = content.optJSONObject("program");
				if(info != null){
					program.id = info.optLong("id");
					program.name = info.optString("name");
					program.pic = info.optString("pic");
					program.area = info.optString("area");
					program.director = info.optString("director");
					program.intro = info.optString("intro");
					program.programEvaluateSum = info.optInt("programEvaluateSum");
					program.isZan = info.optInt("evaluate") == 1 ? true:false;
					program.hasReported = info.optInt("inform") == 1 ? true:false;
					program.progPic = info.optString("progPic");
				}
				JSONArray array = content.optJSONArray("programSub");
				if(array != null && array.length()>0){
					JSONObject temp;
					for(int i=0;i<array.length();i++){
						ShakeSub subItem = new ShakeSub();
						temp = array.optJSONObject(i);
						if(temp != null){
							subItem.id = temp.optInt("id");
							subItem.name = temp.optString("name");
							subItem.url = temp.optString("url");
						}
						program.programSub.add(subItem);
					}
				}
			}
		}
	}
}
