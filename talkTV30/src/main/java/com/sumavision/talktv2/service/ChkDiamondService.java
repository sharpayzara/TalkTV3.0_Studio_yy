package com.sumavision.talktv2.service;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.suamvison.net.NetUtils;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;

/**
 * 查询用户宝石(支付成功)
 * 
 * @author suma-hpb
 * 
 */
public class ChkDiamondService extends Service implements OnHttpErrorListener {

	private static ConcurrentHashMap<Long, SearchThread> sThreadMap = new ConcurrentHashMap<Long, ChkDiamondService.SearchThread>();
	private Handler InfoHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			VolleyUserRequest.queryInfo(ChkDiamondService.this,
					getApplicationContext(), null);
			if (sThreadMap.size() == 0) {
				stopSelf();
			}

		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("chkDiamondService", "onDestroy");
		for (Long orderId : sThreadMap.keySet()) {
			SearchThread mThread = sThreadMap.get(orderId);
			if (mThread.isAlive()) {
				mThread.setStop(true);
				mThread.interrupt();
			}
		}
		sThreadMap.clear();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		long orderId = intent.getLongExtra("orderId", 0);
		if (orderId > 0) {
			if (!sThreadMap.containsKey(orderId)) {
				SearchThread mSearchThread = new SearchThread(orderId);
				sThreadMap.put(orderId, mSearchThread);
				mSearchThread.start();
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 订单查询接口
	 * 
	 * @author suma-hpb
	 * 
	 */
	class SearchThread extends Thread {
		long orderId;

		public SearchThread(long orderId) {
			this.orderId = orderId;
		}

		private boolean stop;

		public boolean isStop() {
			return stop;
		}

		public void setStop(boolean stop) {
			this.stop = stop;
		}

		@Override
		public void run() {
			Log.e("order Search start", "orderId:" + orderId);
			while (!stop) {
				String result = NetUtils.execute(getApplicationContext(),
						createRequest().toString(), null);
				parse(result);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Log.e("InterruptedException", "");
				}
			}
			Log.e("order Search stop", "orderId:" + orderId);
		}

		private JSONObject createRequest() {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("method", "searchOrderPay");
				jsonObject.put("version", "2.6.0");
				jsonObject.put("client", JSONMessageType.SOURCE);
				jsonObject.put("jsession", UserNow.current().jsession);
				jsonObject.put("userId", UserNow.current().userID);
				jsonObject.put("orderId", orderId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObject;
		}

		private void parse(String s) {
			int errCode = 1;
			try {
				JSONObject jsonObject = new JSONObject(s);
				if (jsonObject.has("code")) {
					errCode = jsonObject.getInt("code");
				} else if (jsonObject.has("errcode")) {
					errCode = jsonObject.getInt("errcode");
				} else if (jsonObject.has("errorCode")) {
					errCode = jsonObject.getInt("errorCode");
				}
				if (jsonObject.has("jsession")) {
					UserNow.current().jsession = jsonObject
							.getString("jsession");
				}
				if (errCode == JSONMessageType.SERVER_CODE_OK) {
					JSONObject content = jsonObject.getJSONObject("content");
					boolean paySuccess = content.optBoolean("success");
					if (paySuccess) {
						stop = true;
						sThreadMap.remove(orderId);
						InfoHandler.sendEmptyMessage(0);
					}
				}
			} catch (JSONException e) {
				return;
			}
		}
	}

	@Override
	public void onError(int code) {
		Log.e("chkDiamondService", "query userInfo failed");

	}
}
