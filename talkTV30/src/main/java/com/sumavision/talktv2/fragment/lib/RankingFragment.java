package com.sumavision.talktv2.fragment.lib;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RankingTitleData;
import com.sumavision.talktv2.fragment.TabFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.RankingTitleParser;
import com.sumavision.talktv2.http.json.RankingTitleRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

/**
 * 点播排行榜
 * 
 * @author suma-hpb
 * 
 */
public class RankingFragment extends TabFragment {

	public static RankingFragment newInstance() {
		RankingFragment fragment = new RankingFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_lib_tab);
		fragment.setArguments(bundle);
		return fragment;
	}

	private List<RankingTitleData> listRankingTitle;
	private ArrayList<String> typeList = new ArrayList<String>();
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	PagerSlidingTabStrip tabs;

	private boolean needLoadData = true;

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("RankingFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("RankingFragment");
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		initViewpageAndTabs(mViewPager, tabs);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (needLoadData) {
			request();
		}
	}

	ArrayList<RankingTitleData> rankTypeList = new ArrayList<RankingTitleData>();
	RankingTitleParser parser = new RankingTitleParser();

	private void request() {
		VolleyHelper.post(new RankingTitleRequest().make(), new ParseListener(
				parser) {
			@Override
			public void onParse(BaseJsonParser sparser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						listRankingTitle = parser.listRankingTitle;
						loadData();
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	private void loadData() {
		hideLoadingLayout();
		if (listRankingTitle.size() > 0 && listRankingTitle != null) {
			for (int i = 0; i < listRankingTitle.size(); i++) {
				RankingDetailFragment fragment = RankingDetailFragment
						.newInstance(Integer.parseInt(listRankingTitle.get(i).columnId));
				fragments.add(fragment);
				typeList.add(listRankingTitle.get(i).columnName);
			}
			update();
		} else {
			showEmptyLayout("暂无数据");
		}
	}

	private void update() {
		updateTabs(typeList, fragments, -1);
		if (typeList.size() <= 1) {
			tabs.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPageSelected(int position) {
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		request();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.evaluateColumnList);
	}

}
