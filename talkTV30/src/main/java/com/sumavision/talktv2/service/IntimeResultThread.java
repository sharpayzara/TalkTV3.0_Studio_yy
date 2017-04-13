package com.sumavision.talktv2.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.sumavision.talktv2.bean.interactive.GuessOption;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.service.InteractiveIntimeService.OnIntimeGuessListener;
import com.sumavision.talktv2.utils.LongPollingUtil;

/**
 * 实时竞猜结果请求
 * 
 * @author hpb-16152
 * @version 2014-6-6
 * @since
 */
public class IntimeResultThread extends Thread {
	OnIntimeGuessListener listener;

	public void setListener(OnIntimeGuessListener listener) {
		this.listener = listener;
	}

	Context context;

	LongPollingUtil poll = null;

	public IntimeResultThread(OnIntimeGuessListener listener, Context context) {
		this.context = context;
		this.listener = listener;
		poll = new LongPollingUtil();
	}

	private InteractiveGuess intimeGuess = new InteractiveGuess();

	private static final int START = 1;

	private static final int RESULT = 2;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case START:
				if (listener != null) {
					listener.startInteract(intimeGuess);
				}
				break;
			case RESULT:
				if (listener != null) {
					listener.interactResult(intimeGuess);
				}
				break;

			default:
				break;
			}
		};
	};

	private boolean stop;

	public void stop(boolean stop) {
		this.stop = stop;
		if (poll != null) {
			poll.close();
			poll = null;
		}
		this.interrupt();
	}

	@Override
	public void run() {
		// 218.89.192.143
		Log.e("intime resultThred", "intime resultThred start");
		StringBuilder url = new StringBuilder(
				"http://218.89.192.143:8082/subscribe?id=1");
		while (!stop) {
			try {
				String result = poll.execute(context, url.toString());
				if (result != null && result.length() > 0) {
					JSONObject obj = new JSONObject(result);
					JSONObject content = obj.getJSONObject("content");
					JSONObject guessObj = content.optJSONObject("guess");
					intimeGuess.id = guessObj.optInt("id", 0);
					intimeGuess.activityId = guessObj.optInt("activityId", 0);
					intimeGuess.title = guessObj.optString("title", "");
					intimeGuess.picAd = guessObj.optString("picAd", "");
					intimeGuess.prizeCount = guessObj.optInt("point", 0);
					intimeGuess.startTime = guessObj.optString("startTime", "");
					JSONObject answer = guessObj.optJSONObject("answerOption");
					if (answer != null) {
						GuessOption option = new GuessOption();
						option.id = answer.optInt("id", 0);
						option.name = answer.optString("name", "");
						intimeGuess.answerOption = option;
					}
					handler.sendEmptyMessage(RESULT);
				}

			} catch (JSONException e) {
				Log.e("IntimeResultThread", e.toString());
			}
		}
		Log.e("intime ResultThread", "intime ResultThread exit");
	}
}
