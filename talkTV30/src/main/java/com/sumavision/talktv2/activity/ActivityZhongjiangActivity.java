package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnNewActivityInputContactListener;
import com.sumavision.talktv2.http.listener.activities.OnFetchActivityGoodsListener;
import com.sumavision.talktv2.http.request.VolleyActivityRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 中奖页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityZhongjiangActivity extends BaseActivity implements
		OnClickListener, OnNewActivityInputContactListener,
		OnFetchActivityGoodsListener {
	
	private boolean canSubmit = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_zhongjiang);
		getExtra();
		initViews();
	}

	ActivityData activityNewData;
	private long goodsId;

	private void getExtra() {
		Intent intent = getIntent();
		activityNewData = new ActivityData();
		activityNewData.playPic = intent.getStringExtra("playPic");
		activityNewData.activityPic = intent.getStringExtra("activityPic");
		activityNewData.activityName = intent.getStringExtra("activityName");
		activityNewData.videoPath = intent.getStringExtra("videoPath");
		activityNewData.activityId = intent.getLongExtra("activityId", 0L);
		goodsId = intent.getLongExtra("goodsId", 0L);
	}

	private void initViews() {
		initLoadingLayout();
		findViewById(R.id.btn_get_prize).setOnClickListener(this);
		if (activityNewData.activityName != null) {
			getSupportActionBar().setTitle(activityNewData.activityName);
		}
		ImageView pic = (ImageView) findViewById(R.id.pic);
		loadImage(pic, activityNewData.playPic, R.drawable.program_pic_default);
	}

	ExchangeGood mExchangeGood;

	private void performFetchActivityGoodsTask() {
		canSubmit = false;
		showLoadingLayout();
		mExchangeGood = new ExchangeGood();
		String imei = AppUtil.getImei(this);
		VolleyActivityRequest.fetchActivityGoods(this, goodsId,
				activityNewData.activityId, imei, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_get_prize:
			MobclickAgent.onEvent(this, "qqlingqu");
			if (UserNow.current().userID > 0 && canSubmit) {
				performFetchActivityGoodsTask();
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ActivityZhongjiangActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ActivityZhongjiangActivity");
		super.onPause();
	}

	@Override
	public void sendActivityContactResult(int errCode) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			DialogUtil.alertToast(getApplicationContext(), "提交失败");
			break;
		case JSONMessageType.SERVER_CODE_OK:
			DialogUtil.alertToast(getApplicationContext(), "提交成功");
			Intent intent = new Intent(this, ActivityActivity.class);
			intent.putExtra("activityId", activityNewData.activityId);
			intent.putExtra("videoPath", activityNewData.videoPath);
			intent.putExtra("playPic", activityNewData.playPic);
			intent.putExtra("activityPic", activityNewData.activityPic);
			intent.putExtra("activityName", activityNewData.activityName);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onFetchActivityGoods(int errCode, String errMsg,
			ExchangeGood exchangeGood) {
		hideLoadingLayout();
		canSubmit = true;
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			mExchangeGood = exchangeGood;
			Intent intent = new Intent();
			intent.putExtra("goodsType", mExchangeGood.type);
			intent.putExtra("userGoodsId", mExchangeGood.userGoodsId);
			intent.putExtra("status", mExchangeGood.status);
			intent.putExtra("fromZhongjiang", true);
			intent.putExtra("activityId", activityNewData.activityId);
			intent.putExtra("videoPath", activityNewData.videoPath);
			intent.putExtra("playPic", activityNewData.playPic);
			intent.putExtra("activityPic", activityNewData.activityPic);
			intent.putExtra("activityName", activityNewData.activityName);
			intent.putExtra("fromReview",
					getIntent().getBooleanExtra("fromReview", false));
			if (mExchangeGood.type == ExchangeGood.TYPE_VIRTUAL) {
				intent.setClass(this, GoodsReceiveActivity.class);
			} else if (((TalkTvApplication) getApplication()).mAddressData != null) {
				intent.setClass(this, RealGoodsActivity.class);
			} else {
				intent.setClass(this, AddressActivity.class);
			}
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "领取失败", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.fetchActivityGoods);
	}
}
