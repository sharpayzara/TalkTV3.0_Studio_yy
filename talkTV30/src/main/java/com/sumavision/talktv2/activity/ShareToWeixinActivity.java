package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.SharetoWeixinAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.share.OnUMShareListener;
import com.sumavision.talktv2.share.ShareManager;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

/**
 * @author jianghao
 * @version 2.2
 * @createTime 2013-4-8
 * @description 分享到微信
 * @changLog
 */

public class ShareToWeixinActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private String titleName;
	private String picUrl;
	private String activityPic = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_share_weixin);
		getExtra();
		initViews();
		setListeners();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("play-ShareToWeixinActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("play-ShareToWeixinActivity");
		super.onResume();
	}

	private void getExtra() {
		Intent intent = getIntent();
		titleName = intent.getStringExtra("titleName");
		picUrl = intent.getStringExtra("picUrl");
		activityPic = intent.getStringExtra("activityPic");
	}

	private ListView listView;

	private void initViews() {
		listView = (ListView) findViewById(R.id.listView);
		ArrayList<String> list = new ArrayList<String>();
		list.add(getString(R.string.share_to_friend));
		list.add(getString(R.string.share_to_circle));
		listView.setAdapter(new SharetoWeixinAdapter(this, list));
	}

	private void setListeners() {
		findViewById(R.id.share_layout).setOnClickListener(this);
		findViewById(R.id.cancel).setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_layout:
		case R.id.cancel:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			sendWeixinMessage(true);
			break;
		case 1:
			sendWeixinMessage(false);
			break;
		default:
			break;
		}
		// finish();
	}

	private void sendWeixinMessage(boolean toFriend) {
		// String description = titleName == null ?
		// getString(R.string.share_default_title)
		// : titleName;
		String description = getString(R.string.share_live_message, titleName);
		MobclickAgent.onEvent(getApplicationContext(), "weixin");
		String imageUrl = "";
		UMImage shareImage = null;
		if (StringUtils.isNotEmpty(activityPic)) {
			int index = activityPic.lastIndexOf(".jpg");
			if (index < 0) {
				index = activityPic.lastIndexOf(".png");
			}
			imageUrl = activityPic.substring(0, index) + Constants.PIC_SMALL;
		} else if (StringUtils.isNotEmpty(picUrl)) {
			StringBuilder proURL = new StringBuilder(picUrl);
			if (!picUrl.contains(".jpg")) {
				proURL.append("m.jpg");
			}
			imageUrl = proURL.toString();
		}
		if (TextUtils.isEmpty(imageUrl)) {
			shareImage = new UMImage(this, R.drawable.icon);
		} else {
			shareImage = new UMImage(this, imageUrl);
		}
		int playType = getIntent().getIntExtra("playType", 0);
		final String programId = getIntent().getIntExtra("id", 0) + "";
		// String path = getIntent().getStringExtra("path");
		StringBuilder contentUrl = new StringBuilder(Constants.url);
		if (playType == 4) {
			contentUrl = new StringBuilder(
					"http://tvfan.cn/share/weixinActivity.action?client=1&activityId=");
			contentUrl.append(getIntent().getStringExtra("activityId"));
		} else {
			// contentUrl.append("/share/weixinVideo.action?playType=")
			// .append(playType).append("&programId=").append(programId)
			// .append("&playUrl=").append(path);
			if (playType == 1) {
				contentUrl = new StringBuilder("http://tvfan.cn");
			} else {
				contentUrl.append("web/mobile/shareProgram.action?subId=")
						.append(programId);
			}
		}
		ShareManager.getInstance(this).weixinShare(toFriend,
				contentUrl.toString(), getString(R.string.app_name) + "分享",
				description, shareImage, new OnUMShareListener() {
					@Override
					public void umShareResult(String platform,
							boolean shareSucc, boolean cancel) {
						if (!shareSucc) {
							Toast.makeText(ShareToWeixinActivity.this,
									R.string.share_failed, Toast.LENGTH_SHORT)
									.show();
						}
						if (UserNow.current().userID > 0) {
							shareRequest(Integer.parseInt(programId));
						}
						finish();
					}
				});

	}

	ResultParser shareParser = new ResultParser();

	private void shareRequest(int programId) {
		VolleyHelper.post(new ShareRequest(ShareRequest.TYPE_PROGRAM,
				(int) programId).make(), new ParseListener(shareParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
//				if (shareParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//					if (parser.userInfo.point > 0) {
//						UserNow.current().setTotalPoint(
//								parser.userInfo.totalPoint,parser.userInfo.vipIncPoint);
//					}
//				}
			}
		}, null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		UMSsoHandler ssoHandler = ShareManager.getInstance(this).getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void finish() {
		super.finish();
		ShareManager.getInstance(this).cleanListener();
		VolleyHelper.cancelRequest(Constants.shareAnything);
	}

}
