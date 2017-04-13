package com.sumavision.talktv2.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.sumavision.talktv2.bean.interactive.GuessOption;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.utils.LongPollingUtil;

/**
 * 实时竞猜请求
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveIntimeService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	private InteractiveBinder myBinder = new InteractiveBinder();

	public class InteractiveBinder extends Binder {
		public InteractiveIntimeService getService() {
			return InteractiveIntimeService.this;
		}
	}

	OnIntimeGuessListener listener;

	public void setListener(OnIntimeGuessListener listener) {
		this.listener = listener;
	}

	private InteractiveGuess intimeGuess = new InteractiveGuess();

	private InTimeThread startThread;

	LongPollingUtil poll = null;

	public void inTimeGuessTask() {
		poll = new LongPollingUtil();
		startThread = new InTimeThread();
		startThread.start();
	}

	public void stopThread() {
		if (startThread != null) {
			if (poll != null) {
				poll.close();
				poll = null;
			}
			startThread.setStop(true);
			startThread.interrupt();
			startThread = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("InteractiveIntimeService", "ondestory");
	}

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

	class InTimeThread extends Thread {
		private boolean stop = false;

		public void setStop(boolean stop) {
			this.stop = stop;
		}

		@Override
		public void run() {
			Log.e("intime startThred", "intime startThred start");
			StringBuilder url = new StringBuilder(
					"http://218.89.192.143:8082/subscribe?id=0");
			while (!stop) {
				try {
					String result = poll.execute(getApplicationContext(),
							url.toString());
					if (result != null && result.length() > 0) {
						JSONObject obj = new JSONObject(result);
						JSONObject content = obj.getJSONObject("content");
						JSONObject guessObj = content.optJSONObject("guess");
						intimeGuess.id = guessObj.optInt("id", 0);
						intimeGuess.activityId = guessObj.optInt("activityId",
								0);
						intimeGuess.title = guessObj.optString("title", "");
						intimeGuess.picAd = guessObj.optString("picAd", "");
						intimeGuess.prizeCount = guessObj.optInt("point", 0);
						intimeGuess.startTime = guessObj.optString("startTime",
								"");
						intimeGuess.interactiveDuration = guessObj.optInt(
								"showSeconds", 0);
						ArrayList<GuessOption> options = new ArrayList<GuessOption>();
						JSONArray optArr = guessObj.getJSONArray("option");
						if (optArr != null && optArr.length() > 0) {
							int len = optArr.length();
							for (int index = 0; index < len; index++) {
								JSONObject optObj = optArr.getJSONObject(index);
								GuessOption opt = new GuessOption();
								opt.id = optObj.optInt("id", 0);
								opt.name = optObj.optString("name", "");
								options.add(opt);
							}
						}
						intimeGuess.option = options;
						JSONObject answer = guessObj
								.optJSONObject("AnswerOption");
						if (answer != null) {
							intimeGuess.answerOption = new Gson().fromJson(
									answer.toString(), GuessOption.class);
						}
						handler.sendEmptyMessage(START);
					}

				} catch (JSONException e) {
					Log.e("IntimeStartThread", e.toString());
				}

			}
			Log.e("intime startThred", "intime startThred exit");

		}
	}

	/**
	 * 实时竞猜回调
	 * 
	 * @author suma-hpb
	 * @version
	 * @description
	 */
	public interface OnIntimeGuessListener {
		/**
		 * 竞猜请求回调
		 * 
		 * @param intimeGuess
		 */
		public void startInteract(InteractiveGuess intimeGuess);

		/**
		 * 竞猜结果请求回调
		 * 
		 * @param intimeGuess
		 */
		public void interactResult(InteractiveGuess intimeGuess);
	}
}
