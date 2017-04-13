package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.ShopHomeAdapter;
import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.ShoppingHomeBean;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.listener.eshop.OnRecommendGoodsListListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author liwei
 * @description 商城首页
 * 
 */
public class ShoppingHomeActivity extends BaseActivity implements OnRecommendGoodsListListener, OnRefreshListener2<ListView>, OnClickListener {
	private PullToRefreshListView pullRefresh;
	private ListView mListView;
	private ShopHomeAdapter shopAdapter;
	private List<ShoppingHomeBean> mList = new ArrayList<ShoppingHomeBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping_home);
		getSupportActionBar().setTitle(getString(R.string.shop_title));
		initView();
		showLoadingLayout();
		getTaskResponse();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ShoppingHomeActivity");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(UserInfoEvent event){
		headScore.setText(String.valueOf(UserNow.current().totalPoint));
		if (UserNow.current().isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
	}
	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ShoppingHomeActivity");
		super.onResume();
		if (UserNow.current().userID > 0) {
			updateUserInfo();
		} else {
			headGender.setImageResource(R.drawable.shop_user_gift);
			headName.setVisibility(View.GONE);
			headBuying.setVisibility(View.GONE);
			headScore.setVisibility(View.GONE);
			headDiamand.setVisibility(View.GONE);
			headSign.setVisibility(View.VISIBLE);
			headSignTxt.setVisibility(View.VISIBLE);
			headPic.setImageResource(R.drawable.my_headpic);
		}
	}

	private void updateUserInfo() {
		loadImage(headPic, UserNow.current().iconURL, R.drawable.list_headpic_default);
		if (UserNow.current().isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		headBuying.setVisibility(View.VISIBLE);
		headScore.setVisibility(View.VISIBLE);
		headGender.setVisibility(View.GONE);
		headDiamand.setVisibility(View.VISIBLE);
		headSign.setVisibility(View.GONE);
		headSignTxt.setVisibility(View.GONE);

		if (UserNow.current().gender == 2) {
			headGender.setImageResource(R.drawable.uc_female_selected);
		} else
			headGender.setImageResource(R.drawable.uc_male_selected);

		String shopname = UserNow.current().name;
		if (shopname != null) {
			headName.setVisibility(View.VISIBLE);
			headName.setText(shopname);
		}
		headScore.setText(String.valueOf(UserNow.current().totalPoint));
		headDiamand.setText(String.valueOf(UserNow.current().diamond));
	}

	private ImageView headPic, headGender,vipMark;
	Button headBuying;
	private TextView headName, headScore, headDiamand, headSign, headSignTxt;

	private void initView() {
		View userInfoView = getLayoutInflater().inflate(R.layout.userinfo_layout, null);
		headBuying = (Button) userInfoView.findViewById(R.id.shop_pay_btn);
		headPic = (ImageView) userInfoView.findViewById(R.id.user_header);
		vipMark = (ImageView) userInfoView.findViewById(R.id.user_vip_mark);
		vipMark.setVisibility(View.GONE);
		headGender = (ImageView) userInfoView.findViewById(R.id.gender);
		headName = (TextView) userInfoView.findViewById(R.id.name);
		headScore = (TextView) userInfoView.findViewById(R.id.login_score);
		headDiamand = (TextView) userInfoView.findViewById(R.id.login_diamand);
		headSign = (TextView) userInfoView.findViewById(R.id.signnature);
		headSignTxt = (TextView) userInfoView.findViewById(R.id.signnature_txt);
		userInfoView.findViewById(R.id.userInfo_layout).setOnClickListener(this);
		userInfoView.findViewById(R.id.user_header_layout).setOnClickListener(this);
		headBuying.setOnClickListener(this);
		headScore.setOnClickListener(this);
		headDiamand.setOnClickListener(this);
		pullRefresh = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		pullRefresh.setPullToRefreshOverScrollEnabled(false);
		String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_ABBREV_ALL);
		pullRefresh.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		pullRefresh.setMode(Mode.DISABLED);
		mListView = pullRefresh.getRefreshableView();
		mListView.addHeaderView(userInfoView);
		initLoadingLayout();
	}

	@Override
	protected void reloadData() {
		getTaskResponse();
		showLoadingLayout();
	}

	private void getTaskResponse() {
		VolleyEshopRequest.GetRecommendGoodsList(this, this);
	}

	TextActionProvider myGiftAction;
	TextActionProvider earnPointAction;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.shopping, menu);
		myGiftAction = (TextActionProvider) menu.findItem(R.id.action_my_gift).getActionProvider();
		earnPointAction = (TextActionProvider) menu.findItem(R.id.action_earn_point).getActionProvider();
		if (myGiftAction == null || earnPointAction == null){
			return super.onCreateOptionsMenu(menu);
		}
		myGiftAction.setShowText(R.string.my_gift);
		myGiftAction.setOnClickListener(ActionProviderClickListener, "gift");
		earnPointAction.setShowText(R.string.earn_score_title);
		earnPointAction.setOnClickListener(ActionProviderClickListener, "point");
		return super.onCreateOptionsMenu(menu);
	}

	OnClickListener ActionProviderClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (UserNow.current().userID == 0) {
				openLoginActivity();
			} else {
				String tag = (String) v.getTag();
				if ("gift".equals(tag)) {
					startActivity(new Intent(getApplicationContext(), MyGiftActivity.class));
				} else {
					startActivity(new Intent(getApplicationContext(), ShopPointsActivity.class));
				}
			}

		}
	};

	private void updateView() {
		if (shopAdapter == null) {
			shopAdapter = new ShopHomeAdapter(this, mList);
			mListView.setAdapter(shopAdapter);
		} else {
			shopAdapter.notifyDataSetChanged();
		}

	}

	private void openLoginActivity() {
		startActivity(new Intent(this, LoginActivity.class));
	}

	@Override
	public void OnGetRecommendGoodsList(int errCode, ArrayList<HotGoodsBean> hotGoodsList) {
		pullRefresh.onRefreshComplete();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			showErrorLayout();
			break;
		case JSONMessageType.SERVER_CODE_OK:
			hideLoadingLayout();
			packList(hotGoodsList);
			updateView();
			break;
		default:
			break;
		}

	}

	private void packList(ArrayList<HotGoodsBean> hotGoodsList) {
		int size = hotGoodsList.size();
		for (int i = 0; i < size; i += 2) {
			ArrayList<HotGoodsBean> temp = new ArrayList<HotGoodsBean>();
			temp.add(hotGoodsList.get(i));
			if (i + 1 < size) {
				temp.add(hotGoodsList.get(i + 1));
			}
			ShoppingHomeBean bean = new ShoppingHomeBean();
			bean.hotGoods = temp;
			mList.add(bean);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getTaskResponse();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.name:
		case R.id.user_header_layout:
			if (UserNow.current().userID > 0) {
				MobclickAgent.onEvent(this, "zhanghaoshezhi");
				startActivity(new Intent(this, UserInfoEditActivity.class));
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		case R.id.shop_pay_btn:
			this.startActivity(new Intent(this, MyAccountActivity.class));
			break;
		case R.id.login_score:
		case R.id.login_diamand:
			Intent intent = new Intent(this, MyAccountActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.recommendGoodsList);
	}
}
