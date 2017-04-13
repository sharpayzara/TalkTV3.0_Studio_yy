package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.BindAddParser;
import com.sumavision.talktv2.http.json.BindAddRequest;
import com.sumavision.talktv2.http.json.BindListParser;
import com.sumavision.talktv2.http.json.BindListRequest;
import com.sumavision.talktv2.http.listener.OnBindDeleteListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.share.AccessTokenKeeper;
import com.sumavision.talktv2.share.AuthManager;
import com.sumavision.talktv2.share.OnUMAuthListener;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager.OnSinaBindLoginListener;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.tencent.open.utils.SystemUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import sdk.meizu.auth.MzAuthenticator;
import sdk.meizu.auth.OAuthError;
import sdk.meizu.auth.OAuthToken;
import sdk.meizu.auth.callback.ImplictCallback;

/**
 * 绑定平台帐号
 * 
 * @author suma-hpb
 * 
 */
public class PlatformBindActivity extends BaseActivity implements
		OnClickListener, OnBindDeleteListener {

	SinaAuthManager sinaAuth;
	Oauth2AccessToken sinaToken;
	TextView sinaBindTxt, qqBindTxt, flymeBindTxt;

	RelativeLayout main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_platform_bind);
		main = (RelativeLayout) findViewById(R.id.content_layout);
		main.setVisibility(View.GONE);
		getSupportActionBar().setTitle(getString(R.string.userinfo_bind));
		findViewById(R.id.rlayout_sina).setOnClickListener(this);
		findViewById(R.id.rlayout_qq).setOnClickListener(this);
		findViewById(R.id.rlayout_weixin).setOnClickListener(this);
		findViewById(R.id.rlayout_flyme).setOnClickListener(this);
		sinaAuth = new SinaAuthManager();
		sinaBindTxt = (TextView) findViewById(R.id.tv_bind_unbind);
		qqBindTxt = (TextView) findViewById(R.id.tv_qq_bind_status);
		flymeBindTxt = (TextView) findViewById(R.id.tv_flyme_bind_status);
		// sinaToken = AccessTokenKeeper.readAccessToken(this);
		// if (sinaToken.isSessionValid()) {
		// sinaBindTxt.setText(R.string.to_unbind);
		// }
		initLoadingLayout();
		bindList = new ArrayList<ThirdPlatInfo>();
		final BindListParser bindParser = new BindListParser();
		VolleyHelper.post(new BindListRequest().make(), new ParseListener(
				bindParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (bindParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					main.setVisibility(View.VISIBLE);
					bindList = bindParser.bindlist;
					updateUi();
				} else {
					showErrorLayout();
				}

			}
		}, null);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("PlatformBindActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("PlatformBindActivity");
		super.onPause();
	}

	boolean sinaBind,flymeBind;
	ThirdPlatInfo sinnDada = new ThirdPlatInfo();
	ThirdPlatInfo qqInfo = new ThirdPlatInfo();
	ThirdPlatInfo flymeInfo = new ThirdPlatInfo();

	private void updateUi() {
		for (int i = 0, len = bindList.size(); i < len; i++) {
			ThirdPlatInfo data = bindList.get(i);
			if (data.type == ThirdPlatInfo.TYPE_SINA) {
				sinnDada = data;
				sinaBind = true;
				sinaBindTxt.setText(R.string.to_unbind);
				if (!TextUtils.isEmpty(data.expiresIn)) {
					sinaToken = new Oauth2AccessToken(data.token,
							data.expiresIn);
					AccessTokenKeeper.writeAccessToken(this, sinaToken);
				} else {
					sinaToken = AccessTokenKeeper.readAccessToken(this);
				}
			} else if (data.type == ThirdPlatInfo.TYPE_QQ) {
				qqBindTxt.setText(R.string.to_unbind);
				qqInfo = data;
				qqbind = true;
				AccessTokenKeeper.writeQQInfo(this, data.openId, data.token, 0);
			} else if (data.type == ThirdPlatInfo.TYPE_FLYME) {
				flymeBindTxt.setText(R.string.to_unbind);
				flymeInfo = data;
				flymeBind = true;
			}
		}
	}

	boolean qqbind;
	private ArrayList<ThirdPlatInfo> bindList;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlayout_sina:
			sinaToken = AccessTokenKeeper.readAccessToken(this);
			info = sinnDada;
			if (sinaBind) {
				if (sinaToken.isSessionValid()) {
					sinaAuth.logout(this);
				}
				showLoadingLayout();
				VolleyUserRequest.bindDelete(this, this, sinnDada.bindId);
			} else {
				sinaAuth.auth(this, new OnSinaBindLoginListener() {

					@Override
					public void OnSinaBindSucceed() {
						sinaSuccHandler.sendEmptyMessage(0);
					}
				});
			}
			break;
		case R.id.rlayout_qq:
			info=qqInfo;
			if (qqbind) {
				VolleyUserRequest.bindDelete(this, this, qqInfo.bindId);
			} else {
				if (!SystemUtils.checkMobileQQ(this)) {
					Toast.makeText(this, "未安装QQ客户端，请先安装QQ", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				AuthManager.getInstance(this).auth(SHARE_MEDIA.QQ,
						new OnUMAuthListener() {

							@Override
							public void umAuthResult(SHARE_MEDIA platform,
									boolean authSucc, String openId,
									String token) {
								if (authSucc) {
									info.userId = openId;
									info.type = ThirdPlatInfo.TYPE_QQ;
									info.token = token;
									bindAdd();
								} else {
									Toast.makeText(getApplicationContext(),
											"qq授权异常", Toast.LENGTH_SHORT)
											.show();
								}

							}
						});
			}
			break;
		case R.id.rlayout_weixin:
			Toast.makeText(this, R.string.wait_for_open, Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.rlayout_flyme:
			info = flymeInfo;
			if (flymeBind){
				VolleyUserRequest.bindDelete(this, this, flymeInfo.bindId);
			} else {
				bindFlyme();
			}
			break;
		}
	}

	public void bindFlyme(){
		MzAuthenticator mAuthenticator = new MzAuthenticator("q61s0itVKRK9Q8Fue7zE", "http://tvfan.cn");
		mAuthenticator.requestImplictAuth(PlatformBindActivity.this,"uc_basic_info", new ImplictCallback() {
			@Override
			public void onError(OAuthError error) {
				String errorMsg = "OAuthError: " + error.getError();
//					Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onGetToken(OAuthToken token) {
				String oauthTokenResult = token.getAccessToken();
				info.token = oauthTokenResult;
				info.type = ThirdPlatInfo.TYPE_FLYME;
				info.expiresIn = token.getExpireIn();
				String url = "https://open-api.flyme.cn/v2/me?access_token="+oauthTokenResult;
				VolleyQueueManage.getRequestQueue().add(new StringRequest(url, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject temp;
						try {
							temp = new JSONObject(response);
							if (temp.optString("code").equals("200")){
								temp = temp.optJSONObject("value");
								info.userIconUrl = temp.optString("icon");
								info.userName = temp.optString("nickname");
								info.openId = temp.optString("openId");
								info.userId = temp.optString("openId");
								bindAdd();
							} else {
								ToastHelper.showToast(PlatformBindActivity.this, "获取信息失败");
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
			}
		});
	}
	Handler sinaSuccHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Oauth2AccessToken sinaInfo = AccessTokenKeeper
					.readAccessToken(PlatformBindActivity.this);
			info.userId = sinaInfo.getUid();
			info.type = ThirdPlatInfo.TYPE_SINA;
			info.token = sinaInfo.getToken();
			bindAdd();
		};
	};
	ThirdPlatInfo info = new ThirdPlatInfo();

	BindAddParser addParser = new BindAddParser();

	private void bindAdd() {
		showLoadingLayout();
		VolleyHelper.post(new BindAddRequest(info).make(), new ParseListener(
				addParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (addParser.errCode != JSONMessageType.SERVER_CODE_ERROR) {
					if (UserNow.current().point > 0) {
//						if (UserNow.current().action == null) {
//							UserNow.current().action = "";
//						}
//						String vipStr="";
//						if (UserNow.current().vipIncPoint>0){
//							vipStr = "VIP加成"+UserNow.current().vipIncPoint+"积分";
//						}
//						DialogUtil.updateScoreToast(UserNow.current().action + " +"
//								+ (UserNow.current().point) + "积分"+vipStr);
						UserNow.current().totalPoint = addParser.userInfo.totalPoint;
						EventBus.getDefault().post(new UserInfoEvent());
					}
					bindList = addParser.bindList;
					String msg = "";
					if (info.type == ThirdPlatInfo.TYPE_SINA) {
						if (addParser.sinaIndex >= 0) {
							sinnDada = bindList.get(addParser.sinaIndex);
						}
						sinaBind = true;
						msg = getString(R.string.sina_bind_succeed);
						sinaBindTxt.setText(R.string.to_unbind);
					} else if (info.type == ThirdPlatInfo.TYPE_QQ) {
						if (addParser.qqIndex >= 0) {
							qqInfo = bindList.get(addParser.qqIndex);
						}
						qqbind = true;
						msg = getString(R.string.qq_bind_succeed);
						qqBindTxt.setText(R.string.to_unbind);
					} else if (info.type == ThirdPlatInfo.TYPE_FLYME){
						if (addParser.flymeIndex >= 0) {
							flymeInfo = bindList.get(addParser.flymeIndex);
						}
						flymeBind = true;
						msg = getString(R.string.flyme_bind_succeed);
						flymeBindTxt.setText(R.string.to_unbind);
					}
					Toast.makeText(getApplicationContext(), msg,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), addParser.errMsg,
							Toast.LENGTH_SHORT).show();
				}

			}
		}, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		sinaAuth.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void bindDeleteResult(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			String msg = "";
			if (info.type == ThirdPlatInfo.TYPE_SINA) {
				msg = getString(R.string.sina_unbind_succeed);
				sinaBind = false;
				sinaBindTxt.setText(R.string.to_bind);
				AccessTokenKeeper.clear(this);
			} else if (info.type == ThirdPlatInfo.TYPE_QQ) {
				qqbind = false;
				msg = getString(R.string.qq_unbind_succeed);
				qqBindTxt.setText(R.string.to_bind);
			} else if (info.type == ThirdPlatInfo.TYPE_FLYME){
				flymeBind = false;
				msg = getString(R.string.flyme_unbind_succeed);
				flymeBindTxt.setText(R.string.to_bind);
			}
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.bindList);
		VolleyHelper.cancelRequest(Constants.bindDelete);
		VolleyHelper.cancelRequest(Constants.bindAdd);
	}
}
