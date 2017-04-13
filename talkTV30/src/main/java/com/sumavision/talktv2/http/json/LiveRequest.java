package com.sumavision.talktv2.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LiveRequest extends BaseJsonRequest{
	Context context;
	public LiveRequest(Context context){
		this.context = context;
	}

	@Override
	public JSONObject make() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("method", Constants.channelTypeDetailList);
			jsonObject.put("version", "3.0.7");
			jsonObject.put("client", JSONMessageType.SOURCE);
			jsonObject.put("jsession", UserNow.current().jsession);
			jsonObject.put("p2pType", 2);
			if (UserNow.current().userID > 0) {
				jsonObject.put("userId", UserNow.current().userID);
			}
			String localData = getLocalChannel();
			if (!TextUtils.isEmpty(localData)){
				jsonObject.put("ids",localData);
			}
			Log.i("mylog", jsonObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return jsonObject;
	}
	private String getLocalChannel(){
		StringBuilder result = new StringBuilder("");
		Set<String> favs = PreferencesUtils.getStringSet(context,null,Constants.SP_CHANNEL_RECORD,new HashSet<String>());
		if (favs.size()>0){
			Iterator<String> iterator = favs.iterator();
			while (iterator.hasNext()){
				result.append(iterator.next()+",");
			}
			result.deleteCharAt(result.length() -1);
		}
		return result.toString();
	}
}
