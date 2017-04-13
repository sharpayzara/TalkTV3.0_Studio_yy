package com.sumavision.talktv2.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.widget.Toast;

public class NetManagerReceiver extends BroadcastReceiver {

	public interface OnNetworkChangeListener {
		public void isNetworkConnected(boolean connect);
	}

	OnNetworkChangeListener listener;

	public NetManagerReceiver(OnNetworkChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = null;// get net connect status
		if (listener != null) {
			NetworkInfo info = connManager.getActiveNetworkInfo();
			boolean connect=false;
			if (info != null) {
				connect = info.isAvailable();
			}
			listener.isNetworkConnected(connect);
		}
		if (connManager != null) {
			if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
				state = connManager.getNetworkInfo(
						ConnectivityManager.TYPE_MOBILE).getState();// get
																	// mobile
			}
		}
		if (state != null) {
			if (State.CONNECTED == state) {
				Toast.makeText(context, "亲，当前使用2G/3G网络，请注意您的流量!",
						Toast.LENGTH_LONG).show();
			}
		}

	}
}
