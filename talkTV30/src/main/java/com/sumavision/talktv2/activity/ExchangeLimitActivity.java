package com.sumavision.talktv2.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.encrypt.EncryptUtil;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.R.color;
import com.sumavision.talktv2.adapter.RecommendImageAdapter;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.EKeyData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.FocusGallery;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.ExchangeEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DebrisExchangeRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.eshop.GoodsLimitDetailParser;
import com.sumavision.talktv2.http.json.eshop.GoodsLimitDetailRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeGoodsListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeKeyListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 商城兑换
 * 
 * @author suma-hpb
 * 
 */
public class ExchangeLimitActivity extends BaseActivity implements
		OnClickListener, OnExchangeKeyListener, OnExchangeGoodsListener {

	private ExchangeGood mExchangeGood = new ExchangeGood();
	EncryptUtil native1;
	long hotGoodsId;
	int userGoodsId;
	int goodsType;
	int status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange_limit);
		String title = getIntent().getStringExtra("title");
		if (TextUtils.isEmpty(title)) {
			title = getString(R.string.exchange_detail);
		}
		native1 = new EncryptUtil();
		getSupportActionBar().setTitle(title);
		initLoadingLayout();
		initViews();
		hotGoodsId = getIntent().getLongExtra("hotGoodsId", 0);
		userGoodsId = getIntent().getIntExtra("userGoodsId", 0);
		goodsType = getIntent().getIntExtra("goodsType", 0);
		status = getIntent().getIntExtra("status", 0);
		EventBus.getDefault().register(this);
		getGoodsInfo(hotGoodsId);
	}

	private void getGoodsInfo(long hotGoodsId) {
		showLoadingLayout();
		VolleyHelper.post(new GoodsLimitDetailRequest(hotGoodsId).make(),
				new ParseListener(goodsParser) {

					@Override
					public void onParse(BaseJsonParser parser) {
						hideLoadingLayout();
						if (goodsParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							contentView.setVisibility(View.VISIBLE);
							updateGoodsInfo(goodsParser.goods);
							updateExchangeBtnStatus();
						} else {
							showErrorLayout();
						}
					}
				}, this);
	}

	final GoodsLimitDetailParser goodsParser = new GoodsLimitDetailParser();

	private void updateGoodsInfo(ExchangeGood goods) {
		this.mExchangeGood = goods;
		goodTicketTxt.setText(goods.totalPieceCount + "碎片");
		if (goods.hasPieceCount<goods.totalPieceCount && goods.totalPieceCount>0) {
			ticketBtn.setBackgroundResource(R.drawable.btn_exchange_bg_unable);
			ticketBtn.setTextColor(Color.GRAY);
			ticketBtn.setText(goods.hasPieceCount+"/"+goods.totalPieceCount);
		} else if(goods.hasPieceCount>=goods.totalPieceCount) {
			ticketBtn.setBackgroundResource(R.drawable.btn_exchange_bg);
			ticketBtn.setTextColor(getResources().getColor(color.navigator_bg_color));
			ticketBtn.setText(goods.hasPieceCount + "/" + goods.totalPieceCount);
		}
		if (UserNow.current().userID==0){
			ticketBtn.setText("登录兑换");
			ticketBtn.setBackgroundResource(R.drawable.btn_exchange_bg);
			ticketBtn.setTextColor(getResources().getColor(color.navigator_bg_color));
		}
		goodsNameTxt.setText(goods.name);
		StringBuffer poingBuf = new StringBuffer();
		poingBuf.append(String.valueOf(goods.point));
		poingBuf.append((getString(R.string.point)));
		goodPointsTxt.setText(poingBuf.toString());
		updateStarsLayout(goods.picList);
		if(goods.totalPieceCount==0){
			findViewById(R.id.ticket_layout).setVisibility(View.GONE);
		}
		
		while (layoutContent.getChildCount() > 1) {
			layoutContent.removeViewAt(layoutContent.getChildCount() - 1);
		}
		ArrayList<ExchangeGood> content = goods.contentList;
		if (content == null || content.size() == 0) {
			return;
		}
		for (int i = 1; i < content.size(); i++) {
			View v = addContentLayout(content.get(i));
			if (v != null) {
				layoutContent.addView(v);
			}
		}
	}
	
	private View addContentLayout(ExchangeGood info) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View v = inflater.inflate(R.layout.exchange_item, null);
		TextView title = (TextView) v.findViewById(R.id.item_title);
		TextView content = (TextView) v.findViewById(R.id.item_content);
		title.setText(info.title);
		content.setText(info.content);
		return v;
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ExchangeLimitActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("ExchangeLimitActivity");
		super.onPause();
	}

	protected void reloadData() {
		showLoadingLayout();
		getGoodsInfo(hotGoodsId);
	};

	TextView goodsNameTxt, goodPointsTxt, goodTicketTxt;
	LinearLayout layoutContent;
	Button myGiftBtn, exchangeBtn, ticketBtn;
	ScrollView contentView;

	private void initViews() {
		contentView = (ScrollView) findViewById(R.id.content);
		goodsNameTxt = (TextView) findViewById(R.id.tv_name);
		myGiftBtn = (Button) findViewById(R.id.btn_gift);
		exchangeBtn = (Button) findViewById(R.id.btn_exchange);
		ticketBtn = (Button) findViewById(R.id.btn_exchange_ticket);
		goodPointsTxt = (TextView) findViewById(R.id.tv_point);
		goodTicketTxt = (TextView) findViewById(R.id.tv_ticket);
		layoutContent = (LinearLayout) findViewById(R.id.layout_content);
		myGiftBtn.setOnClickListener(this);
		exchangeBtn.setOnClickListener(this);
		exchangeBtn.setClickable(false);
		ticketBtn.setOnClickListener(this);
		initStarsLayout();
	}

	private FocusGallery picView;
	private LinearLayout picStarsLayout;
	private static final int MSG_REFRESH_GALLERY = 1;
	private static final int DELAY_DURATION = 6000;
	private RecommendImageAdapter imageAdapter;
	private int currentPagePosition = 0;
	private View starCacheView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_GALLERY: {
				if (picView != null && imageAdapter != null
						&& imageAdapter.getCount() > 0) {
					picView.setSelection((currentPagePosition + 1)
							% imageAdapter.getCount());
				}
				sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
				break;
			}
			default:
				break;
			}
		}
	};;

	@SuppressWarnings("deprecation")
	protected void initStarsLayout() {
		picView = (FocusGallery) findViewById(R.id.pic_view);
		picView.requestDisallowInterceptTouchEvent(true);
//		picView.setAnimationDuration(0);
		picView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int position, long arg3) {
				position = (int) arg0.getAdapter().getItemId(position);
				currentPagePosition = position;
				refreshStarsLayout(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		picView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						handler.removeMessages(MSG_REFRESH_GALLERY);
						break;
					case MotionEvent.ACTION_UP:
						handler.sendEmptyMessageDelayed(MSG_REFRESH_GALLERY, 3000);
						break;

					default:
						break;
				}
				return false;
			}
		});

		picStarsLayout = (LinearLayout) findViewById(R.id.pic_star);
	}

	protected void updateStarsLayout(ArrayList<String> picList) {
		if (picList != null && picList.size() > 0) {
			imageAdapter = new RecommendImageAdapter(picList, this);
			picView.setAdapter(imageAdapter);
			int bigPicSize = picList.size();
			picStarsLayout.removeAllViews();
			for (int i = 0; i < bigPicSize; i++) {
				FrameLayout frame = (FrameLayout) getLayoutInflater().inflate(
						R.layout.rcmd_pic_star, null);
				frame.setTag(i);
				picStarsLayout.addView(frame);
			}
			refreshStarsLayout(0);
			handler.removeMessages(MSG_REFRESH_GALLERY);
			sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
		} else {
			picView.setVisibility(View.INVISIBLE);
		}
	}

	private void sendMsg(int what, int delay) {
		Message msg = handler.obtainMessage(what);
		handler.sendMessageDelayed(msg, delay);
	}

	private void refreshStarsLayout(int position) {
		if (picStarsLayout.getChildCount() <= position) {
			return;
		}
		if (starCacheView != null) {
			starCacheView.setSelected(false);
		}
		starCacheView = picStarsLayout.getChildAt(position);
		starCacheView.setSelected(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_gift:
			if (UserNow.current().userID == 0) {
				openLoginActivity();
			} else {
				startActivity(new Intent(this, MyGiftActivity.class));
			}
			break;
		case R.id.btn_exchange_ticket:
			if (mExchangeGood.hasPieceCount == 0) {
				return;
			}
			if (UserNow.current().userID == 0) {
				openLoginActivity();
			} else {
				if (mExchangeGood.hasPieceCount>=mExchangeGood.totalPieceCount){
					AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("确认");
					builder.setMessage("确定兑换" + mExchangeGood.name);
					builder.setPositiveButton("取消", null);
					builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							exchangePieces();
						}
					});
					builder.show();
				}
			}

//			Intent intent = new Intent();
//			if (goodsType == 0) {
//				goodsType = mExchangeGood.type;
//			}
//			intent.putExtra("goodsType", goodsType);
//			userGoodsId = (int) mExchangeGood.userGoodsId;
//			intent.putExtra("userGoodsId", (long) userGoodsId);
//			intent.putExtra("status", 0);
//			intent.putExtra("ticket", true);
//			intent.putExtra("exchangePage", true);
//			if (goodsType == ExchangeGood.TYPE_VIRTUAL) {
//				intent.setClass(this, GoodsReceiveActivity.class);
//			} else if (((TalkTvApplication) getApplication()).mAddressData != null) {
//				intent.setClass(this, RealGoodsActivity.class);
//			} else {
//				intent.setClass(this, AddressActivity.class);
//			}
//			startActivityForResult(intent, REQUEST_RECEIVE);
			break;
		case R.id.btn_exchange:
			if (UserNow.current().userID == 0) {
				openLoginActivity();
			} else {
				if (UserNow.current().totalPoint >= mExchangeGood.point) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("确认");
					builder.setMessage("确定兑换" + mExchangeGood.name);
					builder.setPositiveButton("取消", null);
					builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getExchageKey();
						}
					});
					builder.show();
				} else {
					startActivityForResult(new Intent(this,
							MyAccountActivity.class), REQUEST_MYACCOUNT);
				}
				
			}
			break;
		default:
			break;
		}
	}

	public ResultParser resultParser = new ResultParser();
	private void exchangePieces() {
		VolleyHelper.post(new DebrisExchangeRequest(goodsParser.goods.id).make(), new ParseListener(resultParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (resultParser.errCode == JSONMessageType.SERVER_CODE_OK){
					ToastHelper.showToast(ExchangeLimitActivity.this,"兑换成功");
					reloadData();
					EventBus.getDefault().post(new EventMessage("MyPartsGoodsFragment"));
				} else {
					ToastHelper.showToast(ExchangeLimitActivity.this,"兑换失败");
				}
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				ToastHelper.showToast(ExchangeLimitActivity.this,"网络异常");
			}
		});
	}

	public void getExchageKey() {
		showLoadingLayout();
		VolleyEshopRequest.GetExchangeKey(this, this);
	}

	private static final int REQUEST_LOGIN = 1;

	private static final int REQUEST_RECEIVE = 2;

	private static final int REQUEST_MYACCOUNT = 3;

	private void openLoginActivity() {
		startActivityForResult(new Intent(this, LoginActivity.class),
				REQUEST_LOGIN);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == REQUEST_LOGIN) {
			if (arg1 == RESULT_OK) {
				updateExchangeBtnStatus();
			}
		} else if (arg0 == REQUEST_RECEIVE && arg1 == RESULT_OK) {
			finish();
		} else if (arg0 == REQUEST_MYACCOUNT) {
			if (UserNow.current().totalPoint > mExchangeGood.point) {
				exchangeBtn.setText(R.string.exchange);
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	
	public void onEvent(UserInfoEvent e) {
//		updateExchangeBtnStatus();
		getGoodsInfo(hotGoodsId);
	}
	
	public void onEvent(ExchangeEvent e) {
		getGoodsInfo(hotGoodsId);
	}

	private void updateExchangeBtnStatus() {
		goodTicketTxt.setText(mExchangeGood.totalPieceCount + "碎片");
		if (mExchangeGood.hasPieceCount >= mExchangeGood.totalPieceCount && mExchangeGood.totalPieceCount>0) {
			ticketBtn.setText("兑换");
		} else {
			ticketBtn.setText(mExchangeGood.hasPieceCount+"/"+mExchangeGood.totalPieceCount);
		}
		
		if (UserNow.current().userID == 0) {
			exchangeBtn.setClickable(true);
			exchangeBtn.setText(R.string.login_exchange);
			ticketBtn.setText(R.string.login_exchange);
		} else {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date now = null;
			Date start = null;
			Date end = null;
			try {
				now = format.parse(mExchangeGood.currentTime);
				start = format.parse(mExchangeGood.startTime);
				end = format.parse(mExchangeGood.endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (start.after(now)) {
				exchangeBtn.setText(R.string.exchange_not_start);
				exchangeBtn
						.setTextColor(getResources().getColor(R.color.white));
				exchangeBtn
						.setBackgroundResource(R.drawable.btn_bg_gift_received);
				exchangeBtn.setClickable(false);
			} else {
				if (mExchangeGood.count == 0 || end.before(now)) {
					// 已经兑换完
					exchangeBtn.setText(R.string.exchange_end);
					exchangeBtn.setTextColor(getResources().getColor(
							R.color.white));
					exchangeBtn
							.setBackgroundResource(R.drawable.btn_bg_gift_received);
					exchangeBtn.setClickable(false);
				} else {
					exchangeBtn.setClickable(true);
					if (UserNow.current().totalPoint >= mExchangeGood.point) {
						exchangeBtn.setText(R.string.exchange);
					} else {
						// 积分不足
						exchangeBtn
								.setText(R.string.exchange_point_insufficient);
					}
				}
			}

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(MSG_REFRESH_GALLERY);
	}

	EKeyData mEKeyData;

	private void exchangeGoods() {
		showLoadingLayout();
		VolleyEshopRequest.exchageGood(this, mExchangeGood,
				native1.encrypt(mEKeyData.key), this);
	}

	@Override
	public void onGetExchangeKey(int errCode, EKeyData eKeyData) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			mEKeyData = eKeyData;
			exchangeGoods();
		}

	}

	@Override
	public void onExchangeGood(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			// 兑换成功跳转领取页
			int total = UserNow.current().totalPoint - mExchangeGood.point;
			UserNow.current().setTotalPoint(total);
			Intent intent = new Intent();
			intent.putExtra("goodsType", mExchangeGood.type);
			intent.putExtra("userGoodsId", mExchangeGood.userGoodsId);
			intent.putExtra("status", mExchangeGood.status);
			intent.putExtra("exchangePage", true);
			if (mExchangeGood.type == ExchangeGood.TYPE_VIRTUAL) {
				intent.setClass(this, GoodsReceiveActivity.class);
			} else if (((TalkTvApplication) getApplication()).mAddressData != null) {
				intent.setClass(this, RealGoodsActivity.class);
			} else {
				intent.setClass(this, AddressActivity.class);
			}
			startActivityForResult(intent, REQUEST_RECEIVE);
		} else {
			StringBuilder exchangeTip = new StringBuilder();
			exchangeTip.append(getString(R.string.exchange_failed));
			switch (errCode) {
			case ExchangeGood.EXCHANGE_LESS_POINT:
				exchangeTip
						.append(getString(R.string.exchange_point_insufficient));
				break;
			case ExchangeGood.EXCHANGE_LESS_GOODS:
				exchangeTip
						.append(getString(R.string.exchange_goods_insufficient));
				exchangeBtn.setText(R.string.gift_out);
				exchangeBtn
						.setTextColor(getResources().getColor(R.color.white));
				exchangeBtn
						.setBackgroundResource(R.drawable.btn_bg_gift_received);
				break;
			case ExchangeGood.EXCHANGE_LESS_DIAMOND:
				exchangeTip
						.append(getString(R.string.exchange_diamond_insufficient));
				break;
			case ExchangeGood.EXCHANGE_NOT_START:
				exchangeTip.append(getString(R.string.exchange_not_start));
				break;
			case ExchangeGood.EXCHANGE_OVER:
				exchangeTip.append(getString(R.string.exchange_over));
				exchangeBtn.setText(R.string.gift_out);
				exchangeBtn
						.setTextColor(getResources().getColor(R.color.white));
				exchangeBtn
						.setBackgroundResource(R.drawable.btn_bg_gift_received);
				break;
			default:
				exchangeTip.append("处理异常");
				break;
			}
			Toast.makeText(this, exchangeTip.toString(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		EventBus.getDefault().unregister(this);
		VolleyHelper.cancelRequest(Constants.getExchangeKey);
		VolleyHelper.cancelRequest(Constants.exchangeGoods);
	}
}
