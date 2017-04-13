package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv.videoplayer.activity.WebBrowserActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UmLoginInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.listener.OnBindAccountListener;
import com.sumavision.talktv2.http.listener.OnRegisterListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.share.AuthManager;
import com.sumavision.talktv2.share.OnUMLoginListener;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager.OnSinaUserInfoListener;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImeiUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import de.greenrobot.event.EventBus;

/**
 * 
 * @description 注册界面
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
		OnRegisterListener, OnBindAccountListener {

	private Button btnOk;
	private EditText name;
	private EditText passwd;
	private EditText eMail;
	private EditText inviteCode;
	private ImageView namebtn, passwdbtn, emailbtn;
	private ImageView headpic;
	private LinearLayout bindOldAccount;
	private Button bindOldAccountBtn;
	private RelativeLayout clientLayout;
	private TextView readP;
	private TextView readF;
	private String userName;
	private String passWord;
	private String userEMail;
	private Animation a;
	private CheckBox cb;
	public static final int BIND = 1;
	public static final int NORMAL = 0;
	private int type = NORMAL;

	ThirdPlatInfo thirdInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getSupportActionBar().setTitle(getString(R.string.reg));
		type = getIntent().getIntExtra("type", NORMAL);
		thirdInfo = getIntent().getParcelableExtra("info");
		initLoadingLayout();

		cb = (CheckBox) findViewById(R.id.reg_read_p);
		cb.setChecked(true);
		a = AnimationUtils.loadAnimation(this, R.anim.shake_x);
		readP = (TextView) findViewById(R.id.reg_p_text_txt);
		readF = (TextView) findViewById(R.id.reg_f_text_txt);
		readP.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		readP.getPaint().setAntiAlias(true);
		readF.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		readF.getPaint().setAntiAlias(true);
		readF.setOnClickListener(this);
		readP.setOnClickListener(this);
		btnOk = (Button) findViewById(R.id.register_btn_ok);
		btnOk.setOnClickListener(this);
		name = (EditText) findViewById(R.id.register_edit_name);
		name.addTextChangedListener(new InputTextWatcher(
				R.id.register_edit_name));
		namebtn = (ImageView) findViewById(R.id.name_delete_btn);
		namebtn.setOnClickListener(this);
		passwd = (EditText) findViewById(R.id.register_edit_passwd);
		passwdbtn = (ImageView) findViewById(R.id.passwd_delete_btn);
		passwdbtn.setOnClickListener(this);
		passwd.addTextChangedListener(new InputTextWatcher(
				R.id.register_edit_passwd));
		eMail = (EditText) findViewById(R.id.register_edit_email);
		emailbtn = (ImageView) findViewById(R.id.email_delete_btn);
		emailbtn.setOnClickListener(this);
		eMail.addTextChangedListener(new InputTextWatcher(
				R.id.register_edit_email));
		// main.startAnimation(b);
		inviteCode = (EditText) findViewById(R.id.register_edit_invite);
		if (PreferencesUtils.getBoolean(this,null,Constants.vipActivity,false)){
			findViewById(R.id.register_invite_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.register_invite_line).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.register_invite_layout).setVisibility(View.GONE);
			findViewById(R.id.register_invite_line).setVisibility(View.GONE);
		}
		headpic = (ImageView) findViewById(R.id.register_client_icon);
		bindOldAccount = (LinearLayout) findViewById(R.id.register_bindoldaccount);
		bindOldAccountBtn = (Button) findViewById(R.id.bindoldaccount_btn);
		clientLayout = (RelativeLayout) findViewById(R.id.register_client_layout);
		if (type == BIND) {
			headpic.setVisibility(View.VISIBLE);
			eMail.setVisibility(View.VISIBLE);
			bindOldAccount.setVisibility(View.VISIBLE);
			clientLayout.setVisibility(View.VISIBLE);
			bindOldAccount.setOnClickListener(this);
			bindOldAccountBtn.setOnClickListener(this);
			showLoadingLayout();
			if (thirdInfo.type == ThirdPlatInfo.TYPE_SINA) {
				mSinaAuthManager.getUserInfo(this,
						new OnSinaUserInfoListener() {

							@Override
							public void OnGetSinaUserInfo(boolean succ,
									String name, String iconUrl) {
								if (succ) {
									thirdInfo.userName = name;
									thirdInfo.userIconUrl = iconUrl;
									updateHandler.sendEmptyMessage(0);
								} else {
									Toast.makeText(getApplicationContext(),
											"微博通信异常", Toast.LENGTH_SHORT)
											.show();
									finish();
								}
							}
						});
			} else if (thirdInfo.type == ThirdPlatInfo.TYPE_QQ) {
				AuthManager.getInstance(this).getPlatformInfo(SHARE_MEDIA.QQ,new UmLoginInfo(),
						new OnUMLoginListener() {

							@Override
							public void onUmLogin(SHARE_MEDIA plat,
									boolean loginSucc, UmLoginInfo loginInfo) {
								thirdInfo.userName = loginInfo.userName;
								thirdInfo.userIconUrl = loginInfo.userIconUrl;
								updateHandler.sendEmptyMessage(0);
							}
						});
			}

		}
	}

	SinaAuthManager mSinaAuthManager = new SinaAuthManager();

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("RegisterActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("RegisterActivity");
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		userName = name.getText().toString().trim();
		passWord = passwd.getText().toString().trim();
		userEMail = eMail.getText().toString().trim();

		switch (v.getId()) {
		case R.id.email_delete_btn:
			eMail.setText("");
			break;
		case R.id.name_delete_btn:
			name.setText("");
			break;
		case R.id.passwd_delete_btn:
			passwd.setText("");
			break;
		case R.id.reg_p_text_txt:
			Intent i = new Intent(this, WebBrowserActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("url", "file:///android_asset/tvfan_agreement.htm");
			i.putExtra("title", "电视粉使用协议");
			startActivity(i);
			break;
		case R.id.reg_f_text_txt:
			MobclickAgent.onEvent(this, "szpinglun");
			Intent intent = new Intent(this, StatementActivity.class);
			startActivity(intent);
			break;
		case R.id.register_btn_ok:
			if (TextUtils.isEmpty(userEMail)) {
				Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
				eMail.startAnimation(a);
			} else if (!AppUtil.isEmail(userEMail)) {
				Toast.makeText(this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
				eMail.startAnimation(a);
			} else if (TextUtils.isEmpty(userName)) {
				Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
				name.startAnimation(a);
			} else if (userName.length() > 20 || userName.length() < 1) {
				Toast.makeText(this, "请输入正确的用户名，1-20个字符", Toast.LENGTH_SHORT)
						.show();
				name.startAnimation(a);
			} else if (passWord.equals("")) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				passwd.startAnimation(a);
			} else if (!cb.isChecked()) {
				Toast.makeText(this, "请选择接受注册协议", Toast.LENGTH_SHORT).show();
				cb.startAnimation(a);
			} else {
				if (!StringUtils.containHanzi(passWord)
						&& passWord.length() <= 16 && passWord.length() > 5) {
					AppUtil.hideKeyoard(this);
					UserNow.current().name = userName;
					UserNow.current().passwd = passWord;
					UserNow.current().eMail = userEMail;
					UserNow.current().inviteCode = inviteCode.getText().toString().trim();
					if (type == NORMAL) {
						register();
					} else {
						UserNow.current().userType = 1;
						bindRegister();
					}
				} else if (StringUtils.containHanzi(passWord)) {
					Toast.makeText(RegisterActivity.this, "密码不能为汉字",
							Toast.LENGTH_SHORT).show();
				} else if (passWord.length() > 16 || passWord.length() < 6) {
					Toast.makeText(this, "请输入正确的密码，密码位数6-16位",
							Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case R.id.register_bindoldaccount:
		case R.id.bindoldaccount_btn:
			Intent bindIntent = new Intent(RegisterActivity.this,
					BindAccountActivity.class);
			bindIntent.putExtra("info", thirdInfo);
			startActivityForResult(bindIntent, LoginActivity.REQUESTCODE_REG);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			mSinaAuthManager.logout(this);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mSinaAuthManager.logout(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	// 注册
	private void register() {
		showLoadingLayout();
		VolleyUserRequest.register(this, this);
	}

	// 绑定注册
	private void bindRegister() {
		showLoadingLayout();
		VolleyUserRequest.bindAccount(thirdInfo, this, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			finish();
		}
		mSinaAuthManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void bindAccountResult(int errCode, int point, String msg) {
		allRegisterResult(errCode, msg);
	}

	@Override
	public void registerResult(int errCode, String errMsg) {
		allRegisterResult(errCode, errMsg);
	}

	private void allRegisterResult(int errCode, String errMsg) {
		hideLoadingLayout();
		if (JSONMessageType.SERVER_CODE_OK == errCode) {
			// MobclickAgent.onEvent(getApplicationContext(), "zhucechenggong");
			setResult(RESULT_OK);
			EventBus.getDefault().post(new UserInfoEvent());
			LocalInfo.SaveUserData(this, true);
//			new UshowManager(this).setUserInfo(UserNow.current());
//			ushowHandler();
			finish();
		} else {
			Toast.makeText(RegisterActivity.this, errMsg, Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void ushowHandler() {
		if (getIntent().getBooleanExtra("ushow", false)) {
			int ftype = getIntent().getIntExtra("ftype", 0);
			String fparam = "";
			if (ftype == 1) {
				fparam = getIntent().getStringExtra("fparam");
			}
//			UshowManager mUshowManager = new UshowManager(this);
//			mUshowManager.launch(getIntent(), ftype, fparam);
		}
	}

	/**
	 * 用户名、密码、email输入框检测
	 * 
	 * @author suma-hpb
	 * 
	 */
	class InputTextWatcher implements TextWatcher {

		private int resId;

		public InputTextWatcher(int resId) {
			this.resId = resId;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (resId == R.id.register_edit_name) {
				if (!TextUtils.isEmpty(name.getText().toString())) {
					namebtn.setVisibility(View.VISIBLE);
				} else {
					namebtn.setVisibility(View.GONE);
				}
			} else if (resId == R.id.register_edit_passwd) {
				if (!TextUtils.isEmpty(passwd.getText().toString())) {
					passwdbtn.setVisibility(View.VISIBLE);
				} else {
					passwdbtn.setVisibility(View.GONE);
				}
			} else if (resId == R.id.register_edit_email) {
				if (!TextUtils.isEmpty(eMail.getText().toString())) {
					emailbtn.setVisibility(View.VISIBLE);
				} else {
					emailbtn.setVisibility(View.GONE);
				}
			}

		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	private Handler updateHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideLoadingLayout();
			name.setText(thirdInfo.userName);
			loadImage(headpic, thirdInfo.userIconUrl, R.drawable.my_headpic);
		};
	};

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.register);
		VolleyHelper.cancelRequest(Constants.bindUser);
	}
}