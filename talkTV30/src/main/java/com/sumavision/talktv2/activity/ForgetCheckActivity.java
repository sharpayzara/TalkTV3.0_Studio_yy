package com.sumavision.talktv2.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnCheckCodeCheckListener;
import com.sumavision.talktv2.http.listener.OnReSendEmailListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.SimpleApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 密码找回-验证码校验
 * 
 * @author liwei
 * @description 邮箱验证码 填写并检验
 */
public class ForgetCheckActivity extends BaseActivity implements
		OnReSendEmailListener, OnClickListener, OnCheckCodeCheckListener {
	// 邮箱
	private TextView emailTxt, checkTime;
	private EditText etText;
	// 时间的结束时间
	private final int TIME_FINISH = 4;
	// 倒计时计数
	Timer timer;

	private RelativeLayout checkAgain;
	private Animation leftRightAnim;

	private RelativeLayout connectBg;

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ForgetCheckActivity");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ForgetCheckActivity");
	}

	// 隐藏dialog
	private void hidepb() {
		connectBg.setVisibility(View.GONE);
	}

	// 显示dialog
	private void showpb() {
		connectBg.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_forget_check);
		getSupportActionBar().setTitle(getString(R.string.forget_check_title));
		checkAgain = (RelativeLayout) findViewById(R.id.check_again);
		checkAgain.setOnClickListener(this);
		emailTxt = (TextView) findViewById(R.id.forget_check_txt);
		emailTxt.setText(getResources().getText(R.string.forget_check_email)
				+ oldEmail());
		checkTime = (TextView) findViewById(R.id.check_time_finish);
		checkTime.setText("");
		findViewById(R.id.check_btn_ok).setOnClickListener(this);
		etText = (EditText) findViewById(R.id.check_edt);
		if (timer == null)
			timer = new Timer();
		if (timerTask == null)
			timerTask = new TimerTask() {

				@Override
				public void run() {
					recLen--;
					Message message = new Message();

					message.what = 5;

					mHandler.sendMessage(message);

				}
			};
		timer.schedule(timerTask, 1000, 1000); // timeTask
		checkAgain.setClickable(false);
		connectBg = (RelativeLayout) findViewById(R.id.errLayout);
		connectBg.setVisibility(View.GONE);
		leftRightAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

	}

	private String oldEmail() {
		String str = getIntent().getStringExtra("email");
		String email = "";
		if (null != str && !str.equals("") && str.contains("@")) {
			int index = str.indexOf("@");
			String last = str.substring(index);
			String begain = str.substring(0, index);

			if (begain.length() > 0 && begain.length() < 5) {
				email = "***" + last;
			} else {
				String a = begain.substring(0, 2);
				email = a + "***" + last;
			}
		}
		return email;
	}

	private void getCheckTask() {
		VolleyUserRequest.resendEmail(this, this);
	}

	private void getCheckEmailTask() {
		showpb();
		VolleyUserRequest.checkCode(this, etText.getText().toString().trim(),
				this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.check_again:
			recLen = 60;
			timer = new Timer();
			timerTask = new TimerTask() {

				@Override
				public void run() {
					recLen--;
					Message message = new Message();

					message.what = 5;

					mHandler.sendMessage(message);

				}
			};
			if (timer != null && timerTask != null)
				timer.schedule(timerTask, 1000, 1000); // timeTask
			checkAgain.setClickable(false);
			getCheckTask();
			break;
		case R.id.check_btn_ok:
			String ettxt = etText.getText().toString().trim();
			if (isNumeric(ettxt)) {
				getCheckEmailTask();
			} else {
				DialogUtil.alertToast(getApplicationContext(), "验证码有误!");
				etText.startAnimation(leftRightAnim);
			}
			break;
		default:
			break;
		}
	}

	private boolean isNumeric(String str) {
		if (str != null && !str.equals("")) {
			if (str.length() != 6)
				return false;
			Pattern pattern = Pattern.compile("[0-9]*");
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}

	private int recLen = 60;

	TimerTask timerTask;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 5:
				checkTime.setText("(" + recLen + ")");
				if (recLen < 1) {
					checkTime.setText("");
					sendEmptyMessage(TIME_FINISH);
				}
				break;
			case TIME_FINISH:
				checkAgain.setClickable(true);
				timer.cancel();
				timerTask.cancel();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onSendEmail(int errCode, String errMsg) {
		hidepb();
		if (errCode != JSONMessageType.SERVER_CODE_OK
				&& !TextUtils.isEmpty(errMsg)) {
			DialogUtil.alertToast(getApplicationContext(), errMsg);
		}

	}

	@Override
	public void onCheckCode(int errCode, String errMsg) {
		hidepb();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (!TextUtils.isEmpty(errMsg))
				DialogUtil.alertToast(getApplicationContext(), errMsg);
			break;
		case JSONMessageType.SERVER_CODE_OK:
			if (UserModify.current().isCode) {
				Intent intent = new Intent();
				intent.putExtra("checkcode", etText.getText().toString().trim());
				intent.setClass(this, PasswdFindActivity.class);
				startActivity(intent);
			} else {
				DialogUtil.alertToast(getApplicationContext(), "验证码有误!");
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.pwdRecovery);
		VolleyHelper.cancelRequest(Constants.checkCheckCode);
	}

}
