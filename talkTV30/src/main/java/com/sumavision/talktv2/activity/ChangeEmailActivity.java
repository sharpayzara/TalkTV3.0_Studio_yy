package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.suamvison.net.JSONMessageType;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnUserUpdateListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改邮箱
 * 
 * @author suma-hpb
 * 
 */
public class ChangeEmailActivity extends BaseActivity implements
		OnClickListener, OnUserUpdateListener {
	private EditText oldEmailedt;
	private EditText newEmailedt;
	private EditText psdedt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar()
				.setTitle(getString(R.string.userinfo_email_title));
		setContentView(R.layout.activity_change_email);
		initView();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ChangeEmailActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ChangeEmailActivity");
		super.onPause();
	}
	private void initView() {
		findViewById(R.id.sure_btn_ok).setOnClickListener(this);
		oldEmailedt = (EditText) findViewById(R.id.email_init_edt);
		newEmailedt = (EditText) findViewById(R.id.newemail_init_edt);
		psdedt = (EditText) findViewById(R.id.psd_init_edt);
		initLoadingLayout();
		oldEmailedt.setEnabled(false);
		String hideEmail = hidePartOfEmail();
		if ("".equals(hideEmail)) {
			oldEmailedt.setText("未绑定邮箱");
		} else {
			oldEmailedt.setText(getResources().getText(
					R.string.userinfo_oldemail_hint)
					+ ":" + hideEmail);
		}
	}

	private String hidePartOfEmail() {
		String str = UserNow.current().eMail;
		String email = "";
		if (!TextUtils.isEmpty(str) && str.contains("@")) {
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

	private void hideSoftPad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			hideSoftPad();
			finish();
			break;
		case R.id.sure_btn_ok:
			commit();
			break;

		default:
			break;
		}

	}

	private void commit() {
		String oldEmail = oldEmailedt.getText().toString().trim();
		String newEmail = newEmailedt.getText().toString();
		String tempPassword = psdedt.getText().toString();

		if (TextUtils.isEmpty(newEmail)) {
			if (!TextUtils.isEmpty(oldEmail)) {
				DialogUtil.alertToast(getApplicationContext(), "请先输入新邮箱");
				return;
			}
		} else if (newEmail.equals(UserNow.current().eMail)) {
			DialogUtil.alertToast(getApplicationContext(), "您输入的邮箱与原来一样");
			return;
		} else if (TextUtils.isEmpty(tempPassword)) {
			DialogUtil.alertToast(getApplicationContext(), "密码不能为空");
			return;
		} else if (!tempPassword.equals(UserNow.current().passwd)) {
			DialogUtil.alertToast(getApplicationContext(), "密码输入错误");
			return;
		} else if (!AppUtil.isEmail(newEmail)) {
			DialogUtil.alertToast(getApplicationContext(), "新邮箱输入错误");
			return;
		} else {
			mUserModify.eMail = newEmail;
			mUserModify.eMailFlag = 1;
			mUserModify.passwdOld = UserNow.current().passwd;
		}

		sendUserUpdateInfo();
	}

	UserModify mUserModify = new UserModify();

	private void sendUserUpdateInfo() {
		showLoadingLayout();
		VolleyUserRequest.UserUpdate(this, this, mUserModify);
	}

	@Override
	public void updateUserResult(int errCode, String errMsg) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(getApplicationContext(), "修改成功");
			if (mUserModify.eMailFlag == 1) {
				UserNow.current().eMail = mUserModify.eMail;
			}
			LocalInfo.SaveUserData(this, true);
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
