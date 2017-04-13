package com.sumavision.talktv.videoplayer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {

	public static final String netAction = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String startAction = "com.sumavison.app_boot_start";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action != null
				&& (action.equals(netAction) || action.equals(startAction))) {
			ConnectivityManager connManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			if (null != netInfo
					&& ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
				// DialogUtil.alertToast(context,
				// Constants.environment_net_wifi);
			}else if (netInfo == null) {
				Toast.makeText(context, "无网络连接，请连接至网络", Toast.LENGTH_SHORT).show();
			}else if (netInfo != null && ConnectivityManager.TYPE_MOBILE == netInfo.getType()) {
//				DialogUtil.alertToast(context,
//						Constants.environment_net_not_wifi);
				Toast.makeText(context, "您正在使用2G/3G流量", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
