package com.sumavision.talktv2.http.json.interactive;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 竞猜列表json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessListParser extends BaseJsonParser {

	public ArrayList<InteractiveGuess> guessingList = new ArrayList<InteractiveGuess>();

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
						if (guess.status) {
							guessingList.add(guess);
						}
					}
				}
			}
		} catch (JSONException e) {
			Log.e("InteractiveGuessListTask-parse", e.toString());
		}

	}

}
