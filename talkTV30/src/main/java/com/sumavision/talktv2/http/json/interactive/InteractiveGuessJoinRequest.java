package com.sumavision.talktv2.http.json.interactive;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.json.BaseJsonRequest;
import com.sumavision.talktv2.utils.Constants;
/**
 * 参与竞猜json请求
 * @author suma-hpb
 *
 */
public class InteractiveGuessJoinRequest extends BaseJsonRequest {

	private int guessId;
	private int optionId;
	private long betCount;

	public InteractiveGuessJoinRequest(int guessId, int optionId, long betCount) {
		super();
		this.guessId = guessId;
		this.optionId = optionId;
		this.betCount = betCount;
	}

	@Override
	public JSONObject make() {
		JSONObject object = new JSONObject();
		try {
			object.put("method", Constants.interactiveGuessJoin);
			object.put("version", "2.6.0");
			object.put("client", "1");
			object.put("jsession", UserNow.current().jsession);
			object.put("guessId", guessId);
			object.put("optionId", optionId);
			object.put("userId", UserNow.current().userID);
			object.put("qty", betCount);
			object.put("useDiamond", false);
		} catch (JSONException e) {
			Log.e("InteractiveGuessJoinTask-request", e.toString());
		}
		return object;
	}

}
