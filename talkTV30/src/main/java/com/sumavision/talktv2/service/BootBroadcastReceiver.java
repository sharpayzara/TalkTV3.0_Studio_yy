package com.sumavision.talktv2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


/**
 * 开机启动服务
 * 
 * @author suma-hpb
 * 
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("BootBroadcastReceiver", "开机启动直播提醒");
		String action = intent.getAction();
		boolean reload = false;
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				reload = true;
			}
		} else {
			reload = true;
		}
		if (reload) {
			Intent sintent = new Intent(context, LiveAlertService.class);
			context.startService(sintent);
		}
	}
}
