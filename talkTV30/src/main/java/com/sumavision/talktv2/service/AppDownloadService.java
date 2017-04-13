package com.sumavision.talktv2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.AppDownload;

/**
 * 下载app的服务。
 * url为应用的下载地址，必须不为空
 * name为显示的名字。默认为文件下载
 * resId为notification图标，默认为电视粉图标
 * appId为应用对应的ID，如果不需要加积分则不需要
 * */
public class AppDownloadService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String url = intent.getStringExtra("url");
			String name = intent.getStringExtra("name");
			int appId = intent.getIntExtra("appId", -1);
			int resId = intent.getIntExtra("resId", R.drawable.icon_small);
			
			if (appId == -1) {
				AppDownload download = new AppDownload(this);
				download.startDownload(url, name, resId);
			} else {
				AppDownload download = new AppDownload(this, appId);
				download.startDownload(url, name, resId);
			}
		}
		return super.onStartCommand(intent, START_STICKY, startId);
	}
}
