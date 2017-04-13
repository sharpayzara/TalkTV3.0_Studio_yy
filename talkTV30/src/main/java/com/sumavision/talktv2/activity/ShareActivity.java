package com.sumavision.talktv2.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.share.OnUMShareListener;
import com.sumavision.talktv2.share.ShareManager;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.wxapi.SharePlatformActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * 分享页
 * 
 * @author suma-hpb
 * 
 */
public class ShareActivity extends Activity implements OnClickListener,
		OnUMShareListener {

	private long activityId;
	private String activityPic;
	private String programName;
	private String activityName;
	private int programId;
	private String content;
	private String title;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		activityId = getIntent().getLongExtra("activityId", 0);
		// totalTimes = getIntent().getIntExtra("totalTimes", 0);
		activityPic = getIntent().getStringExtra("activityPic");
		// playPic = getIntent().getStringExtra("playPic");
		programName = getIntent().getStringExtra("programName");
		activityName = getIntent().getStringExtra("activityName");
		programId = getIntent().getIntExtra("id", 0);
		content = getIntent().getStringExtra("content");
		ImageView sinaView = (ImageView) findViewById(R.id.tv_sina);
		ImageView wxView = (ImageView) findViewById(R.id.tv_wx);
		ImageView wxsView = (ImageView) findViewById(R.id.tv_wx_friends);
		ImageView qzoneView = (ImageView) findViewById(R.id.tv_qzone);

		boolean sina = getIntent().getBooleanExtra("sina_canShare", true);
		boolean wx = getIntent().getBooleanExtra("weixin_canShare", true);
		boolean wxFriends = getIntent()
				.getBooleanExtra("circle_canShare", true);
		boolean qzone = getIntent().getBooleanExtra("qzone_canShare", true);
		if (sina) {
			sinaView.setOnClickListener(this);
		} else {
			sinaView.setImageResource(R.drawable.share_weibo_pic_focus);
		}
		if (wx) {
			wxView.setOnClickListener(this);
		} else {
			wxView.setImageResource(R.drawable.share_friend_pic_focus);
		}
		if (wxFriends) {
			wxsView.setOnClickListener(this);
		} else {
			wxsView.setImageResource(R.drawable.share_group_pic_focus);
		}
		if (qzone) {
			qzoneView.setOnClickListener(this);
		} else {
			qzoneView.setImageResource(R.drawable.qzone_share_pressed);
		}
		findViewById(R.id.rlayout_share).setOnClickListener(this);


		if (activityId != 0) {
			targetUrl = "http://tvfan.cn/share/weixinActivity.action?client=1&activityId="
					+ activityId;
		} else {
			targetUrl = Constants.url + "web/mobile/shareProgram.action?subId="
					+ programId;
		}
		if (!TextUtils.isEmpty(programName)) {

			StringBuffer hintBuf = new StringBuffer();
			hintBuf.append("看片花、赢大奖，我参加了电视粉的《")
					.append(programName).append("》看预告片 送精美礼品活动，@电视粉 活动，天天有奖品！你也来试试手气吧！地址：");
			hintBuf.append(targetUrl);
			shareContent = hintBuf.toString();
			title = "看片花赢大奖  " + programName;
		} else {
			title = "看片花赢大奖  " + activityName;
			StringBuilder contentBuilder = new StringBuilder("");
			contentBuilder.append("看片花、赢大奖，我参加了电视粉的《")
					.append(activityName).append("》看预告片 送精美礼品活动，@电视粉 活动，天天有奖品！你也来试试手气吧！地址：");
			contentBuilder.append(targetUrl);
			shareContent = contentBuilder.toString();
		}


		if (!TextUtils.isEmpty(activityPic)) {
			sharePic = activityPic;
		}
		if (TextUtils.isEmpty(sharePic)) {
			shareImg = new UMImage(this, R.drawable.icon);
		} else {
			shareImg = new UMImage(this, sharePic);
		}
	}

	UMImage shareImg = null;
	private String targetUrl;
	private String shareContent;
	private String sharePic;

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ShareActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ShareActivity");
		super.onResume();
	}

	private void openShareActivity(int type) {
		Intent intent = new Intent();
		intent.setClass(this, SharePlatformActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("from", 1);
		intent.putExtra("targetUrl", targetUrl);
		intent.putExtra("activityId", activityId);
		intent.putExtra("activityPic", activityPic);
		intent.putExtra("programName", shareContent);
		startActivityForResult(intent, REQUEST_PLAT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlayout_share:
			finish();
			break;
		case R.id.tv_sina:
			openShareActivity(SharePlatformActivity.TYPE_SINA);
			break;
		case R.id.tv_wx:
			String share = shareContent;
			if (TextUtils.isEmpty(share)) {
				share = getString(R.string.share_weixin_message);
			}
			ShareManager.getInstance(this).weixinShare(true, targetUrl, title,
					share, shareImg, this);
			break;
		case R.id.tv_wx_friends:
			String wxshare = shareContent;
			if (TextUtils.isEmpty(wxshare)) {
				share = getString(R.string.share_weixin_message);
			}
			ShareManager.getInstance(this).weixinShare(false, targetUrl, title,
					wxshare, shareImg, this);
			break;
		case R.id.tv_qzone:
			String qzoneshare = shareContent;
			if (TextUtils.isEmpty(qzoneshare)) {
				share = getString(R.string.share_weixin_message);
			}
			ShareManager.getInstance(this).qzoneShare(title, qzoneshare,
					shareImg, targetUrl, this);
			break;
		default:
			break;
		}
	};

	private static final int REQUEST_PLAT = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_PLAT) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
			}
			finish();
		}
		UMSsoHandler ssoHandler = ShareManager.getInstance(this).getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ShareManager.getInstance(this).cleanListener();
	}

	@Override
	public void umShareResult(String platform, boolean shareSucc, boolean cancel) {
		String key = "";
		if (shareSucc) {
			showToast("成功", platform);
			setResult(RESULT_OK);
			if (activityId > 0) {
				if (SHARE_MEDIA.WEIXIN.name().equals(platform)) {
					key = ActivityActivity.ACTIVITY_PREFERENCE_KEY_WEIXIN;
				} else if (SHARE_MEDIA.WEIXIN_CIRCLE.name().equals(platform)) {
					key = ActivityActivity.ACTIVITY_PREFERENCE_KEY_CIRCLE;
				} else if (SHARE_MEDIA.QZONE.name().equals(platform)) {
					key = ActivityActivity.ACTIVITY_PREFERENCE_KEY_QZONE;
				}
				if (!PreferencesUtils.getBoolean(getApplicationContext(),
						ActivityActivity.ACTIVITY_PREFERENCE_NAME, key + "_"
								+ activityId)) {
					PreferencesUtils.putBoolean(getApplicationContext(),
							ActivityActivity.ACTIVITY_PREFERENCE_NAME, key
									+ "_" + activityId, true);
					increaseChance();
				}
			}
		} else {
			if (cancel) {
				showToast("取消", platform);
			} else {
				showToast("失败", platform);
			}
		}
		finish();
	}

	private void showToast(String success, String platform) {
		if (SHARE_MEDIA.WEIXIN.name().equals(platform)) {
			Toast.makeText(this, "微信分享" + success, Toast.LENGTH_SHORT).show();
		} else if (SHARE_MEDIA.WEIXIN_CIRCLE.name().equals(platform)) {
			Toast.makeText(this, "朋友圈分享" + success, Toast.LENGTH_SHORT).show();
		} else if (SHARE_MEDIA.QZONE.name().equals(platform)) {
			Toast.makeText(this, "QQ空间分享" + success, Toast.LENGTH_SHORT).show();
		}
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
}
