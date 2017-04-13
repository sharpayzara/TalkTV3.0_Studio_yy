package com.sumavision.talktv2.service;

import java.util.List;

import org.litepal.crud.DataSupport;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ChannelDetailActivity;
import com.sumavision.talktv2.activity.LiveDetailActivity;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.TimeUtils;

/**
 * 直播提醒
 * 
 * @author suma-hpb
 * 
 */
public class LiveAlertService extends Service {
	List<Remind> programList;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final String TAG = "liveAlertService";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "start");
		programList = DataSupport.findAll(Remind.class);
		if (programList != null && programList.size() > 0) {
			if (mAlertThread != null) {
				mAlertThread.setStopFlag(true);
				mAlertThread.interrupt();
			}
			mAlertThread = new AlertThread();
			mAlertThread.start();
		} else {
			stopSelf();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		Log.e(TAG, "destroy");
		super.onDestroy();
		if (mAlertThread != null) {
			mAlertThread.setStopFlag(true);
			mAlertThread.interrupt();
		}
	};

	AlertThread mAlertThread;
	private int leftMinute;

	class AlertThread extends Thread {
		@Override
		public void run() {
			while (!stopFlag) {
				int size = programList.size();
				android.util.Log
						.i(TAG, "live alert thread,remindCount=" + size);
				if (size == 0) {
					stopFlag = true;
					alertHandler.sendEmptyMessage(STOP_SERVICE);
					return;
				}
				long now = System.currentTimeMillis();
				int index = -1;
				for (int i = 0; i < size; i++) {
					String startTime = programList.get(i).getCpDate() + " "
							+ programList.get(i).getStartTime() + ":00";
					long start = TimeUtils.StringToMillSeconds(startTime);
					if (now < start && (start - now) <= 300000
							&& (start - now) > 60000) {
						leftMinute = (int) ((start - now) / 60000);
						index = i;
						break;
					}
				}
				if (index >= 0) {
					Remind remind = programList.get(index);
					Message msg = alertHandler.obtainMessage();
					msg.what = SEND_NOTIFY;
					msg.obj = remind;
					alertHandler.sendMessage(msg);
					programList.remove(index);
					/* 已持久化删除 */
					remind.delete();
					index = -1;
				}
				try {
					sleep(120000);
				} catch (InterruptedException e) {
					Log.i(TAG, "sleep Interrupted");
				}
			}
		}

		private boolean stopFlag;

		public void setStopFlag(boolean stopFlag) {
			this.stopFlag = stopFlag;
		}
	}

	private static final int STOP_SERVICE = 1;
	private static final int SEND_NOTIFY = 2;
	Handler alertHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STOP_SERVICE:
				stopSelf();
				break;
			case SEND_NOTIFY:
				sendNotification((Remind) msg.obj);
			default:
				break;
			}

		};
	};

	private static final int NOTIFICATION_ID = 4001;

	protected void sendNotification(Remind program) {
		PreferencesUtils.putBoolean(getApplicationContext(),
				Constants.pushMessage, Constants.key_favourite, true);
		String title = "《" + program.getCpName() + "》";
		String content = getString(R.string.live_alert_content)
				+ leftMinute + getString(R.string.live_alert_content_affix);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		Intent intent = new Intent(this, LiveDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("channelId", Integer.parseInt(program.getChannelId()));
		bundle.putString("channelName", program.getChannelName());
		bundle.putString("pic", program.getChannelLogo());
		intent.putExtras(bundle);
		stackBuilder.addParentStack(ChannelDetailActivity.class);
		stackBuilder.addNextIntent(intent);
		PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(
				getApplicationContext()).setSmallIcon(R.drawable.icon)
				.setTicker(title).setWhen(System.currentTimeMillis())
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_VIBRATE)
				.setContentTitle(title).setContentText(content)
				.setContentIntent(contentIntent).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, notification);

	}

}
