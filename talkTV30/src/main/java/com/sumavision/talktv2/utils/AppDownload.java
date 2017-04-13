package com.sumavision.talktv2.utils;

import java.io.File;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.Toast;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DownloadRecommendAppParser;
import com.sumavision.talktv2.http.json.DownloadRecommendAppRequest;

/**
 * 下载应用 详情见AppDownloadService
 * **/
public class AppDownload {

	private DownloadManager mDownloadManager;
	private Cursor mCursor;
	private DownloadManager.Query mQuery;
	private Handler mHandler;

	private NotificationManager mNotifyManager;
	private NotificationCompat.Builder mBuilder;

	private Context context;
	private long id;
	private int appId;

	private String downloadPath;
	private File mFile;
	private boolean isUpdate;

	public AppDownload(Context context, int appId) {
		this.context = context;
		this.appId = appId;
		this.isUpdate = false;
		init();
	}

	public AppDownload(Context context) {
		this.context = context;
		this.isUpdate = true;
		init();
	}

	private void init() {
		mDownloadManager = (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
		mNotifyManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mQuery = new DownloadManager.Query();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (mNotifyManager != null && mBuilder != null) {
						int prog = msg.arg2;
						if (prog < 100) {
							mBuilder.setProgress(100, prog, false);
							mBuilder.setContentText("正在下载：" + prog + "%");
							mNotifyManager.notify(msg.arg1, mBuilder.build());
						} else {
							changeStatus(msg.arg1);
							removeMessage();
							return;
						}
					}
				}
				mHandler.removeMessages(1);
				queryInfo();
				super.handleMessage(msg);
			}
		};
	}

	public boolean startDownload(String url, String name, int resId) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardExist) {
			Toast.makeText(context, "SD卡不存在，无法更新程序，请插入SD卡后重试",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(url)) {
			// Toast.makeText(context, "下载地址不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		Uri uri = Uri.parse(url);
		if (TextUtils.isEmpty(name)) {
			name = String.valueOf(System.currentTimeMillis());
		}
		if (!name.endsWith(".apk")) {
			name += ".apk";
		}
		DownloadManager.Request request = new DownloadManager.Request(uri);
		try {
			request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);
		} catch (Exception e) {
			request.setShowRunningNotification(false);
		}
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, name);

		StringBuilder builder = new StringBuilder(
				Environment.getExternalStorageDirectory() + "/"
						+ Environment.DIRECTORY_DOWNLOADS + "/" + name);
		downloadPath = builder.toString();
		mFile = new File(downloadPath);
		if (mFile.isFile() && mFile.exists()) {
			mFile.delete();
		}

		id = mDownloadManager.enqueue(request);
		mQuery.setFilterById(id);
		createNotification((int) (id + 10000), name, resId);
		queryInfo();
		return true;
	}

	private void createNotification(int id, String name, int resId) {
		if (name == null) {
			name = "";
		}
		mBuilder = new NotificationCompat.Builder(context).setSmallIcon(resId)
				.setContentTitle(name).setContentText("正在下载");
		mBuilder.setOngoing(false);
		mBuilder.setAutoCancel(true);
		mBuilder.setProgress(100, 0, false);
		mNotifyManager.notify(id, mBuilder.build());
	}

	public void removeMessage() {
		if (mHandler != null) {
			mHandler.removeMessages(1);
		}
	}

	private void changeStatus(int notifyId) {
		mBuilder.setProgress(0, 0, false);
		mBuilder.setContentText("下载完成，点击安装");
		mBuilder.setAutoCancel(true);
		Intent install = new Intent();
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setAction(android.content.Intent.ACTION_VIEW);
		install.setDataAndType(Uri.fromFile(mFile),
				"application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				notifyId, install, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotifyManager.notify(notifyId, mBuilder.build());
		if (!isUpdate) {
			doRequest();
		}
	}

	private void queryInfo() {
		mCursor = mDownloadManager.query(mQuery);
		if (mCursor.moveToFirst()) {
			int currBytesIndex = mCursor
					.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
			int totalBytesIndex = mCursor
					.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
			int curr = mCursor.getInt(currBytesIndex);
			int total = mCursor.getInt(totalBytesIndex);
			int notifyId = (int) id + 10000;
			double progress = (double) curr / (double) total;
			Message msg = mHandler.obtainMessage();
			msg.what = 1;
			msg.arg1 = notifyId;
			msg.arg2 = (int) (progress * 100);
			mHandler.sendMessageDelayed(msg, 1000);
			mCursor.close();
		}
	}

	DownloadRecommendAppParser mParser = new DownloadRecommendAppParser();

	private void doRequest() {
		VolleyHelper.post(new DownloadRecommendAppRequest(appId).make(),
				new ParseListener(mParser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						if (mParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							UserNow.current().setTotalPoint(mParser.totalPoint,mParser.userInfo.vipIncPoint);
						}
					}
				}, null);
	}
}
