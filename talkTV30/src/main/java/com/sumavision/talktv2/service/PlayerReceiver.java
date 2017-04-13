package com.sumavision.talktv2.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.UserFeedbackActivity;

/**
 * 第三次从播放器退出弹出对话框
 * */
public class PlayerReceiver extends BroadcastReceiver {

	private static int dialogn = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("quit_from_player")) {
			if (dialogn == 0) {
				createDialog(context);
			}
		}
	}

	private void createDialog(final Context context) {
		dialogn++;
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.dialog_request_comment, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				context.getApplicationContext());
		builder.setView(layout);
		final Dialog dialog = builder.create();
		dialog.getWindow()
				.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();

		Button later = (Button) layout.findViewById(R.id.later);
		Button advice = (Button) layout.findViewById(R.id.advice);
		Button goodcomment = (Button) layout.findViewById(R.id.goodcomment);
		later.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		advice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserFeedbackActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				dialog.dismiss();
			}
		});
		goodcomment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openScoreActivity(context);
				startCommentService(context);
				dialog.dismiss();
			}
		});
	}

	private void openScoreActivity(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="
					+ context.getPackageName()));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (ActivityNotFoundException E) {
			Toast.makeText(context, "亲，您还没有安装应用市场哦！", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void startCommentService(Context context) {
		Intent intent = new Intent(context, GoodCommentService.class);
		context.startService(intent);
	}

}