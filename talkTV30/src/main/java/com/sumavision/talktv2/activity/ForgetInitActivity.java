package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnForgetInitListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.SimpleApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 密码找回-第一步提交用户名或邮箱
 * 
 * @author liwei
 * @description
 * @createTime 2014-05-23
 * 
 */
public class ForgetInitActivity extends BaseActivity implements
		OnClickListener, OnForgetInitListener {

	private EditText editText;
	private Animation leftRightAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SimpleApplication.getInstance().addActivity(this);
		getSupportActionBar()
				.setTitle(getString(R.string.forget_findpsd_title));
		setContentView(R.layout.activity_forget_passwd);
		findViewById(R.id.btn_next).setOnClickListener(this);
		editText = (EditText) findViewById(R.id.forget_init_edt);
		leftRightAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		initLoadingLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ForgetInitActivity");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ForgetInitActivity");
	}

	private void getTaskResponse(String input) {
		showLoadingLayout();
		VolleyUserRequest.initForget(this, input, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_next:
			String input = editText.getText().toString().trim();
			if (!TextUtils.isEmpty(input)) {
				getTaskResponse(input);
			} else {
				Toast.makeText(this, "请输入邮箱/用户名", Toast.LENGTH_SHORT).show();
				editText.startAnimation(leftRightAnim);
			}

			break;
		default:
			break;
		}
	}

	@Override
	public void onGetEmailInFindPasswd(int errCode, String errMsg, String email) {

		hideLoadingLayout();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_OK:
			if (!TextUtils.isEmpty(email)) {
				Intent intent = new Intent();
				intent.putExtra("email", email);
				intent.setClass(this, ForgetCheckActivity.class);
				startActivity(intent);
			} else {
				DialogUtil.alertToast(getApplicationContext(),
						"对不起，该账户没有绑定电子邮箱，无法找回密码！");
			}
			break;
		default:
			if (!TextUtils.isEmpty(errMsg)) {
				DialogUtil.alertToast(getApplicationContext(), errMsg);
			}
			break;
		}
	}

	@Override
	public void onError(int code) {
		hideLoadingLayout();
		super.onError(code);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.pwdRecovery);
	}
}
