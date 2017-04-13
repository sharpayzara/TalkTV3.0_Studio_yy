package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.GetAddressParser;
import com.sumavision.talktv2.http.json.GetAddressRequest;
import com.sumavision.talktv2.http.listener.OnBindAccountListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * @author HPB
 * @version 3.0
 * @description 绑定账号界面
 * @changeLog
 */
public class BindAccountActivity extends BaseActivity implements
		OnClickListener, OnBindAccountListener {
	private EditText name;
	private EditText passwd;
	private String userName;
	private String passWord;
	private Animation a;

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("BindAccountActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("BindAccountActivity");
		super.onPause();
	}

	ThirdPlatInfo thirdInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.login);
		setContentView(R.layout.activity_bind_account);
		thirdInfo = getIntent().getParcelableExtra("info");
		initLoadingLayout();
		a = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		findViewById(R.id.bindaccount_login).setOnClickListener(this);
		name = (EditText) findViewById(R.id.bindaccount_edit_email);
		name.setHint("用户名/邮箱");
		passwd = (EditText) findViewById(R.id.bindaccount_edit_passwd);
	}

	@Override
	public void onClick(View v) {
		userName = name.getText().toString().trim();
		passWord = passwd.getText().toString().trim();
		switch (v.getId()) {
		case R.id.bindaccount_login:
			if (userName.equals("")) {
				Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
				name.startAnimation(a);
			} else if (passWord.equals("")) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				passwd.startAnimation(a);
			} else if (!userName.equals("") && !passWord.equals("")) {
				hideSoftPad();
				UserNow.current().name = userName;
				UserNow.current().passwd = passWord;
				UserNow.current().userType = 2;
				bindRegister();
			}
			break;
		default:
			break;
		}
	}

	// 绑定注册
	private void bindRegister() {
		showLoadingLayout();
		VolleyUserRequest.bindAccount(thirdInfo, this, this);
	}

	private void hideSoftPad() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(BindAccountActivity.this
						.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void bindAccountResult(int errCode, int point, String errMsg) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (point > 0) {
				if (UserNow.current().action == null) {
					UserNow.current().action = "";
				}
				String vipStr="";
				if (UserNow.current().vipIncPoint>0){
					vipStr = "VIP加成"+UserNow.current().vipIncPoint+"积分";
				}
				DialogUtil.updateScoreToast(UserNow.current().action + " +"
						+ (UserNow.current().point) + "积分"+vipStr);
			}
			EventBus.getDefault().post(new UserInfoEvent());
			checkAddressRequest();
		} else {
			Toast.makeText(BindAccountActivity.this, errMsg, Toast.LENGTH_LONG)
					.show();
		}
	}
	
	private GetAddressParser aparser = new GetAddressParser();
	public void checkAddressRequest() {
		showLoadingLayout();
		VolleyHelper.post(new GetAddressRequest().make(), new ParseListener(
				aparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (aparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					((TalkTvApplication) getApplication()).mAddressData = aparser.address;
				}
				setResult(RESULT_OK);
				finish();
			}
		}, null);
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.bindUser);
	}

}