package com.sumavision.talktv2.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnChangePasswdListener;
import com.sumavision.talktv2.http.listener.OnLogInListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.SimpleApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 密码找回-修改密码页
 * 
 * @author suma-hpb
 * 
 */
public class PasswdFindActivity extends BaseActivity implements
		OnClickListener, OnLogInListener, OnChangePasswdListener {
	private EditText newpsd;
	private String checkcode;
	private RelativeLayout connectBg;
	private Animation leftRightAnim;

	private void hidepb() {
		connectBg.setVisibility(View.GONE);
	}

	private void showpb() {
		connectBg.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_find_passwd);
		getSupportActionBar().setTitle(getString(R.string.userinfo_psd_title));
		newpsd = (EditText) findViewById(R.id.newpsd_edt);
		findViewById(R.id.btn_commit).setOnClickListener(this);
		if (getIntent().hasExtra("checkcode"))
			checkcode = getIntent().getStringExtra("checkcode");
		connectBg = (RelativeLayout) findViewById(R.id.errLayout);
		connectBg.setVisibility(View.GONE);
		leftRightAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("PasswdFindActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("PasswdFindActivity");
		super.onPause();
	}

	private void getNewpsdTask() {
		showpb();
		VolleyUserRequest.ChangePasswd(this, checkcode, newpsd.getText()
				.toString().trim(), this);
	}

	private void login() {
		UserNow.current().passwd = newpsd.getText().toString().trim();
		VolleyUserRequest.login(this, this);
	}

	private boolean ispsd(String edInput) {
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(edInput);
		p = Pattern.compile("[\u4e00-\u9fa5]");
		m = p.matcher(edInput);
		if (m.find()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_commit:
			if (!newpsd.getText().toString().trim().equals("")
					&& newpsd.getText().toString().trim() != null) {
				if (!ispsd(newpsd.getText().toString())) {
					DialogUtil.alertToast(getApplicationContext(), "密码不能为汉字");
					return;
				} else if (newpsd.getText().toString().length() < 6
						|| newpsd.getText().toString().length() > 16) {
					DialogUtil.alertToast(getApplicationContext(),
							"请输入正确的密码，密码位数6-16位！");
					newpsd.startAnimation(leftRightAnim);
					return;
				} else {
					getNewpsdTask();
				}
			} else {
				DialogUtil.alertToast(getApplicationContext(), "请输入密码!");
				newpsd.startAnimation(leftRightAnim);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			hidepb();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onChangePasswd(int errCode, String errMsg) {
		hidepb();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (!TextUtils.isEmpty(errMsg))
				DialogUtil.alertToast(getApplicationContext(), errMsg);
			break;
		case JSONMessageType.SERVER_CODE_OK:
			login();
			break;
		default:
			break;
		}

	}

	@Override
	public void loginResult(int errCode, int point, String errMsg) {
		hidepb();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (!TextUtils.isEmpty(errMsg))
				DialogUtil.alertToast(getApplicationContext(), errMsg);
			hidepb();
			break;
		case JSONMessageType.SERVER_CODE_OK:
			if (point > 0) {
				DialogUtil.alertToast(getApplicationContext(),
						String.valueOf(point));
			}
			Intent intent = new Intent();
			intent.setClass(this, PasswdFindSuccActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.pwdReset);
		VolleyHelper.cancelRequest(Constants.logIn);
	}
}
