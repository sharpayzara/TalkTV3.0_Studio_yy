package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.FestivalGoodsAdapter;
import com.sumavision.talktv2.bean.Good;
import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.StaticListView;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.NewYearGuaEvent;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.FestivalChanceParser;
import com.sumavision.talktv2.http.json.FestivalChanceRequest;
import com.sumavision.talktv2.http.json.FestivalParser;
import com.sumavision.talktv2.http.json.FestivalRequest;
import com.sumavision.talktv2.http.json.activities.ActivityShakeParser;
import com.sumavision.talktv2.http.json.activities.ActivityShakeRequest;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsParser;
import com.sumavision.talktv2.http.json.activities.FetchActivityGoodsRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.WebpUtils;
import com.sumavision.talktv2.widget.EraserView;
import com.sumavision.talktv2.widget.EraserView.OnShowAwardListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 节日刮奖
 * 
 * @version
 * @description
 */
public class NewYearGuaActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
	
	private int id;
	private String rule;
	private String tips;
	private int count;
	private ArrayList<HotGoodsBean> listGoods = new ArrayList<HotGoodsBean>();
	
	private boolean hasOpened = false;
	private boolean firstin = true;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		PreferencesUtils
				.putBoolean(this, null, "isForPad", AppUtil.isPad(this));
		MobclickAgent.onEvent(this, "guajiang");
		getWindow().setBackgroundDrawable(
				new BitmapDrawable(getResources(), WebpUtils.getAssetBitmap(
						this, "webp/activity_gua_bg.webp")));
		setContentView(R.layout.activity_newyear_gua);
		EventBus.getDefault().register(this);
		getExtra();
		initViews();
		getData();
	}
	
	TextActionProvider myGiftAction;
	TextActionProvider earnPointAction;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.newyear_gua, menu);
		myGiftAction = (TextActionProvider) menu.findItem(R.id.action_my_gift).getActionProvider();
		earnPointAction = (TextActionProvider) menu.findItem(R.id.action_list).getActionProvider();
		if (myGiftAction == null || earnPointAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		myGiftAction.setShowText(R.string.my_prize);
		myGiftAction.setOnClickListener(ActionProviderClickListener, "gift");
		earnPointAction.setShowText(R.string.winner_list);
		earnPointAction.setOnClickListener(ActionProviderClickListener, "list");
		return super.onCreateOptionsMenu(menu);
	}

	OnClickListener ActionProviderClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (UserNow.current().userID == 0) {
//				openLoginActivity();
			} else {
				String tag = (String) v.getTag();
				if ("gift".equals(tag)) {
					startActivity(new Intent(getApplicationContext(), MyGiftActivity.class));
				} else {
					Intent intent = new Intent(getApplicationContext(), PrizeListActivity.class);
					intent.putExtra("id", id);
					startActivity(intent);
				}
			}

		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("dataloaded", true);
	};

	ImageView gifView;
	AnimationDrawable animationDrawable;
	private EraserView eraserView;
	private ProgressBar progressBar;
	private TextView errTextView;
	private RelativeLayout guaLayoutRe;
	private RelativeLayout gua_layout;
	private ImageView eraserLayiv;
	private RelativeLayout eraserLay;
	private ImageButton openButton;
	
	private TextView explain;
	private TextView btip;
	private Button again;
	private StaticListView rewardsListView;
	private FestivalGoodsAdapter adapter;
	
	private ScrollView guaLayout;

	private void initViews() {
		initLoadingLayout();
		hideLoadingLayout();
		getSupportActionBar().setTitle("");
		guaLayout = (ScrollView) findViewById(R.id.gua_scroll_layout);
		explain = (TextView) findViewById(R.id.explain);
		btip = (TextView) findViewById(R.id.btip);
		again = (Button) findViewById(R.id.btn_again);
		again.setOnClickListener(this);
		rewardsListView = (StaticListView) findViewById(R.id.reward_listview);
		adapter = new FestivalGoodsAdapter(this, listGoods);
		rewardsListView.setAdapter(adapter);
		rewardsListView.setOnItemClickListener(this);
		rewardsListView.setFocusable(false);
		rewardsListView.setFocusableInTouchMode(false);
		
		gua_layout = (RelativeLayout) findViewById(R.id.gua_layout);
		gua_layout.setGravity(Gravity.CENTER_HORIZONTAL);
		guaLayoutRe = (RelativeLayout) findViewById(R.id.gua_layout_re);
		eraserLay = (RelativeLayout) findViewById(R.id.eraserLay);
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
		progressBar = (ProgressBar) findViewById(R.id.progressBar_prize);
		errTextView = (TextView) findViewById(R.id.err_text_prize);
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
				if (hasOpened) {
					return;
				}
				MobclickAgent.onEvent(NewYearGuaActivity.this, "guakai");
				eraserView.setForeGround(0x00ff00ff);
				handler.sendEmptyMessage(1);
			}
		});
		eraserView.setShowAwardListener(new OnShowAwardListener() {
			@Override
			public void showAwardSucceed() {
				eraserView.setForeGround(0x00ff00ff);
				handler.sendEmptyMessage(1);
			}
		});
	}

	private void setEraserBackGround() {
		hasOpened = false;
		int resId = R.drawable.jiang0;
		if (good != null)
			switch (good.level) {
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
			case 6:
				resId = R.drawable.jiang6;
				break;
			case 7:
				resId = R.drawable.jiang7;
				break;
			case 0:
			default:
				resId = R.drawable.jiang0;
				break;
			}
		if (PreferencesUtils.getBoolean(this, null, "isForPad")) {
			eraserLayiv.setBackgroundResource(resId);
		} else {
			eraserView.setForeGround(R.drawable.foreground, 0);
			eraserView.setBackgroundResource(resId);
			eraserView.setShowAwardListener(new OnShowAwardListener() {
				@Override
				public void showAwardSucceed() {
					eraserView.setForeGround(0x00ff00ff);
					handler.sendEmptyMessage(1);
				}
			});
		}

	}

	private void getExtra() {
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);
	}

	AnimationDrawable zhongjiangDrawable;
	
	private FestivalParser fparser = new FestivalParser();
	private void getData() {
		showLoadingLayout();
		guaLayout.setVisibility(View.GONE);
		VolleyHelper.post(new FestivalRequest(id).make(), new ParseListener(fparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (fparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					listGoods.clear();
					listGoods.addAll(fparser.listGoods);
					count = fparser.count;
					tips = fparser.tips;
					rule = fparser.rule;
					updateView();
					guaLayout.setVisibility(View.VISIBLE);
					if (count > 0) {
						doActivityShake();
					} else {
						if (firstin) {
							setEraserViewBkg();
						}
					}
					hideLoadingLayout();
				} else {
					hideLoadingLayout();
				}
			}
		}, null);
	}
	
	private void setEraserViewBkg() {
		hasOpened = true;
		again.setEnabled(true);
		errTextView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		eraserView.setVisibility(View.VISIBLE);
		eraserView.setForeGround(0x00ff00ff);
		int lastLevel = PreferencesUtils.getInt(this, null, "newyear_good_level");
		int resId = R.drawable.jiang0;
		switch (lastLevel) {
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
		case 6:
			resId = R.drawable.jiang6;
			break;
		case 7:
			resId = R.drawable.jiang7;
			break;
		case 0:
		default:
			resId = R.drawable.jiang0;
			break;
		}
		eraserView.setBackgroundResource(resId);
		firstin = false;
	}
	
	private void updateView() {
		btip.setText(tips);
		explain.setText(rule);
		adapter.notifyDataSetChanged();
		setBadgeText();
	}
	
	private void setBadgeText() {
		if (count > 0) {
			again.setText("抽奖机会*" + count);
			again.setTextColor(getResources().getColor(R.color.newyear_text));
			again.setBackgroundResource(R.drawable.festival_ensure_btn_unable);
			again.setEnabled(false);
		} else {
			again.setText("积分换抽奖");
			again.setTextColor(Color.WHITE);
			again.setEnabled(true);
			again.setBackgroundResource(R.drawable.festival_ensure_btn);
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("NewYearGuaActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("NewYearGuaActivity");
		super.onPause();
	}
	
	ActivityShakeParser sparser = new ActivityShakeParser();
	Good good;
	public void doActivityShake() {
		showShakeLoading();
		VolleyHelper.post(new ActivityShakeRequest(this, id).make(), new ParseListener(sparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (sparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					setBadgeText();
					good = sparser.good;
					errTextView.setVisibility(View.GONE);
					progressBar.setVisibility(View.GONE);
					eraserView.setVisibility(View.VISIBLE);
					setEraserBackGround();
					hideShakeLoading();
				} else {
					hideShakeLoading();
				}
			}
		}, null);
	}
	
	private void showShakeLoading() {
		progressBar.setVisibility(View.VISIBLE);
		eraserView.setVisibility(View.GONE);
	}
	
	private void hideShakeLoading() {
		errTextView.setVisibility(View.GONE);
		progressBar.setVisibility(View.GONE);
		eraserView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_again) {
			if (count > 0) {
				doActivityShake();
			} else {
				if (hasOpened) {
					openJiangPinActivity(0);
				}
			}
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (good != null) {
				hasOpened = true;
				count--;
				setBadgeText();
				openJiangPinActivity(1);
			}
		};
	};
	
	private void openJiangPinActivity(int type) {
		Intent intent = new Intent(this, NewYearPrizeActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("id", id);
		if (good != null) {
			intent.putExtra("goodId", good.id);
			intent.putExtra("goodLevel", good.level);
			intent.putExtra("goodName", good.name);
			intent.putExtra("goodPic", good.pic);
			intent.putExtra("goodType", good.goodsType);
			intent.putExtra("goodPoint", good.point);
		}
		startActivity(intent);
	}
	
	public void onEvent(NewYearGuaEvent event) {
		checkChance();
	}
	
	FestivalChanceParser fcparser = new FestivalChanceParser();
	private void checkChance() {
		VolleyHelper.post(new FestivalChanceRequest(id).make(), new ParseListener(fcparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (fcparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					count = fcparser.count;
					if (count > 0) {
						doActivityShake();
					}
				}
			}
		}, null);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HotGoodsBean good = listGoods.get(position);
		if (good.name.contains("积分") || good.goodsType == 3) {
			return;
		}
		Intent intent = new Intent(this, ExchangeLimitActivity.class);
		intent.putExtra("hotGoodsId", good.goodsId);
		startActivity(intent);
	}
	
	FetchActivityGoodsParser gparser = new FetchActivityGoodsParser();

	private void getPrize(int activityId, int goodId) {
		showLoadingLayout();

		VolleyHelper.post(new FetchActivityGoodsRequest(activityId, goodId,AppUtil.getImei(this)).make(),
				new ParseListener(gparser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						hideLoadingLayout();
						if (gparser.errCode == JSONMessageType.SERVER_CODE_OK) {
							Log.i("mylog", "FetchActivityGoodsRequest");
							PreferencesUtils.putInt(NewYearGuaActivity.this, null, "newyear_good_level", good.level);
							if (good.goodsType == 3) {
								UserNow.current().totalPoint += good.point;
								EventBus.getDefault().post(new UserInfoEvent());
								DialogUtil.updateScoreToast("积分+" + good.point);
							}
						}
						hasOpened = true;
						finish();
					}
				}, null);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && good != null && good.level > 0 && !hasOpened) {
			getPrize(id, (int) good.id);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (good != null && good.level > 0 && !hasOpened) {
				getPrize(id, (int) good.id);
			} else {
				finish();
			}
		}
		return true;
	}
}
