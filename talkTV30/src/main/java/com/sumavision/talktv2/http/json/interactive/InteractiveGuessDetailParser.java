package com.sumavision.talktv2.http.json.interactive;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.gson.Gson;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.GuessOption;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 竞猜详情解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessDetailParser extends BaseJsonParser {

	public InteractiveGuess guess = new InteractiveGuess();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				JSONObject guessObj = content.getJSONObject("guess");
				guess.id = guessObj.optInt("id", 0);
				guess.prizeCount = guessObj.optInt("prizeCount", 0);
				guess.prizeType = guessObj.optInt("prizeType", 0);
				guess.type = guessObj.optInt("type", 0);
				guess.status = guessObj.optBoolean("status", false);
				guess.title = guessObj.optString("title", "");
				guess.endTime = guessObj.optString("endTime", "");
				JSONArray optArr = guessObj.optJSONArray("option");
				if (optArr != null && optArr.length() > 0) {
					ArrayList<GuessOption> list = new ArrayList<GuessOption>();
					int len = optArr.length();
					for (int index = 0; index < len; index++) {
						GuessOption opt = new Gson().fromJson(optArr
								.getJSONObject(index).toString(),
								GuessOption.class);
						list.add(opt);
					}
					guess.option = list;
				}
				JSONObject userOptObj = content.optJSONObject("userOption");
				if (userOptObj != null) {
					GuessOption opt = new GuessOption();
					opt.id = userOptObj.optInt("id", 0);
					opt.name = userOptObj.optString("name", "");
					opt.betAmount = userOptObj.optInt("betAmount", 0);
					guess.userWin = userOptObj.optBoolean("win", false);
					guess.userOption = opt;
				}
				guess.userJoin = content.optBoolean("userJoin", false);
				UserNow.current().totalPoint = content.optInt("userPoint", 0);
				UserNow.current().diamond = content.optInt("userDiamond", 0);
			}
		} catch (JSONException e) {
			Log.e("InteractiveCyclopediaListTask-parse", e.toString());
		}

	}

}
