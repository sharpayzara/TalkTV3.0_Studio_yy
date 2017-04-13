package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.interactive.GuessResult;
import com.sumavision.talktv2.http.json.BaseJsonParser;

/**
 * 参与竞猜json解析
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessJoinParser extends BaseJsonParser {

	public GuessResult guessResult = new GuessResult();

	@Override
	public void parse(JSONObject obj) {
		try {
			errCode = obj.optInt("code", JSONMessageType.SERVER_CODE_ERROR);
			if (errCode == JSONMessageType.SERVER_CODE_OK) {
				JSONObject content = obj.getJSONObject("content");
				guessResult.consumeCount = content.optInt("guessQty", 0);
				guessResult.guessCorrect = content.optBoolean("guessCorrect",
						false);
				guessResult.prizeType = content.optInt("prizeType", 0);
				guessResult.userOptionName = content.optString(
						"userOptionName", "");
				guessResult.userOptionId = content.optInt("userOptionId", 0);
				UserNow.current().totalPoint = content.optInt("userPoint", 0);
				UserNow.current().diamond = content.optInt("userDiamond", 0);
				JSONObject userObj = content.optJSONObject("newUserInfo");
				setPointInfo(userObj);
			}
		} catch (JSONException e) {
			Log.e("InteractiveGuessJoinTask-parse", e.toString());
		}

	}

}
