package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.suamvison.net.JSONMessageType;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.AcceptInviteRequest;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.InviteInfoParser;
import com.sumavision.talktv2.http.json.InviteInfoRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.ShareRequest;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.share.OnUMShareListener;
import com.sumavision.talktv2.share.ShareManager;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.wxapi.SharePlatformActivity;
import com.tencent.open.utils.SystemUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;

import de.greenrobot.event.EventBus;

/**
 * 
 * @description VIP信息页面
 * 
 */
public class MyVipInfoActivity extends BaseActivity implements View.OnClickListener{
	private TextView countText,totalDayText,leftDayText;
	private ImageView img1,img2,img3,img4;
	private TextView rulesText,privilegeText;
	private RelativeLayout unloginLayout,inviteLayout;
	private LinearLayout loginedLayout;
	private EditText editText;
	private TextView codeCommit,myInviteCodeText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip_activity);
		getSupportActionBar().setTitle("VIP活动");
		initViews();
		showLoginInfo();
//		getVipInfo();
//		EventBus.getDefault().register(this);
	}
	private void initViews(){
		initLoadingLayout();
		countText = (TextView) findViewById(R.id.vip_dt_num1);
		totalDayText = (TextView) findViewById(R.id.vip_dt_num2);
		leftDayText = (TextView) findViewById(R.id.vip_dt_num3);
		img1 = (ImageView) findViewById(R.id.vip_invisit_img1);
		img2 = (ImageView) findViewById(R.id.vip_invisit_img2);
		img3 = (ImageView) findViewById(R.id.vip_invisit_img3);
		img4 = (ImageView) findViewById(R.id.vip_invisit_img4);
		img1.setOnClickListener(this);
		img2.setOnClickListener(this);
		img3.setOnClickListener(this);
		img4.setOnClickListener(this);
		rulesText = (TextView) findViewById(R.id.vip_gz_text2);
		privilegeText = (TextView) findViewById(R.id.vip_tq_text2);
		unloginLayout = (RelativeLayout) findViewById(R.id.vip_unlogin_layout);
		unloginLayout.setOnClickListener(this);
		loginedLayout = (LinearLayout) findViewById(R.id.vip_logined_layout);
		editText = (EditText) findViewById(R.id.vip_edit_code);
		codeCommit = (TextView) findViewById(R.id.vip_code_commit);
		codeCommit.setOnClickListener(this);
		myInviteCodeText = (TextView) findViewById(R.id.vip_my_code);
		inviteLayout = (RelativeLayout) findViewById(R.id.invite_layout);
	}
	InviteInfoParser vipParser = new InviteInfoParser();
	public void getVipInfo(){
		showLoadingLayout();
		VolleyHelper.post(new InviteInfoRequest().make(), new ParseListener(vipParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
					hideLoadingLayout();
					countText.setText("已邀请好友" + vipParser.userInfo.inviteCount + "位");
					totalDayText.setText("获得VIP时长" + vipParser.userInfo.getVipDay + "天");
					leftDayText.setText("剩余VIP时长" + vipParser.userInfo.leftVipDay + "天");
					rulesText.setText(vipParser.rules);
					privilegeText.setText(vipParser.vipPrivilege);
					myInviteCodeText.setText("我的邀请码：" + vipParser.inviteCode);
//					EventBus.getDefault().post(new UserInfoEvent());
				} else {
					showErrorLayout();
				}
			}
		}, null);
	}

	@Override
	protected void reloadData() {
		super.reloadData();
		getVipInfo();
	}
	private void showLoginInfo(){
		if (UserNow.current().userID>0){
			loginedLayout.setVisibility(View.VISIBLE);
			unloginLayout.setVisibility(View.GONE);
			inviteLayout.setVisibility(View.VISIBLE);
		} else {
			unloginLayout.setVisibility(View.VISIBLE);
			loginedLayout.setVisibility(View.GONE);
			inviteLayout.setVisibility(View.GONE);
		}
	}
//	public void onEvent(UserInfoEvent event){
//		showLoginInfo();
//		getVipInfo();
//	}
	ResultParser resultParser = new ResultParser();
	private void commitInviteCode(String str){
		VolleyHelper.post(new AcceptInviteRequest(this, str).make(), new ParseListener(resultParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (resultParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					ToastHelper.showToast(MyVipInfoActivity.this, "提交邀请码成功");
					editText.setText("");
				} else {
					ToastHelper.showToast(MyVipInfoActivity.this, "提交邀请码失败" + resultParser.errMsg);
				}
			}
		}, null);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MyVipInfoActivity");
		super.onResume();
		showLoginInfo();
		getVipInfo();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyVipInfoActivity");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		EventBus.getDefault().unregister(this);
		VolleyHelper.cancelRequest(Constants.inviteInfo);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.vip_invisit_img1:
				shareToWeixin(true);
				break;
			case R.id.vip_invisit_img2:
				shareToWeixin(false);
				break;
			case R.id.vip_invisit_img3:
				shareToWeibo();
				break;
			case R.id.vip_invisit_img4:
				shareToQzone();
				break;
			case R.id.vip_code_commit:
				String code = editText.getText().toString().trim();
				if (TextUtils.isEmpty(code)){
					ToastHelper.showToast(this,"请输入邀请码");
				} else {
					commitInviteCode(code);
					MobclickAgent.onEvent(this, "viptijiao");
				}
				break;
			case R.id.vip_unlogin_layout:
				startActivity(new Intent(this,LoginActivity.class));
				break;
		}
	}
	private String getShareTitle() {
		return "电视粉	有好礼——分享得VIP	快来参加！";
	}
	private String getShareContent() {
		StringBuilder hintBuf = new StringBuilder();
		hintBuf.append("邀请你使用电视粉，邀请码：")
				.append(vipParser.inviteCode)
				.append("。视频聚合应用，一个就够了。新用户可享VIP特权。");
		return hintBuf.toString();
	}
	private UMImage getShareImage(){
		return new UMImage(this, R.drawable.icon);
	}
	private String getShareUrl(){
		return Constants.host.substring(0,Constants.host.lastIndexOf("/")+1)+"share/inviteUser.action";
	}

	private void shareToWeixin(boolean friend) {
		ShareManager.getInstance(this).weixinShare(friend, getShareUrl()+"?type=1",
				getShareTitle(), getShareContent(), getShareImage(), new OnUMShareListener() {
					@Override
					public void umShareResult(String platform,
											  boolean shareSucc, boolean cancel) {
						if (!shareSucc) {
							Toast.makeText(MyVipInfoActivity.this,
									R.string.share_failed, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
	}
	private void shareToQzone() {
		if (!SystemUtils.checkMobileQQ(this)) {
			Toast.makeText(this, "未安装QQ客户端，请先安装QQ", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (!isFinishing()) {
			ShareManager.getInstance(this).qzoneShare(getShareTitle(), getShareContent(),
					getShareImage(), getShareUrl(), new OnUMShareListener() {
						@Override
						public void umShareResult(String platform, boolean shareSucc, boolean cancel) {

						}
					});
		}
	}
	private void shareToWeibo() {
		Intent intent = new Intent();
		intent.setClass(this, SharePlatformActivity.class);
		intent.putExtra("type", SharePlatformActivity.TYPE_SINA);
		intent.putExtra("targetUrl", getShareUrl());
		intent.putExtra("activityPic", "vip_info_share");
		intent.putExtra("programName", getShareTitle()+"    "+getShareContent()
				+ "链接："+getShareUrl());
		startActivity(intent);
	}
}
