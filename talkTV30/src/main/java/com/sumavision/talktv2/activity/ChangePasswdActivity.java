package com.sumavision.talktv2.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.CustomTextWatcher;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnUserUpdateListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改密码
 * 
 * @author suma-hpb
 * 
 */
public class ChangePasswdActivity extends BaseActivity implements
		OnClickListener, OnUserUpdateListener {
	private EditText passwordText;
	private EditText newPasswordText;
	private EditText confirmPasswordText;
	private ImageView oldbtn, newbtn, checkbtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_passwd);
		getSupportActionBar().setTitle(getString(R.string.userinfo_psd_title));
		initView();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ChangePasswdActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ChangePasswdActivity");
		super.onPause();
	}

	private void initView() {
		initLoadingLayout();
		passwordText = (EditText) findViewById(R.id.psd_init_edt);
		passwordText
				.addTextChangedListener(new MyTextWatcher(R.id.psd_init_edt));
		newPasswordText = (EditText) findViewById(R.id.newpsd_init_edt);
		newPasswordText.addTextChangedListener(new MyTextWatcher(
				R.id.newpsd_init_edt));
		confirmPasswordText = (EditText) findViewById(R.id.checkpsd_init_edt);
		confirmPasswordText.addTextChangedListener(new MyTextWatcher(
				R.id.checkpsd_init_edt));
		findViewById(R.id.sure_btn_ok).setOnClickListener(this);
		oldbtn = (ImageView) findViewById(R.id.oldpsd_delete_btn);
		oldbtn.setOnClickListener(this);
		oldbtn.setVisibility(View.GONE);
		newbtn = (ImageView) findViewById(R.id.psd_delete_btn);
		newbtn.setOnClickListener(this);
		newbtn.setVisibility(View.GONE);
		checkbtn = (ImageView) findViewById(R.id.checkpsd_delete_btn);
		checkbtn.setOnClickListener(this);
		checkbtn.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sure_btn_ok:
			commit();
			break;
		case R.id.oldpsd_delete_btn:
			passwordText.setText("");
			break;
		case R.id.psd_delete_btn:
			newPasswordText.setText("");
			break;
		case R.id.checkpsd_delete_btn:
			confirmPasswordText.setText("");
			break;
		default:
			break;
		}
	}

	private void commit() {
		String oldPasswd = passwordText.getText().toString().trim();
		String tempPasswrod = newPasswordText.getText().toString().trim();
		String tempConfirmPassword = confirmPasswordText.getText().toString().trim();

		if (TextUtils.isEmpty(oldPasswd)) {
			DialogUtil.alertToast(getApplicationContext(), "请先输入原密码");
			return;
		}
//		else if (oldPasswd.equals(UserNow.current().passwd)) {
//
//		} else {
//			DialogUtil.alertToast(getApplicationContext(), "原密码输入错误！");
//			return;
//		}

		if (!TextUtils.isEmpty(tempPasswrod)) {
			if (tempPasswrod.equals(tempConfirmPassword)
					&& tempPasswrod.length() > 5 && tempPasswrod.length() < 17
					&& ispsd(tempPasswrod)) {
				UserModify.current().passwdNew = tempPasswrod;
				UserModify.current().passwdNewFlag = 1;
			} else if (!ispsd(tempPasswrod) || !ispsd(tempConfirmPassword)) {
				DialogUtil.alertToast(getApplicationContext(), "密码不能为汉字");
				return;
			} else if (tempPasswrod.length() < 6 || tempPasswrod.length() > 16) {
				DialogUtil.alertToast(getApplicationContext(),
						"请输入正确的密码，密码位数6-16位！");
				return;
			} else {
				DialogUtil.alertToast(getApplicationContext(), "两次密码不一致！");
				return;
			}
		} else {
			DialogUtil.alertToast(getApplicationContext(), "请输入新密码！");
			return;
		}

		if (!TextUtils.isEmpty(oldPasswd) && !TextUtils.isEmpty(tempPasswrod)
				&& !TextUtils.isEmpty(tempConfirmPassword)) {
			mUserModify.passwdOld = oldPasswd;
			mUserModify.passwdNew = tempPasswrod;
			mUserModify.passwdNewFlag = 1;
			sendUserUpdateInfo();
		} else {
			UserModify.current().passwdNewFlag = 0;
			return;
		}

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

	UserModify mUserModify = new UserModify();

	private void sendUserUpdateInfo() {
		VolleyUserRequest.UserUpdate(this, this, mUserModify);
	}

	class MyTextWatcher extends CustomTextWatcher {
		private int resId;

		public MyTextWatcher(int resId) {
			this.resId = resId;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (resId == R.id.checkpsd_init_edt) {
				checkInput(s, checkbtn);
			} else if (resId == R.id.newpsd_init_edt) {
				checkInput(s, newbtn);
			} else if (resId == R.id.psd_init_edt) {
				checkInput(s, oldbtn);
			}
		}

		private void checkInput(CharSequence s, ImageView btn) {
			if (!TextUtils.isEmpty(s)) {
				btn.setVisibility(View.VISIBLE);
			} else {
				btn.setVisibility(View.GONE);
			}
		}

	}

	@Override
	public void updateUserResult(int errCode, String errMsg) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(getApplicationContext(), "修改成功");
			if (UserModify.current().passwdNewFlag == 1) {
				UserNow.current().passwd = UserModify.current().passwdNew;
			}
			LocalInfo.SaveUserData(this, true);
			UserNow.current().showAlert = false;
			finish();
		} else {
			DialogUtil.alertToast(getApplicationContext(), errMsg);
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.userUpdate);
	}

}
