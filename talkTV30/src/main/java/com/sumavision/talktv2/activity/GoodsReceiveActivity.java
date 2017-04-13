package com.sumavision.talktv2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ReceiverAdapter;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.components.StaticListView;
import com.sumavision.talktv2.fragment.ReceiverInfoFragment;
import com.sumavision.talktv2.fragment.ReceiverInfoFragment.OnInputReceiverInfoListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.ExchangeEvent;
import com.sumavision.talktv2.http.listener.eshop.OnCommitReceiverInfoListener;
import com.sumavision.talktv2.http.listener.eshop.OnEntityGoodDetailListener;
import com.sumavision.talktv2.http.listener.eshop.OnPastDueGoodsDetailListener;
import com.sumavision.talktv2.http.listener.eshop.OnVirtualGoodDetailListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 领取页面
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GoodsReceiveActivity extends BaseActivity implements
		OnClickListener, OnInputReceiverInfoListener,
		OnPastDueGoodsDetailListener, OnVirtualGoodDetailListener,
		OnEntityGoodDetailListener, OnCommitReceiverInfoListener {

	private ImageView logoImageView;

	private Button exchangeImageView;

	private TextView giftName;

	private TextView giftPoint;

	private TextView giftTime;

	private LinearLayout receiverInfolayout;

	private TextView receiverNameText;

	private TextView receiverAddressText;

	private TextView receiverPhoneText;
	private TextView receiverRemarkText;

	private TextView updateInfo;

	private TextView entityTip;

	private ReceiverInfo receiverInfo = new ReceiverInfo();

	private LinearLayout exchangeCodeLayout;

	private TextView exchangeCodeText;

	private TextView useIntro;

	private ImageView expand;

	private StaticListView receiverListView;

	private RelativeLayout infoLayout;

	private ExchangeGood giftDataInfo = new ExchangeGood();

	SharedPreferences receiverSp;

	private boolean received;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setTitle(R.string.receive);
		setContentView(R.layout.activity_goods_receive);

		getExtras();
		EventBus.getDefault().post(new ExchangeEvent());
		logoImageView = (ImageView) findViewById(R.id.logo);
		infoLayout = (RelativeLayout) findViewById(R.id.info_layout);
		giftName = (TextView) findViewById(R.id.name);
		giftPoint = (TextView) findViewById(R.id.goods_point);
		giftTime = (TextView) findViewById(R.id.time);
		exchangeImageView = (Button) findViewById(R.id.exchange);
		exchangeImageView.setClickable(false);
		initLoadingLayout();
		getData();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("GoodsReceiveActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("GoodsReceiveActivity");
		super.onPause();
	}

	private void getData() {
		if (giftDataInfo.status == ExchangeGood.STATUS_OVER) {
			getPastDueData();
		} else {
			if (giftDataInfo.type == ExchangeGood.TYPE_VIRTUAL) {
				initVirtualView();
			} else {
				initEntityGiftView();
			}
			getDetailTask();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			close();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			close();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean fromExchange;

	boolean fromBadgeFlow;

	private ActivityData mActivityNewData;

	private void getExtras() {
		clickIndex = getIntent().getIntExtra("clickIndex", -1);
		giftDataInfo.userGoodsId = getIntent().getLongExtra("userGoodsId", 0);
		giftDataInfo.type = getIntent().getIntExtra("goodsType", 0);
		giftDataInfo.status = getIntent().getIntExtra("status", 0);
		fromExchange = getIntent().getBooleanExtra("exchangePage", false);
		giftDataInfo.ticket = getIntent().getBooleanExtra("ticket", false);
		/*
		 * 徽章活动流程
		 */
		Intent intent = getIntent();
		fromBadgeFlow = intent.getBooleanExtra("fromZhongjiang", false);
		if (fromBadgeFlow) {
			mActivityNewData = new ActivityData();
			mActivityNewData.playPic = intent.getStringExtra("playPic");
			mActivityNewData.activityPic = intent.getStringExtra("activityPic");
			mActivityNewData.activityName = intent
					.getStringExtra("activityName");
			mActivityNewData.videoPath = intent.getStringExtra("videoPath");
			mActivityNewData.activityId = intent.getLongExtra("activityId", 0L);
		}
	}

	private void initEntityGiftView() {
		receiverInfolayout = (LinearLayout) findViewById(R.id.receiver_info);
		receiverNameText = (TextView) findViewById(R.id.receiver_name);
		receiverAddressText = (TextView) findViewById(R.id.receiver_address);
		receiverPhoneText = (TextView) findViewById(R.id.receiver_phone);
		receiverRemarkText = (TextView) findViewById(R.id.receiver_remark);
		updateInfo = (TextView) findViewById(R.id.update_info);
		updateInfo.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		updateInfo.getPaint().setAntiAlias(true);
		entityTip = (TextView) findViewById(R.id.entity_tip);
		exchangeImageView.setOnClickListener(this);
		updateInfo.setOnClickListener(this);
		receiverInfolayout.setOnClickListener(this);
		receiverSp = getSharedPreferences("receiver", Context.MODE_PRIVATE);
		// receiverSp.registerOnSharedPreferenceChangeListener(this);
	}

	private void initVirtualView() {
		exchangeCodeLayout = (LinearLayout) findViewById(R.id.exchange_code);
		exchangeCodeText = (TextView) findViewById(R.id.code);
		expand = (ImageView) findViewById(R.id.expand);
		useIntro = (TextView) findViewById(R.id.use_desc);
		expand.setOnClickListener(this);
		receiverListView = (StaticListView) findViewById(R.id.receiver_list);
	}

	private void getPastDueData() {
		infoLayout.setVisibility(View.GONE);
		exchangeImageView.setVisibility(View.GONE);
		showLoadingLayout();
		VolleyEshopRequest.pastDueGoodsDetail(this, giftDataInfo.userGoodsId,
				this);
	}

	private void getDetailTask() {
		infoLayout.setVisibility(View.GONE);
		exchangeImageView.setVisibility(View.GONE);
		showLoadingLayout();
		if (giftDataInfo.type == ExchangeGood.TYPE_VIRTUAL) {
			VolleyEshopRequest.virtualGoodDetail(this, giftDataInfo, this);
		} else {
			VolleyEshopRequest.entityGoodDetail(this, giftDataInfo, this);
		}
	}

	private void commitInfo() {
		showLoadingLayout();
		if (TextUtils.isEmpty(receiverInfo.name)
				|| TextUtils.isEmpty(receiverInfo.address)
				|| TextUtils.isEmpty(receiverInfo.phone)) {
			Toast.makeText(this, "收货信息不完整，请重新填写", Toast.LENGTH_SHORT).show();
			hideLoadingLayout();
			return;
		}
		VolleyEshopRequest.CommitReceiverInfo(this, receiverInfo,
				giftDataInfo.userGoodsId, this);
	}

	private void updateEntityGoodsUI() {
		giftName.setText(giftDataInfo.name);
		giftPoint.setText(String.valueOf(giftDataInfo.point)
				+ getString(R.string.point));
		infoLayout.setVisibility(View.VISIBLE);
		loadImage(logoImageView, giftDataInfo.picDetail,
				R.drawable.emergency_pic_bg_detail);
		giftTime.setText(getString(R.string.gift_time, giftDataInfo.endTime));
		receiverInfolayout.setVisibility(View.VISIBLE);
		exchangeImageView.setVisibility(View.VISIBLE);
		exchangeImageView.setClickable(false);
		if (!TextUtils.isEmpty(receiverInfo.name)) {
			updateInfo.setVisibility(View.INVISIBLE);
			entityTip.setVisibility(View.VISIBLE);
			updateInfo.setClickable(false);
			receiverInfolayout.setClickable(false);
			
			AddressData addressData = ((TalkTvApplication)getApplication()).mAddressData;
			if (addressData != null) {
				StringBuilder builder = new StringBuilder();
				builder.append(addressData.province);
				if (!addressData.city.equals(addressData.province)) {
					builder.append(addressData.city);
				}
				if (!addressData.district.equals(addressData.city)) {
					builder.append(addressData.district);
				}
				builder.append(addressData.street);
				receiverNameText.setText(addressData.name);
				receiverAddressText.setText(builder.toString());
				receiverPhoneText.setText(addressData.phone);
			}
//			receiverNameText.setText(receiverInfo.name);
//			receiverAddressText.setText(receiverInfo.address);
//			receiverPhoneText.setText(receiverInfo.phone);
//			receiverRemarkText.setText(receiverInfo.remark);
			if (!TextUtils.isEmpty(receiverInfo.remark)) {
				receiverRemarkText.setText(receiverInfo.remark);
			}
			// --成功领取
			exchangeImageView.setText(R.string.gift_received);
			exchangeImageView.setTextColor(getResources().getColor(
					R.color.white));
			exchangeImageView
					.setBackgroundResource(R.drawable.btn_bg_gift_received);
		} else {
			// --填写信息正确领取
			exchangeImageView.setText(R.string.commit_receiver_info);
			exchangeImageView
					.setBackgroundResource(R.drawable.login_btn_selecter);
			exchangeImageView.setClickable(true);
		}
	}

	private void updateVitualGoodsUI() {
		received = true;
		exchangeImageView.setVisibility(View.VISIBLE);
		exchangeImageView.setClickable(false);
		infoLayout.setVisibility(View.VISIBLE);
		loadImage(logoImageView, giftDataInfo.picDetail,
				R.drawable.emergency_pic_bg_detail);
		giftName.setText(giftDataInfo.name);
		giftPoint.setText(String.valueOf(giftDataInfo.point)
				+ getString(R.string.point));
		giftTime.setText(getString(R.string.gift_time, giftDataInfo.endTime));
		useIntro.setText(getString(R.string.use_desc, giftDataInfo.useIntro));
		if (giftDataInfo.status == ExchangeGood.STATUS_RECEIVED
				&& !TextUtils.isEmpty(giftDataInfo.code)) {
			exchangeCodeLayout.setVisibility(View.VISIBLE);
			exchangeCodeText.setText(giftDataInfo.code);
			// --成功领取
			exchangeImageView.setText(R.string.gift_received);
			exchangeImageView.setTextColor(getResources().getColor(
					R.color.white));
			exchangeImageView
					.setBackgroundResource(R.drawable.btn_bg_gift_received);
		} else {
			// 等待小秘书发放
			if (fromExchange) {
				Toast.makeText(this, R.string.receive_succeed,
						Toast.LENGTH_SHORT).show();
			}
			exchangeImageView.setText(R.string.waiting_gift);
			exchangeImageView.setTextColor(getResources().getColor(
					R.color.white));
			exchangeImageView
					.setBackgroundResource(R.drawable.btn_bg_gift_received);
		}
		if (giftDataInfo.userList != null) {
			receiverListView.setAdapter(new ReceiverAdapter(this,
					giftDataInfo.userList));
		}

	}

	@Override
	protected void reloadData() {
		getData();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.update_info || v.getId() == R.id.receiver_info) {
			showInputDialog();
		} else if (v.getId() == R.id.exchange) {
			commitInfo();
		} else if (v.getId() == R.id.expand) {
			if (receiverListView.getVisibility() == View.VISIBLE) {
				v.startAnimation(AnimationUtils.loadAnimation(v.getContext(),
						R.anim.img_to_up));
				receiverListView.setVisibility(View.GONE);
			} else {
				v.startAnimation(AnimationUtils.loadAnimation(v.getContext(),
						R.anim.img_to_down));
				receiverListView.setVisibility(View.VISIBLE);
			}

		}

	}

	ReceiverInfoFragment mFragment;

	private void showInputDialog() {
		mFragment = ReceiverInfoFragment.newInstance(receiverInfo);
		mFragment.setOnInputReceiverInfoListener(this);
		mFragment.show(getSupportFragmentManager(), "receiver");
	}

	private int clickIndex;

	// private boolean receiveSucc;// 领取成功

	private void close() {
		if (clickIndex >= 0 && giftDataInfo.status == 0 && received) {
			Intent intent = new Intent();
			intent.putExtra("userGoodsId", giftDataInfo.userGoodsId);
			intent.putExtra("clickIndex", clickIndex);
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED);
		}
		if (fromExchange) {
			setResult(RESULT_OK);
		}
//		if (fromBadgeFlow) {
//			startActivity(new Intent(this, ActivityActivity.class));
//		}
		finish();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sumavision.talktv2.fragment.ReceiverInfoFragment.
	 * OnInputReceiverInfoListener
	 * #onUpdateInfo(com.sumavision.talktv2.data.ReceiverInfo)
	 */
	@Override
	public void onUpdateInfo(ReceiverInfo mReceiverInfo) {
		this.receiverInfo = mReceiverInfo;
		receiverNameText.setText(receiverInfo.name);
		receiverAddressText.setText(receiverInfo.address);
		receiverPhoneText.setText(receiverInfo.phone);
		if (!TextUtils.isEmpty(receiverInfo.remark)) {
			receiverRemarkText.setText(receiverInfo.remark);
		}
	}

	@Override
	public void onGetPastDueGoodsDetail(int errCode, ExchangeGood good) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_ERROR) {
			showErrorLayout();
		} else {
			good.userGoodsId = giftDataInfo.userGoodsId;
			good.type = giftDataInfo.type;
			good.status = giftDataInfo.status;
			giftDataInfo = good;
			exchangeImageView.setVisibility(View.VISIBLE);
			exchangeImageView.setText(R.string.gift_out);
			loadImage(logoImageView, giftDataInfo.picDetail,
					R.drawable.emergency_pic_bg_detail);
			giftName.setText(giftDataInfo.name);
			giftTime.setText(getString(R.string.gift_time, giftDataInfo.endTime));
			exchangeImageView.setTextColor(getResources().getColor(
					R.color.white));
			exchangeImageView
					.setBackgroundResource(R.drawable.btn_bg_gift_received);
		}

	}

	@Override
	public void onVirtualGoodDetail(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_ERROR) {
			showErrorLayout();
		} else {
			updateVitualGoodsUI();
		}

	}

	@Override
	public void OnEntityGoodDetail(int errCode, ReceiverInfo info) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_ERROR) {
			showErrorLayout();
		} else {
			this.receiverInfo = info;
			updateEntityGoodsUI();
		}

	}

	@Override
	public void OnCommitReceiverInfo(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			// receiveSucc = true;
			received = true;
			exchangeImageView.setVisibility(View.VISIBLE);
			exchangeImageView.setText(R.string.gift_received);
			exchangeImageView.setTextColor(getResources().getColor(
					R.color.white));
			exchangeImageView
					.setBackgroundResource(R.drawable.btn_bg_gift_received);
			updateInfo.setVisibility(View.INVISIBLE);
			entityTip.setVisibility(View.VISIBLE);
			exchangeImageView.setClickable(false);
			updateInfo.setClickable(false);
			receiverInfolayout.setClickable(false);

		} else {
			Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.pastDueGoodsDetail);
		VolleyHelper.cancelRequest(Constants.fetchDetailRealGoods);
		VolleyHelper.cancelRequest(Constants.fetchDetailVirtualGoods);
		VolleyHelper.cancelRequest(Constants.submitAddress);
	}
}
