package com.sumavision.talktv2.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.BaseActivity;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.share.AccessTokenKeeper;
import com.sumavision.talktv2.share.sina.SinaAuthManager;
import com.sumavision.talktv2.share.sina.SinaAuthManager.OnSinaBindLoginListener;
import com.sumavision.talktv2.share.sina.SinaShareManager;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 微博、微信分享
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SharePlatformActivity extends BaseActivity implements
		OnClickListener {
	public static final int TYPE_SINA = 1;
	public static final int TYPE_WEIXIN = 2;
	public static final int TYPE_CIRCLE = 3;
	private int type;
	String videopath;
	int from;
	private RelativeLayout share_activity_rel;

	String programShareMsg;
	String activityName;
	String activityContent;
	String pic;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getExtra();
		getSupportActionBar().setTitle(R.string.navigator_activity_weibo);
		setContentView(R.layout.activity_share_weibo);
		share_activity_rel = (RelativeLayout) findViewById(R.id.share_activity_rel);
		share_activity_rel.setVisibility(View.GONE);
		initViews();

		InputMethodManager inputManager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.showSoftInput(edit, 0);
		if (!TextUtils.isEmpty(programName)) {
			programShareMsg = programName;
			edit.setText(programName);
		}
		if (type == TYPE_SINA) {
			share_activity_rel.setVisibility(View.VISIBLE);
			if (TextUtils.isEmpty(programName)) {
				edit.setText(R.string.share_sina_message);
			}
			msinaAuthManager = new SinaAuthManager();
			mSinaShareManager = new SinaShareManager(this,
					new SinaShareManager.OnSinaShareListener() {
						@Override
						public void onSinaShare(boolean succeed) {
							setResult(Activity.RESULT_OK);
							if (succeed) {
								Toast.makeText(SharePlatformActivity.this, "新浪微博分享成功", Toast.LENGTH_SHORT).show();
								EventBus.getDefault().post(new EventMessage("video_share_sina"));
							} else {
								Toast.makeText(SharePlatformActivity.this, "新浪微博分享失败", Toast.LENGTH_SHORT).show();
							}
							if (activityId > 0) {
								if (!PreferencesUtils
										.getBoolean(
												getApplicationContext(),
												ActivityActivity.ACTIVITY_PREFERENCE_NAME,
												ActivityActivity.ACTIVITY_PREFERENCE_KEY_SINA
														+ "_" + activityId)) {
									PreferencesUtils
											.putBoolean(
													getApplicationContext(),
													ActivityActivity.ACTIVITY_PREFERENCE_NAME,
													ActivityActivity.ACTIVITY_PREFERENCE_KEY_SINA
															+ "_" + activityId,
													true);
									increaseChance();
								}
							}
							finish();
						}
					});
			mSinaShareManager.init(arg0);
		}

	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("SharePlatformActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("SharePlatformActivity");
		super.onResume();
	}

	SinaAuthManager msinaAuthManager;
	SinaShareManager mSinaShareManager;
	private EditText edit;

	private void initViews() {
		findViewById(R.id.send).setOnClickListener(this);
		findViewById(R.id.cancel1).setOnClickListener(this);
		edit = (EditText) findViewById(R.id.text);
	}

	private long activityId = 0;
//	private String activityPic = "";
	private String programName;
	private String targetUrl;

	private void getExtra() {
		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		videopath = intent.getStringExtra("videoPath");
		from = intent.getIntExtra("from", 0);
		programName = intent.getStringExtra("programName");
		pic = intent.getStringExtra("activityPic");
		targetUrl = intent.getStringExtra("targetUrl");
		if (intent.hasExtra("activityId"))
			activityId = intent.getLongExtra("activityId", 0);
		// if (intent.hasExtra("activityPic"))
		// activityPic = intent.getStringExtra("activityPic");
	}

	private String mInfo;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel1:
			setResult(RESULT_CANCELED);
			finish();
			break;
		case R.id.send:
			switch (type) {
			case TYPE_SINA:
				String s = edit.getText().toString();
				if (StringUtils.isNotEmpty(s)) {
					mInfo = s;
				} else {
					mInfo = getString(R.string.share_sina_message);
					if (TextUtils.isEmpty(programName)) {
						s = mInfo;
					} else {
						s = programShareMsg;
					}
				}
				Oauth2AccessToken token = AccessTokenKeeper
						.readAccessToken(this);
				if (token.isSessionValid()) {
					precessWeibo(s);
				} else {
					getSinaAuth();
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}

	private void precessWeibo(final String content) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.loadImage(pic, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				if (!TextUtils.isEmpty(pic) && pic.equals("vip_info_share")){
					mSinaShareManager.share(content, BitmapFactory.decodeResource(getResources(),R.drawable.icon));
				} else {
					mSinaShareManager.share(content, null);
				}
			}
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				mSinaShareManager.share(content, loadedImage);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	private void getSinaAuth() {
		msinaAuthManager.auth(this, new OnSinaBindLoginListener() {

			@Override
			public void OnSinaBindSucceed() {
				precessWeibo(mInfo);
			}
		});
	}

	private void increaseChance() {
		int chanceCount = PreferencesUtils.getInt(getApplicationContext(),
				ActivityActivity.ACTIVITY_PREFERENCE_NAME,
				ActivityActivity.ACTIVITY_PREFERENCE_KEY + "_" + activityId);

		chanceCount++;
		PreferencesUtils.putInt(getApplicationContext(),
				ActivityActivity.ACTIVITY_PREFERENCE_NAME,
				ActivityActivity.ACTIVITY_PREFERENCE_KEY + "_" + activityId,
				chanceCount);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		msinaAuthManager.onActivityResult(arg0, arg1, arg2);
		super.onActivityResult(arg0, arg1, arg2);
	}
}
