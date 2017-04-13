package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.suamvison.net.JSONMessageType;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.ActivityDetailActivity;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.MyVipInfoActivity;
import com.sumavision.talktv2.activity.ShakeActivity;
import com.sumavision.talktv2.activity.ShoppingHomeActivity;
import com.sumavision.talktv2.activity.WebPageActivity;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DiscoveryParser;
import com.sumavision.talktv2.http.json.DiscoveryRequest;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.service.TvBaiduPushMessageReceiver;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.RedTipTextView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashSet;

import de.greenrobot.event.EventBus;

/**
 * 新版发现页
 * 
 * @author cx
 * 
 */
public class FoundIndexFragment extends FocusLayoutFragment implements OnClickListener ,SharedPreferences.OnSharedPreferenceChangeListener
{

	private RedTipTextView activity;
	private RedTipTextView shop;
	private RedTipTextView shake;
	private RedTipTextView awardText;
	private RelativeLayout shakeLayout,duanwuLayout,vipLayout,awardLayout;
	private RelativeLayout rcmdLayout;
	private PullToRefreshScrollView scrollView;
	private SharedPreferences defaultSp,pushSp;
	
	private DiscoveryParser dparser = new DiscoveryParser();
	private int haveShake;
	private boolean loadData = true;

	public static FoundIndexFragment newInstance() {
		FoundIndexFragment fragment = new FoundIndexFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_found_index);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (loadData){
				doRequest();
			}
//			showRed();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		defaultSp = PreferencesUtils.getSharedPreferences(getActivity(),null);
		defaultSp.registerOnSharedPreferenceChangeListener(this);
		pushSp = PreferencesUtils.getSharedPreferences(getActivity(),Constants.pushMessage);
		pushSp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
//		showRed();
	}

	private void showRed(){
		if (PreferencesUtils.getBoolean(getActivity(),
				Constants.pushMessage, Constants.KEY_ACTIVITY,
				false)){
			if (activity != null){
				activity.setRedTip(RedTipTextView.RED_TIP_VISIBLE);
			}
		}
		if (PreferencesUtils.getBoolean(getActivity(),
				Constants.pushMessage, Constants.KEY_GOODS,
				false)){
			if (shop != null){
				shop.setRedTip(RedTipTextView.RED_TIP_VISIBLE);
			}
		}
	}
	
	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		initStarsLayout();
		hideStarsLayout();
		
		activity = (RedTipTextView) view.findViewById(R.id.activity_title);
		shop = (RedTipTextView) view.findViewById(R.id.shop_title);
		shake = (RedTipTextView) view.findViewById(R.id.shake_title);
		rcmdLayout = (RelativeLayout) view.findViewById(R.id.rcmd_layout);
		shakeLayout = (RelativeLayout) view.findViewById(R.id.shake_layout);
		view.findViewById(R.id.shop_layout).setOnClickListener(this);
		view.findViewById(R.id.activity_layout).setOnClickListener(this);
		shakeLayout.setOnClickListener(this);
		rcmdLayout.addView(headerView);
		duanwuLayout = (RelativeLayout) view.findViewById(R.id.duanwu_layout);
		duanwuLayout.setOnClickListener(this);
		vipLayout = (RelativeLayout) view.findViewById(R.id.vip_layout);
		vipLayout.setOnClickListener(this);
		awardLayout = (RelativeLayout) view.findViewById(R.id.award_layout);
		awardLayout.setOnClickListener(this);
		awardText = (RedTipTextView) view.findViewById(R.id.award_tip);
		scrollView = (PullToRefreshScrollView) view.findViewById(R.id.found_scrollview);
		scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				doRequest();
			}
		});
		showRed();
	}
	
	private void doRequest() {
		showLoadingLayout();
		VolleyHelper.post(new DiscoveryRequest().make(), new ParseListener(dparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (scrollView != null){
					scrollView.onRefreshComplete();
				}
				if (dparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					haveShake = dparser.haveShake;
					updateView(dparser.listRecommend);
				}
				hideLoadingLayout();
				loadData = false;
			}
		}, new OnHttpErrorListener() {
			@Override
			public void onError(int code) {
				scrollView.onRefreshComplete();
			}
		});
	}

	private void updateView(ArrayList<RecommendData> listRecommend) {
		if (rcmdLayout == null){
			return;
		}
		if (listRecommend.size() > 0 && getPicView() != null) {
			updateStarsLayout(listRecommend);
			showStarsLayout();
			rcmdLayout.setVisibility(View.VISIBLE);
		} else {
			hideStarsLayout();
			rcmdLayout.setVisibility(View.GONE);
		}
		if (haveShake == 0) {
			shakeLayout.setVisibility(View.GONE);
		} else {
			shakeLayout.setVisibility(View.VISIBLE);
		}
		if (dparser.hasDuanwu){
			duanwuLayout.setVisibility(View.VISIBLE);
		} else {
			duanwuLayout.setVisibility(View.GONE);
		}
		if (dparser.hasInvite){
			vipLayout.setVisibility(View.VISIBLE);
		} else {
			vipLayout.setVisibility(View.GONE);
		}
		if (dparser.hasLottery){
			awardLayout.setVisibility(View.VISIBLE);
			updateAwardTip();
		} else {
			awardLayout.setVisibility(View.GONE);
		}
		setAwardText(dparser.dayLottery);
		PreferencesUtils.putBoolean(getActivity(),Constants.pushMessage,Constants.KEY_DAYLOTTERY,dparser.dayLottery);
	}
	public void updateAwardTip(){
		if (PreferencesUtils.getString(getActivity(),null,"curDate_found_award_date","2015")
				.equals(CommonUtils.getDateString())
				&& PreferencesUtils.getStringSet(getActivity(),null,"curDate_found_award_ids",new HashSet<String>()).contains(UserNow.current().userID+"")){
			setAwardText(true);
		} else {
			setAwardText(false);
		}
	}
	public void setAwardText(boolean flag){
		if (awardText != null){
			if (flag){
				awardText.setText("明天再来吧");
				awardText.setRedTip(RedTipTextView.RED_TIP_GONE);
			} else {
				awardText.setText("100%中奖");
				awardText.setRedTip(RedTipTextView.RED_TIP_VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shop_layout:
			MobclickAgent.onEvent(getActivity(), "fxjifenshangcheng");
			if (PreferencesUtils.getBoolean(getActivity(),
					Constants.pushMessage, Constants.KEY_GOODS,
					false)){
				shop.setRedTip(RedTipTextView.RED_TIP_GONE);
				PreferencesUtils.putBoolean(getActivity(),
						Constants.pushMessage, Constants.KEY_GOODS,
						false);
				if (!PreferencesUtils.getBoolean(getActivity(),
						Constants.pushMessage, Constants.KEY_ACTIVITY,
						false)){
					PreferencesUtils.putBoolean(getActivity(),
							Constants.pushMessage, Constants.KEY_FOUND,
							false);
				}
			}
			getActivity().startActivity(new Intent(getActivity(), ShoppingHomeActivity.class));
			break;
		case R.id.activity_layout:
			MobclickAgent.onEvent(getActivity(), "fxhuizhanghuodong");
			if (PreferencesUtils.getBoolean(getActivity(),
					Constants.pushMessage, Constants.KEY_ACTIVITY,
					false)){
				activity.setRedTip(RedTipTextView.RED_TIP_GONE);
				PreferencesUtils.putBoolean(getActivity(),
						Constants.pushMessage, Constants.KEY_ACTIVITY,
						false);
				if (!PreferencesUtils.getBoolean(getActivity(),
						Constants.pushMessage, Constants.KEY_GOODS,
						false)){
					PreferencesUtils.putBoolean(getActivity(),
							Constants.pushMessage, Constants.KEY_FOUND,
							false);
				}
			}
			getActivity().startActivity(new Intent(getActivity(), ActivityDetailActivity.class));
			break;
		case R.id.shake_layout:
			MobclickAgent.onEvent(getActivity(), "fxyaojiapian");
			getActivity().startActivity(new Intent(getActivity(), ShakeActivity.class));
			break;
		case R.id.duanwu_layout:
			MobclickAgent.onEvent(getActivity(),"duanwuhuodong");
			Intent intent = new Intent(getActivity(), ActivityActivity.class);
			intent.putExtra("activityId",(long)dparser.activityId);
			intent.putExtra("activityName",dparser.activityName);
			intent.putExtra("isTemp",true);
			getActivity().startActivity(intent);
			break;
		case R.id.vip_layout:
			MobclickAgent.onEvent(getActivity(), "fxvip");
			startActivity(new Intent(getActivity(), MyVipInfoActivity.class));
			break;
		case R.id.award_layout:
			if (UserNow.current().userID>0){
				MobclickAgent.onEvent(getActivity(), "fxmeirichoujiang");
				Intent awardIntent = new Intent(getActivity(),WebPageActivity.class);
				awardIntent.putExtra("url",Constants.host.substring(0,Constants.host.lastIndexOf("/")+1)+"newweb/deckGame/game.jsp");
				awardIntent.putExtra("title","每日抽奖");
				startActivity(awardIntent);
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		default:break;
		}
	}
//	Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what){
//				case 1:
//					updateAwardTip();
//					break;
//			}
//		}
//	};
	public void onEvent(UserInfoEvent event){
		loadData = true;
		if (UserNow.current().userID <= 0){
			duanwuLayout.setVisibility(View.GONE);
			setAwardText(false);
		} else {
			if (rcmdLayout != null){
				doRequest();
			}
		}
//		handler.sendEmptyMessage(1);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		defaultSp.unregisterOnSharedPreferenceChangeListener(this);
		pushSp.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
		if (s.equals("curDate_found_award_date")){
			updateAwardTip();
		} else if (s.equals("curDate_found_award_ids")){
			updateAwardTip();
		} else if (s.equals(Constants.KEY_ACTIVITY) || s.equals(Constants.KEY_GOODS)){
			showRed();
		}
	}
}
