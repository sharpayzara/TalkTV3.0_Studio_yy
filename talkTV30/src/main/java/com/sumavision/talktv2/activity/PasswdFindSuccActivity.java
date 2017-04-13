package com.sumavision.talktv2.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.SimpleApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改密码成功页
 * 
 * @author suma-hpb
 * 
 */
public class PasswdFindSuccActivity extends BaseActivity {
	private TextView accountTxt, waitTxt;
	// 倒计时计数
	Timer timer = new Timer();
	// 时间的结束时间
	private final int TIME_FINISH = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_find_pwdsucc);
		getSupportActionBar()
				.setTitle(getString(R.string.forget_findpsd_title));
		accountTxt = (TextView) findViewById(R.id.tv_account);
		waitTxt = (TextView) findViewById(R.id.tv_waiting_close);
		accountTxt.setText("账号:" + UserNow.current().name);
		timer.schedule(timerTask, 1000, 1000); // timeTask
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("PasswdFindSuccActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("PasswdFindSuccActivity");
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SimpleApplication.getInstance().exit();
		}
		return super.onKeyDown(keyCode, event);
	}

	private int recLen = 4;
	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			recLen--;
			Message message = new Message();

			message.what = 5;

			mHandler.sendMessage(message);

		}

	};
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 5:
				waitTxt.setText(recLen + "");
				if (recLen < 1) {
					timer.cancel();
					sendEmptyMessage(TIME_FINISH);
				}
				break;
			case TIME_FINISH:
				Intent intent = new Intent();
				intent.setClass(PasswdFindSuccActivity.this,
						SlidingMainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				if (timerTask != null)
					timerTask.cancel();
				break;

			default:
				break;
			}
		};
	};
}
