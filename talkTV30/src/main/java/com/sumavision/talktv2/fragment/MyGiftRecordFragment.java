package com.sumavision.talktv2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.AddressActivity;
import com.sumavision.talktv2.activity.BaseActivity;
import com.sumavision.talktv2.activity.GoodsReceiveActivity;
import com.sumavision.talktv2.activity.HolidayResultActivity;
import com.sumavision.talktv2.activity.RealGoodsActivity;
import com.sumavision.talktv2.activity.ShoppingHomeActivity;
import com.sumavision.talktv2.adapter.MyGiftAdapter;
import com.sumavision.talktv2.application.TalkTvApplication;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.MyGiftEvent;
import com.sumavision.talktv2.http.listener.eshop.OnUserGoodsListener;
import com.sumavision.talktv2.http.request.VolleyEshopRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 用户礼品页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyGiftRecordFragment extends BaseFragment implements OnClickListener,
		OnItemClickListener, OnUserGoodsListener, OnRefreshListener2<ScrollView> {

	private StaticGridView goodsGrid;

	private RelativeLayout noGetGiftLayout, noGetNoGiftLayout;

	private PullToRefreshScrollView scrollView;

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		EventBus.getDefault().register(this);
	}
	public static MyGiftRecordFragment newInstance(){
		MyGiftRecordFragment fragment = new MyGiftRecordFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.activity_my_gift);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		performUserGoddsTask();
		return rootView;
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		scrollView = (PullToRefreshScrollView) view.findViewById(R.id.scrollview);
		scrollView.setMode(Mode.PULL_FROM_START);
		scrollView.setOnRefreshListener(this);
		noGetGiftLayout = (RelativeLayout) view.findViewById(R.id.gift);
		noGetNoGiftLayout = (RelativeLayout) view.findViewById(R.id.no_gift);
		TextView tip = (TextView) view.findViewById(R.id.tip);
		tip.setOnClickListener(this);
		tip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tip.getPaint().setAntiAlias(true);
		goodsGrid = (StaticGridView) view.findViewById(R.id.grid_gift);
		goodsAdapter = new MyGiftAdapter(getActivity(), allList);
		goodsGrid.setAdapter(goodsAdapter);
		goodsGrid.setOnItemClickListener(this);
	}

	@Override
	public void reloadData() {
		performUserGoddsTask();
	}

	private void performUserGoddsTask() {
		showLoadingLayout();
		VolleyEshopRequest.getUserGoods(this, this);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("MyGiftRecordFragment");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyGiftRecordFragment");
		super.onPause();
	}

	private ArrayList<ExchangeGood> allList = new ArrayList<ExchangeGood>();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tip:
			startActivity(new Intent(getActivity(), ShoppingHomeActivity.class));
			break;
		case R.id.err_text:
			performUserGoddsTask();
			break;
		default:
			break;
		}
	}

	private static final int REQUEST_CODE_RECEIVE = 1;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ExchangeGood goods = (ExchangeGood) parent.getItemAtPosition(position);
		if (goods.isHolidayGoods){
			Intent holiday = new Intent(getActivity(),HolidayResultActivity.class);
			holiday.putExtra("goodsId",goods.id);
			holiday.putExtra("isAward",true);
			startActivity(holiday);
			return;
		}
		if (goods.fetchType == 4 || goods.type == 5) {
			return;
		} else if (goods.type != 3) {
			Intent intent = new Intent();
			intent.putExtra("userGoodsId", goods.userGoodsId);
			intent.putExtra("goodsType", goods.type);
			intent.putExtra("status", goods.status);
			intent.putExtra("clickIndex", position);
			if (goods.type == ExchangeGood.TYPE_VIRTUAL || goods.status == ExchangeGood.STATUS_RECEIVED
					|| goods.status == ExchangeGood.STATUS_OVER) {
				intent.setClass(getActivity(), GoodsReceiveActivity.class);
			} else if (((TalkTvApplication) getActivity().getApplication()).mAddressData != null) {
				intent.setClass(getActivity(), RealGoodsActivity.class);
			} else {
				intent.setClass(getActivity(), AddressActivity.class);
			}
			startActivityForResult(intent, REQUEST_CODE_RECEIVE);
		}
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg0 == REQUEST_CODE_RECEIVE && arg1 == Activity.RESULT_OK && arg2 != null) {
			int clickIndex = arg2.getIntExtra("clickIndex", -1);
			if (clickIndex >= 0) {
				ExchangeGood goods = allList.get(clickIndex);
				goods.status = ExchangeGood.STATUS_RECEIVED;
				goodsAdapter.notifyDataSetChanged();
			}
		}
	}

	MyGiftAdapter goodsAdapter;

	@Override
	public void onGetUserGoodsList(int errCode,
			ArrayList<ExchangeGood> goodsList) {
		scrollView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
//			unreceiveTip.setVisibility(View.VISIBLE);
			if (allList.size() > 0) {
				allList.clear();
			}
			allList.addAll(goodsList);
			if (allList.size() == 0) {
				noGetGiftLayout.setVisibility(View.GONE);
				noGetNoGiftLayout.setVisibility(View.VISIBLE);
			} else {
				goodsAdapter.notifyDataSetChanged();
			}
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		VolleyHelper.cancelRequest(Constants.userGoodsList);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		VolleyEshopRequest.getUserGoods(this, this);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
		
	}
	
	public void onEvent(MyGiftEvent e) {
		if (e.clickIndex >= 0 && e.clickIndex < allList.size() && allList != null) {
			ExchangeGood goods = allList.get(e.clickIndex);
			goods.status = ExchangeGood.STATUS_RECEIVED;
			goodsAdapter.notifyDataSetChanged();
		}
	}

}
