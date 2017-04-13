package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.andexert.library.RippleView;
import com.andexert.library.RippleView.OnAnimationEndListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.SinaData;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UmLoginInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.GetAddressParser;
import com.sumavision.talktv2.http.json.GetAddressRequest;
import com.sumavision.talktv2.http.json.UploadPushInfoParser;
import com.sumavision.talktv2.http.json.UploadPushInfoRequest;
import com.sumavision.talktv2.http.listener.OnBindLogInListener;
import com.sumavision.talktv2.http.listener.OnLogInListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.service.PushUtils;
import com.sumavision.talktv2.share.AccessTokenKeeper;
import com.sumavision.talktv2.share.AuthManager;
import com.sumavision.talktv2.share.OnUMLoginListener;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager.OnSinaBindLoginListener;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.tencent.open.utils.SystemUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;
import sdk.meizu.auth.MzAuthenticator;
import sdk.meizu.auth.OAuthError;
import sdk.meizu.auth.OAuthToken;
import sdk.meizu.auth.callback.ImplictCallback;

/**
 * @author 郭鹏
 * @createTime 2012-5-31
 * @description 登录界面
 * @changeLog
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnLogInListener, OnBindLogInListener {

	private Button btnOk;
	private EditText name, passwd;
	private ImageView ivDelete, psdDelete;
	private TextView forgetpsd;
	private Animation leftRightAnim;

	private final int LOGIN = 1;
	private final int REGISTER = 5;
	public static final int REQUESTCODE_REG = 0;

	private SinaAuthManager mSinaAuthManager;
	private int funcFlag = 0;
	private final int SINA = 1;
	private String userName = "";
	private String passWord = "";
	MzAuthenticator mAuthenticator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getSupportActionBar().setTitle(R.string.login);
		initView();
		mSinaAuthManager = new SinaAuthManager();
		findViewById(R.id.login_btn_qq).setOnClickListener(this);
		findViewById(R.id.login_btn_bottom_flyme).setOnClickListener(this);
		RippleView loginRipple = (RippleView) findViewById(R.id.rect);
		loginRipple.setOnAnimationEndListener(new OnAnimationEndListener() {

			@Override
			public void OnAnimationEnd() {
				onClick(btnOk);
			}
		});
		mAuthenticator = new MzAuthenticator("q61s0itVKRK9Q8Fue7zE", "http://tvfan.cn");
	}

	TextActionProvider loginAction;

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("LoginActivity");
		super.onPause();
	}

	@Override
	public void finish() {
		super.finish();
		if (getIntent().hasExtra("where") && getIntent().getStringExtra("where").equals("WebPageActivity")){
			Intent awardIntent = new Intent();
			if (UserNow.current().userID>0){
				awardIntent.setClass(this, WebPageActivity.class);
				awardIntent.putExtra("url", Constants.host.substring(0,Constants.host.lastIndexOf("/")+1)+"newweb/deckGame/game.jsp");
				awardIntent.putExtra("title","每日抽奖");
				awardIntent.putExtra("notice", true);
			} else {
				awardIntent.setClass(this,SlidingMainActivity.class);
			}
			startActivity(awardIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.login, menu);
		loginAction = (TextActionProvider) menu.findItem(R.id.action_login)
				.getActionProvider();
		if (loginAction != null){
			loginAction.setShowText(R.string.register);
			loginAction.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent iR = new Intent(LoginActivity.this,
							RegisterActivity.class);
					iR.putExtra("type", RegisterActivity.NORMAL);
					startActivityForResult(iR, REQUESTCODE_REG);
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}

	ThirdPlatInfo thirdInfo = new ThirdPlatInfo();

	@Override
	public void onClick(View v) {

		userName = name.getText().toString().trim();
		passWord = passwd.getText().toString().trim();

		switch (v.getId()) {
		case R.id.login_btn_qq:
			if (!SystemUtils.checkMobileQQ(this)) {
				Toast.makeText(this, "未安装QQ客户端，请先安装QQ", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			AuthManager.getInstance(LoginActivity.this).login(SHARE_MEDIA.QQ, new OnUMLoginListener() {
				@Override
				public void onUmLogin(SHARE_MEDIA plat, boolean loginSucc, UmLoginInfo loginInfo) {
					if (loginSucc){
						thirdInfo.userName = loginInfo.userName;
						thirdInfo.userIconUrl = loginInfo.userIconUrl;
						thirdInfo.token = loginInfo.accessToken;
						thirdInfo.openId = loginInfo.openid;
						thirdInfo.type = ThirdPlatInfo.TYPE_QQ;
						AccessTokenKeeper.writeQQInfo(
								LoginActivity.this, thirdInfo.openId,
								thirdInfo.token, 0);
						bindLoginhandler.sendEmptyMessage(0);
					} else {
						Toast.makeText(getApplicationContext(),
								"QQ授权失败", Toast.LENGTH_SHORT).show();
					}
				}
			});
			break;
		case R.id.login_btn_bottom_sina:
			funcFlag = SINA;
			mSinaAuthManager.auth(this, new OnSinaBindLoginListener() {

				@Override
				public void OnSinaBindSucceed() {
					thirdInfo.userId = SinaData.id;
					thirdInfo.type = ThirdPlatInfo.TYPE_SINA;
					thirdInfo.token = SinaData.accessToken;
					mSinaAuthManager.getUserInfo(LoginActivity.this, new SinaAuthManager.OnSinaUserInfoListener() {
						@Override
						public void OnGetSinaUserInfo(boolean getInfoSucc, String name, String iconUrl) {
							if (getInfoSucc){
								thirdInfo.userName = name;
								thirdInfo.userIconUrl = iconUrl;
								bindLoginhandler.sendEmptyMessage(0);
							}
						}
					});
				}
			});
			break;
		case R.id.login_btn_ok:
			if (TextUtils.isEmpty(userName)) {
				Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
				name.startAnimation(leftRightAnim);
			} else if (TextUtils.isEmpty(passWord)) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				passwd.startAnimation(leftRightAnim);
			} else {
				UserNow.current().name = userName;
				UserNow.current().passwd = passWord;
				funcFlag = LOGIN;
				showLoadingLayout();
				VolleyUserRequest.login(this, this);
				AppUtil.hideKeyoard(this);
			}
			break;
		case R.id.login_delete_btn:
			name.setText("");
			break;
		case R.id.psd_delete_btn:
			passwd.setText("");
			break;
		case R.id.login_forget_psd:
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, ForgetInitActivity.class);
			startActivity(intent);
			break;
		case R.id.login_btn_bottom_flyme:
			mAuthenticator.requestImplictAuth(LoginActivity.this,"uc_basic_info", new ImplictCallback() {
				@Override
				public void onError(OAuthError error) {
					String errorMsg = "OAuthError: " + error.getError();
//					Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onGetToken(OAuthToken token) {
					String oauthTokenResult = token.getAccessToken();
					thirdInfo.token = oauthTokenResult;
					thirdInfo.type = ThirdPlatInfo.TYPE_FLYME;
					String url = "https://open-api.flyme.cn/v2/me?access_token="+oauthTokenResult;
					VolleyQueueManage.getRequestQueue().add(new StringRequest(url, new Response.Listener<String>() {
						@Override
						public void onResponse(String response) {
							JSONObject temp;
							try {
								temp = new JSONObject(response);
								if (temp.optString("code").equals("200")){
									temp = temp.optJSONObject("value");
									thirdInfo.userIconUrl = temp.optString("icon");
									thirdInfo.userName = temp.optString("nickname");
									thirdInfo.openId = temp.optString("openId");
									bindLoginhandler.sendEmptyMessage(0);
								} else {
									ToastHelper.showToast(LoginActivity.this,"获取信息失败");
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

						}
					}));
//					Toast.makeText(LoginActivity.this, oauthTokenResult, Toast.LENGTH_SHORT).show();
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (funcFlag == SINA) {
			funcFlag = 0;
			mSinaAuthManager.onActivityResult(requestCode, resultCode, data);
		} else {
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case REQUESTCODE_REG:
					if (resultCode == RESULT_OK) {
						funcFlag = REGISTER;
						LocalInfo.SaveUserData(this, true);
						setResult(RESULT_OK);
						finish();
					}
					break;
				default:
					break;
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}

	private void initView() {
		findViewById(R.id.login_btn_bottom_sina).setOnClickListener(this);

		btnOk = (Button) findViewById(R.id.login_btn_ok);
		// btnOk.setOnClickListener(this);
		name = (EditText) findViewById(R.id.login_edit_name);
		ivDelete = (ImageView) findViewById(R.id.login_delete_btn);
		ivDelete.setOnClickListener(this);
		ivDelete.setVisibility(View.GONE);
		psdDelete = (ImageView) findViewById(R.id.psd_delete_btn);
		psdDelete.setOnClickListener(this);
		psdDelete.setVisibility(View.GONE);
		name.addTextChangedListener(new InputTextWatcher(R.id.login_edit_name));
		passwd = (EditText) findViewById(R.id.login_edit_passwd);
		passwd.addTextChangedListener(new InputTextWatcher(
				R.id.login_edit_passwd));
		passwd.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					btnOk.performClick();
				}

				return false;
			}
		});

		forgetpsd = (TextView) findViewById(R.id.login_forget_psd);
		forgetpsd.setOnClickListener(this);
		leftRightAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

		userName = PreferencesUtils.getString(this, "userInfo", "username");
		passWord = PreferencesUtils.getString(this, "userInfo", "password");

		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
			name.setText(userName);
			passwd.setText(passWord);
		} else {
			name.setText("");
			passwd.setText("");
		}

		initLoadingLayout();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("LoginActivity");
		super.onResume();
		if (UserNow.current().userID != 0) {
			LocalInfo.SaveUserData(this, true);
			setResult(RESULT_OK);
			finish();
		}
	}

	private static final int UNBIND = 2;// 未绑定电视粉帐号

	@Override
	public void bindLogInResult(int errCode, String errmsg, UserNow user) {
		switch (errCode) {
		case UNBIND:
			Intent iR = new Intent(this, RegisterActivity.class);
			iR.putExtra("type", RegisterActivity.BIND);
			iR.putExtra("info", thirdInfo);
			startActivityForResult(iR, REQUESTCODE_REG);
			break;
		case JSONMessageType.SERVER_CODE_ERROR:
			DialogUtil.alertToast(getApplicationContext(), errmsg);
			break;
		case JSONMessageType.SERVER_CODE_OK:
			MobclickAgent.onEvent(getApplicationContext(), "weibodenglu");
			updateBaiduBindInfo();
			EventBus.getDefault().post(new UserInfoEvent());
			LocalInfo.SaveUserData(this, true);
//			new UshowManager(this).setUserInfo(UserNow.current());
//			ushowHandler();
			checkAddressRequest();
			break;
		default:
			break;
		}
		hideLoadingLayout();
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

	private void updateBaiduBindInfo() {
		UploadPushInfoParser uploadInfoparser = new UploadPushInfoParser();
		String bUserId = PushUtils.getBindUserId(this);
		String bchannelId = PushUtils.getBindChannelId(this);
		if (TextUtils.isEmpty(bUserId)) {
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_API_KEY,
					PushUtils.getMetaValue(this, "api_key"));
		} else {
			VolleyHelper.post(new UploadPushInfoRequest(bUserId, bchannelId,
					UserNow.current().userID).make(), new ParseListener(
					uploadInfoparser) {
				@Override
				public void onParse(BaseJsonParser parser) {

				}
			}, null);
		}
	}

	@Override
	public void loginResult(int errCode, int point, String errMsg) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (TextUtils.isEmpty(errMsg)) {
				errMsg = "登录失败!";
			}
			DialogUtil.alertToast(getApplicationContext(), errMsg);
			break;
		case JSONMessageType.SERVER_CODE_OK:
			updateBaiduBindInfo();
			EventBus.getDefault().post(new UserInfoEvent());
			LocalInfo.SaveUserData(this, true);
//			if (UserNow.current().showAlert){
//				startActivity(new Intent(LoginActivity.this,UserInfoEditActivity.class));
//			}
			checkAddressRequest();
			break;
		default:
			break;
		}
		hideLoadingLayout();
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

	/**
	 * 用户名、密码输入框检测
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
			if (resId == R.id.login_edit_name) {
				if (!TextUtils.isEmpty(name.getText().toString())) {
					ivDelete.setVisibility(View.VISIBLE);
				} else {
					ivDelete.setVisibility(View.GONE);
				}
			} else {
				if (!TextUtils.isEmpty(passwd.getText().toString())) {
					psdDelete.setVisibility(View.VISIBLE);
				} else {
					psdDelete.setVisibility(View.GONE);
				}
			}

		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	private void bindLogin() {
		showLoadingLayout();
		VolleyUserRequest.bindLogIn(thirdInfo, this, this);
	}

	private Handler bindLoginhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			bindLogin();
		};
	};

}