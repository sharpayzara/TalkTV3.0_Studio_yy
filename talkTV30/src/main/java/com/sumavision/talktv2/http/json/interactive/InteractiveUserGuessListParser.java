package com.sumavision.talktv2.http.json.interactive;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 用户竞猜列表解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveUserGuessListParser extends BaseJsonParser {

	public ArrayList<InteractiveGuess> userGuessList = new ArrayList<InteractiveGuess>();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONArray guessObj = content.getJSONArray("guess");
				if (guessObj != null && guessObj.length() > 0) {
					Type type = new TypeToken<ArrayList<InteractiveGuess>>() {
					}.getType();
					ArrayList<InteractiveGuess> resultList = new Gson()
							.fromJson(guessObj.toString(), type);
					for (InteractiveGuess guess : resultList) {
						guess.userJoin = true;
					}
					userGuessList.addAll(resultList);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
