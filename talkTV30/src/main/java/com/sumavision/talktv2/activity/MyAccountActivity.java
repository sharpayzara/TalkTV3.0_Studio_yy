package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.suamvision.data.Product;
import com.sumavision.encrypt.EncryptUtil;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.LocalInfo;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.DiamondAdapter;
import com.sumavision.talktv2.bean.DiamondData;
import com.sumavision.talktv2.bean.EKeyData;
import com.sumavision.talktv2.bean.ExchangeData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.OrderResultData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.fragment.AccountHelpDialogFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.listener.epay.OnExchangeVirtualListener;
import com.sumavision.talktv2.http.listener.epay.OnOrderPaySearchListener;
import com.sumavision.talktv2.http.listener.epay.OnPayRuleListListener;
import com.sumavision.talktv2.http.listener.epay.OnSubmitOrderListener;
import com.sumavision.talktv2.http.listener.eshop.OnExchangeKeyListener;
import com.sumavision.talktv2.http.request.VolleyEPayRequest;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.service.ChkDiamondService;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 我的帐号
 * 
 * @author suma-hpb
 * 
 */
@SuppressLint("NewApi")
public class MyAccountActivity extends BaseActivity implements
		android.view.View.OnClickListener, OnSharedPreferenceChangeListener,
		OnPayRuleListListener, OnSubmitOrderListener,
		OnExchangeVirtualListener, OnExchangeKeyListener,
		OnOrderPaySearchListener {
	ListView buyDiamodlist;

	EditText diamondnum, creditnum;

	TextView hyue, hdiam, hcredits, payhistory, mtoc, tcreadits, totaldiamond;

	ArrayList<DiamondData> list;

	ProgressBar progressBar;

	EncryptUtil native1;

	TextView earnScoreTxt;

	int type = 0;

	DiamondData mDiamondData;
	SharedPreferences userInfoSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_account);
		getSupportActionBar()
				.setTitle(getString(R.string.user_info_my_account));
		native1 = new EncryptUtil();
		initUI();
		progressBar.setVisibility(View.VISIBLE);
		getPayRuleList();
		userInfoSp = getSharedPreferences("userInfo", 0);
		userInfoSp.registerOnSharedPreferenceChangeListener(this);
		com.suamvison.net.Constants.host = Constants.host;
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("MyAccountActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyAccountActivity");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (userInfoSp != null) {
			userInfoSp.unregisterOnSharedPreferenceChangeListener(this);
		}
	}

	LinearLayout exchangeCoinLayout;
	EditText coinDiamondNum, coinNum;
	TextView exchangeCoin;

	public void initUI() {
		exchangeCoinLayout = (LinearLayout) findViewById(R.id.exchange_coin_layout);
		if (getIntent().getBooleanExtra("ushow", false)) {
			exchangeCoinLayout.setVisibility(View.VISIBLE);
		}
		buyDiamodlist = (ListView) findViewById(R.id.diamondlist);
		diamondnum = (EditText) findViewById(R.id.diamondnum);
		creditnum = (EditText) findViewById(R.id.creditnum);
		tcreadits = (TextView) findViewById(R.id.tcredits);
		totaldiamond = (TextView) findViewById(R.id.tdiamond);
		earnScoreTxt = (TextView) findViewById(R.id.earn_score);
		earnScoreTxt.setOnClickListener(this);
		tcreadits.setText("" + UserNow.current().totalPoint);
		totaldiamond.setText("" + UserNow.current().diamond);
		hyue = (TextView) findViewById(R.id.hyue);
		hyue.setOnClickListener(this);
		hdiam = (TextView) findViewById(R.id.hdiam);
		hdiam.setOnClickListener(this);
		hcredits = (TextView) findViewById(R.id.hcredits);
		hcredits.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		payhistory = (TextView) findViewById(R.id.payhistroy);
		payhistory.setOnClickListener(this);
		mtoc = (TextView) findViewById(R.id.mtoc);
		mtoc.setOnClickListener(this);
		// add ushow operation
		coinDiamondNum = (EditText) findViewById(R.id.coin_diamond_num);
		coinDiamondNum
				.addTextChangedListener(new MyTextWatcher(coinDiamondNum));
		coinNum = (EditText) findViewById(R.id.coin_num);
		exchangeCoin = (TextView) findViewById(R.id.tv_exchange_coin);
		findViewById(R.id.query_coin).setOnClickListener(this);
		exchangeCoin.setOnClickListener(this);
		// add ushow end
		diamondnum.setSelection(1);
		diamondnum.addTextChangedListener(new MyTextWatcher(diamondnum));
	}

	TextActionProvider serviceAction;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.my_account, menu);
		serviceAction = (TextActionProvider) menu.findItem(R.id.action_service)
				.getActionProvider();
		if (serviceAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		serviceAction.setShowText(R.string.customer_service);
		serviceAction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						TvfanServiceActivity.class));
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private boolean checkDiamond(String num) {
		if (num == null || num.equals("")) {
			Toast.makeText(getApplicationContext(), "请输入宝石数！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (Integer.parseInt(num) == 0) {
			Toast.makeText(getApplicationContext(), "一次至少兑换1个宝石",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (UserNow.current().diamond == 0) {
			Toast.makeText(getApplicationContext(), "您目前暂无宝石,请充值",
					Toast.LENGTH_SHORT).show();
			return false;
		} else if (Integer.parseInt(num) > UserNow.current().diamond) {
			Toast.makeText(getApplicationContext(), "宝石不足", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}

	private void checkNum() {
		String num = diamondnum.getText().toString().trim();
		if (num.length() < 4) {
			if (!TextUtils.isEmpty(num)) {
				if (Integer.parseInt(num) > UserNow.current().diamond) {
					if (UserNow.current().diamond > 999) {
						Toast.makeText(getApplicationContext(),
								"最多一次只能兑换999个钻石", Toast.LENGTH_SHORT).show();
						num = "999";

					} else {
						Toast.makeText(getApplicationContext(), "钻石不能超过您的总额",
								Toast.LENGTH_SHORT).show();
						num = String.valueOf(UserNow.current().diamond);

					}
				}
				if (Long.parseLong(num) == 0) {
					Toast.makeText(getApplicationContext(), "一次最少兑换1个钻石",
							Toast.LENGTH_SHORT).show();
					num = "1";

				} else {
					creditnum.setText("" + dtoc(Long.parseLong(num)));
				}
			} else {
				creditnum.setText("");
			}
		} else {
			diamondnum.setText("999");
		}
	}

	public void getPayRuleList() {
		VolleyEPayRequest.getPayRuleList(this, this);
	}

	public long dtoc(long diamond) {
		// 钻石到积分
		return diamond * 50;
	}

	private void setListViewHeight(ListView lv) {
		ListAdapter la = lv.getAdapter();
		if (null == la) {
			return;
		}
		// calculate height of all items.
		int h = 0;
		final int cnt = la.getCount();
		for (int i = 0; i < cnt; i++) {
			View item = la.getView(i, null, lv);
			item.measure(0, 0);
			h += item.getMeasuredHeight();
		}
		// reset ListView height
		ViewGroup.LayoutParams lp = lv.getLayoutParams();
		lp.height = h + (lv.getDividerHeight() * (cnt - 1));
		lv.setLayoutParams(lp);
	}

	public void update() {
		progressBar.setVisibility(View.GONE);
		DiamondAdapter da = new DiamondAdapter(this, list);
		buyDiamodlist.setAdapter(da);
		setListViewHeight(buyDiamodlist);

	}

	private Product product;

	private void submitOrder(DiamondData data) {
		VolleyEPayRequest.submitOrder(this, String.valueOf(data.id), this);
	}

	private ExchangeData ed;

	Dialog dialog;

	protected void dialog() {
		dialog = new AlertDialog.Builder(this)
				.setTitle(exchangeType == EXCHANGE_COIN ? "兑换金币" : "兑换积分")
				.setMessage("确定要兑换吗？")
				.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								getExchageKey();
								type = 2;
								progressBar.setVisibility(View.VISIBLE);
							}
						})
				.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								dialog.dismiss();
								progressBar.setVisibility(View.GONE);
							}
						}).create();
		dialog.show();
	}

	@SuppressLint("NewApi")
	public void exchageVirtual() {
		String inputDiamond = "";
		if (exchangeType == EXCHANGE_COIN) {
			inputDiamond = coinDiamondNum.getText().toString();
		} else {
			inputDiamond = diamondnum.getText().toString();
		}
		if (inputDiamond.isEmpty()) {
			Toast.makeText(getApplicationContext(), "请输入兑换宝石数",
					Toast.LENGTH_SHORT).show();
			return;
		}
		VolleyEPayRequest.exchangeVirtualMoney(this, exchangeType,
				Integer.parseInt(inputDiamond), native1.encrypt(mEKeyData.key),
				getIntent().getStringExtra("comm"), this);
	}

	private EKeyData mEKeyData;

	public void getExchageKey() {
		VolleyEshopRequest.GetExchangeKey(this, this);
	}

	public void openPay() {
		ToastHelper.showToast(this,"充值暂时关闭请稍后再试");
//		progressBar.setVisibility(View.GONE);
//		Intent intent = new Intent();
//		intent.setClass(this, PayMainActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("product", product);
//		intent.putExtra("jsession", UserNow.current().jsession);
//		intent.putExtra("userid", UserNow.current().userID);
//		intent.putExtra("key", native1.encrypt(mEKeyData.key));
//		intent.putExtras(bundle);
//		startActivityForResult(intent, 21);
	}

	OrderResultData orderResult;

	public void searchOrder() {
		VolleyEPayRequest.searchOrderPay(this, product.orderId, this);
	}

	public void updateExchange(int type) {
		if (type == 1) {// 兑换
			UserNow.current().totalPoint = ed.point;
			UserNow.current().diamond = ed.diamond;
			EventBus.getDefault().post(new UserInfoEvent());
		}
		tcreadits.setText("" + UserNow.current().totalPoint);
		totaldiamond.setText("" + UserNow.current().diamond);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 21 && resultCode == RESULT_OK) {
			Intent sIntent = new Intent(this, ChkDiamondService.class);
			sIntent.putExtra("orderId", product.orderId);
			startService(sIntent);

			if (mDiamondData.canPayCount != 0) {
				for (int i = 0; i < list.size(); i++) {
					if (mDiamondData.id == list.get(i).id) {
						list.get(i).alreadyPayCount++;
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void openHelp(int type) {
		AccountHelpDialogFragment dialogFragment = AccountHelpDialogFragment
				.newInstance(type);
		dialogFragment.show(getSupportFragmentManager(), "help");
	}

	private int exchangeType = 1;// 1=宝石兑换积分，2=宝石兑换秀场币
	private static final int EXCHANGE_COIN = 2;
	private static final int EXCHANGE_POINT = 1;

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.earn_score:
			startActivity(new Intent(this, ShopPointsActivity.class));
			break;
		case R.id.hyue:
			openHelp(AccountHelpDialogFragment.HELP_TYPE_POINT);
			break;
		case R.id.hdiam:
			openHelp(AccountHelpDialogFragment.HELP_TYPE_GOLDEN);
			break;
		case R.id.hcredits:
			openHelp(AccountHelpDialogFragment.HELP_TYPE_DIAMOND);
			break;
		case R.id.payhistroy:
			startActivity(new Intent(this, PayHistoryActivity.class));
			break;
		case R.id.tv_exchange_coin:
			String diamondNum = coinDiamondNum.getText().toString();
			boolean check = checkDiamond(diamondNum);
			if (!check) {
				return;
			}
			exchangeType = EXCHANGE_COIN;
			dialog();
			break;
		case R.id.mtoc:
			exchangeType = EXCHANGE_POINT;
			String num = diamondnum.getText().toString();
			checkNum();
			check = checkDiamond(num);
			if (!check) {
				return;
			}
			dialog();
			break;
		case R.id.purchase:
			mDiamondData = (DiamondData) arg0.getTag();
			if (mDiamondData.canPayCount != 0
					&& mDiamondData.canPayCount <= mDiamondData.alreadyPayCount) {
				Toast.makeText(getApplicationContext(), "您已经参加该活动了",
						Toast.LENGTH_SHORT).show();
			} else {
				progressBar.setVisibility(View.VISIBLE);
				submitOrder(mDiamondData);
			}
			break;
		case R.id.query_coin:
//			new UshowManager(this).launchUserCenter();
		default:
			break;
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		totaldiamond.setText("" + UserNow.current().diamond);
	}

	@Override
	public void onUpdatePayRuleList(int errCode, ArrayList<DiamondData> payList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			list = payList;
			update();
		}

	}

	@Override
	public void onSubmitOrder(int errCode, Product product) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:

			break;
		case JSONMessageType.SERVER_CODE_OK:
			this.product = product;
			getExchageKey();
			type = 1;
			break;
		default:
			break;
		}
		progressBar.setVisibility(View.GONE);

	}

	@Override
	public void OnExchangeVirtualMoney(int errCode, ExchangeData exchangeData) {
		progressBar.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			ed = exchangeData;
			if (ed.success) {
				updateExchange(1);
				Toast.makeText(this, "兑换成功", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "兑换失败", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onGetExchangeKey(int errCode, EKeyData eKeyData) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			mEKeyData = eKeyData;
			if (type == 1) {
				openPay();
			} else if (type == 2) {
				exchageVirtual();
			}
		}

	}

	@Override
	public void onSearchOrderPay(int errCode, OrderResultData orderResult) {
		if (errCode == JSONMessageType.SERVER_CODE_ERROR) {
			this.orderResult = orderResult;
			if (orderResult.paySuccess) {
				Toast.makeText(this, "充值成功", Toast.LENGTH_SHORT).show();
				UserNow.current().diamond += mDiamondData.num;
				updateExchange(2);
				LocalInfo.SaveUserData(this, true);
			} else {
				searchOrder();
			}
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.payRuleList);
		VolleyHelper.cancelRequest(Constants.submitOrder);
		VolleyHelper.cancelRequest(Constants.exchangeVirtual);
		VolleyHelper.cancelRequest(Constants.getExchangeKey);
		VolleyHelper.cancelRequest(Constants.searchOrderPay);
	}

	class MyTextWatcher implements TextWatcher {
		private EditText mEditText;

		public MyTextWatcher(EditText mEditText) {
			this.mEditText = mEditText;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			String input = s.toString();
			if (!TextUtils.isEmpty(input)) {
				long longInput = Long.parseLong(input);
				if (longInput == 0) {
					Toast.makeText(getApplicationContext(), "一次至少兑换1个宝石",
							Toast.LENGTH_SHORT).show();
				}
				if (mEditText.getId() == R.id.coin_diamond_num) {
					coinNum.setText("" + longInput * 500);
				} else {
					creditnum.setText("" + longInput * 50);
				}

			}
		}
	}
}
