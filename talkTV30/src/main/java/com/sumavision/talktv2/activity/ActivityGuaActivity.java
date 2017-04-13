package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ActivityData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.Good;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsParser;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsRequest;
import com.sumavision.talktv2.http.listener.activities.OnActivityShakeListener;
import com.sumavision.talktv2.http.request.VolleyActivityRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.WebpUtils;
import com.sumavision.talktv2.widget.EraserView;
import com.sumavision.talktv2.widget.EraserView.OnShowAwardListener;
import com.sumavision.talktv2.wxapi.SharePlatformActivity;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 刮奖
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ActivityGuaActivity extends BaseActivity implements
		OnActivityShakeListener {
	boolean isTemp;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		PreferencesUtils
				.putBoolean(this, null, "isForPad", AppUtil.isPad(this));
		MobclickAgent.onEvent(this, "guajiang");
		getWindow().setBackgroundDrawable(
				new BitmapDrawable(getResources(), WebpUtils.getAssetBitmap(
						this, "webp/activity_gua_bg.webp")));
		getExtra();
		setContentView(R.layout.activity_gua);
		initViews();
		if (arg0 != null) {
			boolean loaded = arg0.getBoolean("dataloaded");
			if (!loaded) {
				getData();
			}
		} else {
			getData();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("dataloaded", true);
	};

	ImageView gifView;
	AnimationDrawable animationDrawable;
	private EraserView eraserView;
	private ProgressBar progressBar;
	private TextView errTextView;
	private ImageButton openButton;
	private int guaTime;
	private TextView tipTxt;
	private ImageView imgTip;
	private RelativeLayout guaLayoutRe;
	private RelativeLayout gua_layout, noChanceLayout;
	private LinearLayout contentLayout;
	private ImageView eraserLayiv;
	private RelativeLayout eraserLay;

	private void initViews() {

		contentLayout = (LinearLayout) findViewById(R.id.linear);
		noChanceLayout = (RelativeLayout) findViewById(R.id.layout_no_chance);
		gua_layout = (RelativeLayout) findViewById(R.id.gua_layout);
		gua_layout.setGravity(Gravity.CENTER_HORIZONTAL);
		guaLayoutRe = (RelativeLayout) findViewById(R.id.gua_layout_re);
		eraserLay = (RelativeLayout) findViewById(R.id.eraserLay);
		tipTxt = (TextView) findViewById(R.id.btip);
		imgTip = (ImageView) findViewById(R.id.img_tip);
		if (PreferencesUtils.getBoolean(this, null, "isForPad")) {// padguajiang
			guaLayoutRe.setVisibility(View.GONE);
			eraserLay.setVisibility(View.VISIBLE);
			eraserView = new EraserView(this);
			eraserLayiv = (ImageView) findViewById(R.id.eraserLay_iv);
			eraserView.setForeGround(R.drawable.foreground, 0);
			eraserLay.addView(eraserView);
		} else {
			guaLayoutRe.setVisibility(View.VISIBLE);
			eraserLay.setVisibility(View.GONE);
			eraserView = (EraserView) findViewById(R.id.eraserView);
		}
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		errTextView = (TextView) findViewById(R.id.err_text);
		errTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				errTextView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				openButton.setVisibility(View.GONE);
				eraserView.setVisibility(View.GONE);
				getData();
			}
		});
		openButton = (ImageButton) findViewById(R.id.open);
		openButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(ActivityGuaActivity.this, "guakai");
				eraserView.setForeGround(0x00ff00ff);
				handler.sendEmptyMessageDelayed(12, 1000);
			}
		});
		eraserView.setShowAwardListener(new OnShowAwardListener() {
			@Override
			public void showAwardSucceed() {
				eraserView.setForeGround(0x00ff00ff);
				handler.sendEmptyMessageDelayed(12, 1000);
			}
		});
		if (isTemp){
			String str = tipTxt.getText().toString();
			int index = str.indexOf("2");
			if (index != -1){
				tipTxt.setText(str.substring(0,index));
			}
		}
	}

	private void setEraserBackGround() {
		int resId = R.drawable.jiang0;
		if (activityNewData.good != null)
			switch (activityNewData.good.level) {
			case 1:
				resId = R.drawable.jiang1;
				break;
			case 2:
				resId = R.drawable.jiang2;
				break;
			case 3:
				resId = R.drawable.jiang3;
				break;
			case 4:
				resId = R.drawable.jiang4;
				break;
			case 5:
				resId = R.drawable.jiang5;
				break;
			case 0:
			default:
				activityNewData.good.level = 0;
				resId = R.drawable.jiang0;
				break;
			}
		if (isTemp && activityNewData.good.level == 1){
			resId = R.drawable.holiday_award;
		}
		if (PreferencesUtils.getBoolean(this, null, "isForPad")) {
			eraserLayiv.setBackgroundResource(resId);
		} else {
			eraserView.setForeGround(R.drawable.foreground, 0);
			eraserView.setBackgroundResource(resId);
		}

	}

	private void getExtra() {
		Intent intent = getIntent();
		activityNewData = new ActivityData();
		activityNewData.activityId = intent.getLongExtra("activityId", 0L);
		activityNewData.activityName = intent.getStringExtra("activityName");
		activityNewData.videoPath = intent.getStringExtra("videoPath");
		activityNewData.playPic = intent.getStringExtra("playPic");
		activityNewData.activityPic = intent.getStringExtra("activityPic");
		isTemp = intent.getBooleanExtra("isTemp", false);
		if (!isTemp){
			VolleyActivityRequest.newActivityComplete(null, null, ActivityGuaActivity.this,
					activityNewData.activityId);
		}
	}

	AnimationDrawable zhongjiangDrawable;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (isTemp){
				Intent intent = new Intent(ActivityGuaActivity.this,HolidayResultActivity.class);
				if (activityNewData.good != null && activityNewData.good.id>0) {
					intent.putExtra("isAward",true);
					intent.putExtra("goodsId",(int)activityNewData.good.id);
				}else {
					intent.putExtra("isAward", false);
				}
				PreferencesUtils.putBoolean(ActivityGuaActivity.this, "dw", "duanwu_award"+UserNow.current().userID, true);
				EventBus.getDefault().post(new EventMessage("ActivityActivity"));
				startActivity(intent);
				finish();
				return;
			}
			boolean zhong = false;
			if (activityNewData.good != null && activityNewData.good.level != 0) {
				guaTime = 2;
				openJiangPinActivity();
				zhong = true;
			}
			if (guaTime >= 1 && !zhong) {
				showOverActivity();
			}

		};
	};

	private void openJiangPinActivity() {
		Intent intent = new Intent(this, ActivityZhongjiangActivity.class);
		activityNewData.playPic = activityNewData.good.pic;
		intent.putExtra("activityId", activityNewData.activityId);
		intent.putExtra("playPic", activityNewData.playPic);
		intent.putExtra("activityPic", activityNewData.activityPic);
		intent.putExtra("videoPath", activityNewData.videoPath);
		intent.putExtra("activityId", activityNewData.activityId);
		intent.putExtra("activityName", activityNewData.activityName);
		intent.putExtra("goodsId", activityNewData.good.id);
		intent.putExtra("fromReview",
				getIntent().getBooleanExtra("fromReview", false));
		intent.putExtra("isTemp",getIntent().getBooleanExtra("isTemp",false));
		startActivity(intent);
		finish();
	}

	ActivityData activityNewData;

	private void getData() {
		if (isTemp && !TextUtils.isEmpty(PreferencesUtils.getString(this,"dw","duanwu_goods"+ UserNow.current().userID,""))){
			Good temp;
			String str = PreferencesUtils.getString(this,"dw","duanwu_goods"+ UserNow.current().userID,"");
			Gson gson = new Gson();
			temp = gson.fromJson(str,Good.class);
			getActivityGood(0,"",temp);
			return;
		}
		guaTime++;
		VolleyActivityRequest.newActivityShake(this, this, this,
				(int) activityNewData.activityId);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ActivityGuaActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ActivityGuaActivity");
		super.onPause();
	}

	RelativeLayout retryLayout;
	Button cancel1Btn, shareBtn;
	ImageView cancel2Btn;
	RelativeLayout overLayout;
	Button overBtn;
	ImageView cancel3Btn;

	private void showOverActivity() {
		overLayout = (RelativeLayout) findViewById(R.id.overlayout);
		overLayout.setVisibility(View.VISIBLE);
		overBtn = (Button) findViewById(R.id.back2);
		overBtn.setOnClickListener(retryLayoutOnClickListener);
		cancel3Btn = (ImageView) findViewById(R.id.cancel2);
		cancel3Btn.setOnClickListener(retryLayoutOnClickListener);
	}

	OnClickListener retryLayoutOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.review:
			case R.id.cancel1:
				retryLayout.setVisibility(View.GONE);
				showOverActivity();
				break;
			case R.id.back:
				retryLayout.setVisibility(View.GONE);
				showShare();
				break;
			case R.id.back2:
			case R.id.cancel2:
				// if (PreferencesUtils.getInt(getApplicationContext(), null,
				// "isreview") != 2) {
				// Intent intent = new Intent(ActivityGuaActivity.this,
				// ActivityResultActivity.class);
				// intent.putExtra("activityId", activityNewData.activityId);
				// intent.putExtra("videoPath", activityNewData.videoPath);
				// intent.putExtra("playPic", activityNewData.playPic);
				// intent.putExtra("activityPic", activityNewData.activityPic);
				// intent.putExtra("activityName",
				// activityNewData.activityName);
				// startActivity(intent);
				// }
				finish();
				break;
			case R.id.share1:
				MobclickAgent.onEvent(ActivityGuaActivity.this, "hdfenxiang",
						"新浪微博");
				openShareActivity(1);
				share.setVisibility(View.GONE);
				break;
			case R.id.share2:
				MobclickAgent.onEvent(ActivityGuaActivity.this, "hdfenxiang",
						"微信会话");
				openShareActivity(2);
				share.setVisibility(View.GONE);
				break;
			case R.id.share3:
				MobclickAgent.onEvent(ActivityGuaActivity.this, "hdfenxiang",
						"微信朋友圈");
				openShareActivity(3);
				share.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}

	};

	private void openShareActivity(int type) {
		Intent intent = new Intent(this, SharePlatformActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("from", 1);
		startActivityForResult(intent, 22);
	}

	ImageView share1, share2, share3;
	View share;

	private void showShare() {
		share = findViewById(R.id.share);
		share.setVisibility(View.VISIBLE);
		share1 = (ImageView) findViewById(R.id.share1);
		share2 = (ImageView) findViewById(R.id.share2);
		share3 = (ImageView) findViewById(R.id.share3);
		share1.setOnClickListener(retryLayoutOnClickListener);
		share2.setOnClickListener(retryLayoutOnClickListener);
		share3.setOnClickListener(retryLayoutOnClickListener);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 22) {
			if (RESULT_OK == arg1) {
				guaTime = 1;
				errTextView.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				openButton.setVisibility(View.GONE);
				eraserView.setVisibility(View.GONE);
				getData();
				DialogUtil.alertToast(getApplicationContext(), "还可以再玩一次");
			} else {
				// showRetryActivity();
			}
		}
	}

	@Override
	public void getActivityGood(int errCode, String errmsg, Good activityGood) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (isTemp || (!TextUtils.isEmpty(errmsg) && errmsg.contains("已摇奖"))) {
				contentLayout.setVisibility(View.INVISIBLE);
				noChanceLayout.setVisibility(View.VISIBLE);
				tipTxt.setVisibility(View.GONE);
				imgTip.setVisibility(View.GONE);
				if (isTemp){
					PreferencesUtils.putBoolean(ActivityGuaActivity.this,"dw","duanwu_award"+UserNow.current().userID,true);
					EventBus.getDefault().post(new EventMessage("ActivityActivity"));
				}
			} else {
				errTextView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				openButton.setVisibility(View.GONE);
				eraserView.setVisibility(View.GONE);
			}
			break;
		case JSONMessageType.SERVER_CODE_OK:
			errTextView.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
			eraserView.setVisibility(View.VISIBLE);
			openButton.setVisibility(View.VISIBLE);
			activityNewData.good = activityGood;
			setEraserBackGround();
			if (isTemp && TextUtils.isEmpty(PreferencesUtils.getString(this,"dw","duanwu_goods"+ UserNow.current().userID,""))){
				Gson gson = new Gson();
				String str = gson.toJson(activityGood);
				PreferencesUtils.putString(this,"dw","duanwu_goods"+ UserNow.current().userID,str);
			}
			if (isTemp ){
				if (activityNewData.good.id>0){
					getAward(activityNewData.good.id);
				}else {
					PreferencesUtils.putBoolean(ActivityGuaActivity.this,"dw","duanwu_award"+UserNow.current().userID,true);
					EventBus.getDefault().post(new EventMessage("ActivityActivity"));
				}
			}
			break;
		default:
			break;
		}

	}
	FetchActivityGoodsParser mParser = new FetchActivityGoodsParser();
	private void getAward(long goodsId){
		VolleyHelper.post(new FetchActivityGoodsRequest(activityNewData.activityId,goodsId , AppUtil.getImei(this)).make(), new ParseListener(mParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (mParser.errCode == JSONMessageType.SERVER_CODE_OK){
					PreferencesUtils.putBoolean(ActivityGuaActivity.this,"dw","duanwu_award"+UserNow.current().userID,true);
					EventBus.getDefault().post(new EventMessage("ActivityActivity"));
				}
			}
		}, null);
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.activityList);
	}

}
