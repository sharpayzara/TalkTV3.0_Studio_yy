package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.AddressData;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.http.eventbus.ExchangeEvent;
import com.sumavision.talktv2.http.eventbus.MyGiftEvent;
import com.sumavision.talktv2.http.listener.eshop.OnCommitReceiverInfoListener;
import com.sumavision.talktv2.http.listener.eshop.OnEntityGoodDetailListener;
import com.sumavision.talktv2.http.listener.eshop.OnPastDueGoodsDetailListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;

import de.greenrobot.event.EventBus;

/**
 * 领取实物奖品页面
 * */
public class RealGoodsActivity extends BaseActivity implements OnClickListener,
		OnCommitReceiverInfoListener, OnPastDueGoodsDetailListener,
		OnEntityGoodDetailListener {

	private ExchangeGood giftDataInfo = new ExchangeGood();
	private int clickIndex;
	private boolean fromExchange;
	private boolean fromBadgeFlow;

	private ActivityData mActivityNewData;

	private RelativeLayout modifyAddress;
	private TextView nameTxt;
	private TextView phoneTxt;
	private TextView addressTxt;
	private ImageView logo;
	private TextView goodsName;
	private TextView goodsPoint;
	private Button submit;

	private AddressData addressData;
	private boolean received;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("领取");
		setContentView(R.layout.activity_exchange_address);

		getExtras(getIntent());
		initViews();
		getData();
	}

	private void getExtras(Intent intent) {
		clickIndex = intent.getIntExtra("clickIndex", -1);
		giftDataInfo.userGoodsId = intent.getLongExtra("userGoodsId", 0);
		giftDataInfo.type = intent.getIntExtra("goodsType", 0);
		giftDataInfo.status = intent.getIntExtra("status", 0);
		giftDataInfo.ticket = intent.getBooleanExtra("ticket", false);
		fromExchange = intent.getBooleanExtra("exchangePage", false);

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
		addressData = ((TalkTvApplication) getApplication()).mAddressData;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getExtras(intent);
		update();
	}

	private void initViews() {
		initLoadingLayout();
		hideLoadingLayout();
		modifyAddress = (RelativeLayout) findViewById(R.id.modify_address_layout);
		nameTxt = (TextView) findViewById(R.id.name);
		phoneTxt = (TextView) findViewById(R.id.phone);
		addressTxt = (TextView) findViewById(R.id.address);
		logo = (ImageView) findViewById(R.id.logo);
		goodsName = (TextView) findViewById(R.id.goods_name);
		goodsPoint = (TextView) findViewById(R.id.goods_point);
		submit = (Button) findViewById(R.id.submit);

		submit.setOnClickListener(this);
		modifyAddress.setOnClickListener(this);
		update();
	}
	
	private void update() {
		nameTxt.setText(addressData.name);
		phoneTxt.setText(addressData.phone);
		StringBuilder builder = new StringBuilder();
		builder.append(addressData.province);
		if (!addressData.city.equals(addressData.province)) {
			builder.append(addressData.city);
		}
		if (!addressData.district.equals(addressData.city)) {
			builder.append(addressData.district);
		}
		builder.append(addressData.street);
		addressTxt.setText(builder.toString());
	}

	private void getData() {
//		if (giftDataInfo.ticket) {
//			return;
//		}
		if (giftDataInfo.status == ExchangeGood.STATUS_OVER) {
			showLoadingLayout();
			VolleyEshopRequest.pastDueGoodsDetail(this, giftDataInfo.userGoodsId,
					this);
		} else {
			showLoadingLayout();
			VolleyEshopRequest.entityGoodDetail(this, giftDataInfo, this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit) {
			showLoadingLayout();
			submitAddressRequest();
		} else {
			if (addressData != null) {
				Intent intent = new Intent(RealGoodsActivity.this,
						AddressActivity.class);
				intent.putExtra("modify", true);
				setExtras(intent);
				startActivity(intent);
			}
		}
	}
	
	private void setExtras(Intent intent) {
		intent.putExtra("clickIndex", clickIndex);
		intent.putExtra("userGoodsId", giftDataInfo.userGoodsId);
		intent.putExtra("goodsType", giftDataInfo.type);
		intent.putExtra("status", giftDataInfo.status);
		intent.putExtra("exchangePage", fromExchange);
		intent.putExtra("fromZhongjiang", fromBadgeFlow);
		if (mActivityNewData != null) {
			intent.putExtra("playPic", mActivityNewData.playPic);
			intent.putExtra("activityPic", mActivityNewData.activityPic);
			intent.putExtra("activityName", mActivityNewData.activityName);
			intent.putExtra("activityId", mActivityNewData.activityId);
			intent.putExtra("videoPath", mActivityNewData.videoPath);
		}
	}

	private void submitAddressRequest() {
		showLoadingLayout();
		ReceiverInfo receiverInfo = new ReceiverInfo();
		StringBuilder builder = new StringBuilder();
		builder.append(addressData.province);
		if (!addressData.city.equals(addressData.province)) {
			builder.append(addressData.city);
		}
		if (!addressData.district.equals(addressData.city)) {
			builder.append(addressData.district);
		}
		builder.append(addressData.street);
		receiverInfo.address = builder.toString();
		receiverInfo.name = addressData.name;
		receiverInfo.phone = addressData.phone;
		receiverInfo.ticket = giftDataInfo.ticket;
		VolleyEshopRequest.CommitReceiverInfo(this, receiverInfo,
				giftDataInfo.userGoodsId, this);
	}

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
		EventBus.getDefault().post(new ExchangeEvent());
		finish();
	}

	@Override
	public void OnEntityGoodDetail(int errCode, ReceiverInfo info) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			goodsName.setText(giftDataInfo.name);
			goodsPoint.setText(String.valueOf(giftDataInfo.point)
					+ getString(R.string.point));
			loadImage(logo, giftDataInfo.picDetail,
					R.drawable.emergency_pic_bg_detail);
//			if (!TextUtils.isEmpty(info.name)) {
//				submit.setText("已领取");
//				submit.setClickable(false);
//			}
		} else {
			showErrorLayout();
		}
	}

	@Override
	public void onGetPastDueGoodsDetail(int errCode, ExchangeGood good) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			good.userGoodsId = giftDataInfo.userGoodsId;
			good.type = giftDataInfo.type;
			good.status = giftDataInfo.status;
			giftDataInfo = good;
			goodsName.setText(giftDataInfo.name);
			loadImage(logo, giftDataInfo.picDetail,
					R.drawable.emergency_pic_bg_detail);
			submit.setText("已过期");
			submit.setClickable(false);
		} else {
			showErrorLayout();
		}
	}

	@Override
	public void OnCommitReceiverInfo(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			received = true;
			submit.setClickable(false);
			MyGiftEvent event = new MyGiftEvent();
			event.clickIndex = clickIndex;
			EventBus.getDefault().post(event);
			Toast.makeText(this, "领取成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this, GoodsReceiveActivity.class);
			intent.putExtra("userGoodsId", giftDataInfo.userGoodsId);
			intent.putExtra("goodsType", giftDataInfo.type);
			intent.putExtra("status", giftDataInfo.status);
			startActivity(intent);
			finish();
		} else {
			Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
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

}
