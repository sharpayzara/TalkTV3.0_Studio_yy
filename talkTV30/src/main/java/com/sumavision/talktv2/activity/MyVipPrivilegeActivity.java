package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.suamvison.net.JSONMessageType;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.InviteInfoParser;
import com.sumavision.talktv2.http.json.InviteInfoRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 
 * @description VIP信息页面
 * 
 */
public class MyVipPrivilegeActivity extends BaseActivity implements View.OnClickListener{
	private ImageView img1;
	private TextView vipInfoText,shareText,nameText;
	private TextView privilegeText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("我的VIP特权");
		setContentView(R.layout.activity_vip_privilege);
		initViews();
		getVipInfo();
	}
	private void initViews(){
		initLoadingLayout();
		img1 = (ImageView) findViewById(R.id.privilege_img);
		vipInfoText = (TextView) findViewById(R.id.privilege_date);
		shareText = (TextView) findViewById(R.id.privilege_invite);
		privilegeText = (TextView) findViewById(R.id.privilege_text);
		shareText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
		shareText.getPaint().setAntiAlias(true);//抗锯齿
		shareText.setOnClickListener(this);
		shareText.setOnClickListener(this);
		if (PreferencesUtils.getBoolean(this,null,Constants.vipActivity,false)){
			shareText.setVisibility(View.VISIBLE);
		} else {
			shareText.setVisibility(View.GONE);
		}
		nameText = (TextView) findViewById(R.id.privilege_name);
		nameText.setText(TextUtils.isEmpty(UserNow.current().name) ? "" : UserNow.current().name);
	}
	InviteInfoParser vipParser = new InviteInfoParser();
	public void getVipInfo(){
		showLoadingLayout();
		VolleyHelper.post(new InviteInfoRequest().make(), new ParseListener(vipParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
					hideLoadingLayout();
					if (vipParser.userInfo.isVip) {
						img1.setImageResource(R.drawable.vip_privilege_yes);
						vipInfoText.setText("VIP剩余时长："+vipParser.userInfo.leftVipDay + "天");
						shareText.setText("邀请好友，增加VIP时长");
					} else {
						img1.setImageResource(R.drawable.vip_privilege_no);
						vipInfoText.setText("哎~您还不是VIP");
						shareText.setText("分享得VIP，享特权");
					}
					privilegeText.setText(vipParser.vipPrivilege);
					EventBus.getDefault().post(new UserInfoEvent());
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

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MyVipInfoActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyVipInfoActivity");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.inviteInfo);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.privilege_invite:
				startActivity(new Intent(MyVipPrivilegeActivity.this,MyVipInfoActivity.class));
				break;
		}
	}
}
