package com.sumavision.talktv2.fragment.lib;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ContentType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.fragment.TabFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.LibNormalParser;
import com.sumavision.talktv2.http.json.LibNormalRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

/**
 * 子节目类页面
 * 
 * @author suma-hpb
 * 
 */
public class LibSubProgramFragment extends TabFragment {

	public static LibSubProgramFragment newInstance(int columnId) {
		LibSubProgramFragment fragment = new LibSubProgramFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_lib_tab);
		bundle.putInt("columnId", columnId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnId = getArguments().getInt("columnId");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LibSubProgramFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LibSubProgramFragment");
	}

	private int columnId;
	// private int programType;
	// private boolean hasFilter;
	private boolean needLoadData = true;
	private ArrayList<String> typeList = new ArrayList<String>();
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	private ViewPager mViewPager;
	public static final int REQUEST_CODE = 0;
	PagerSlidingTabStrip tabs;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
		initViewpageAndTabs(mViewPager, tabs);
	}

	public void onStart() {
		super.onStart();
		if (needLoadData) {
			request();
		}
	};

	ArrayList<ContentType> listContent;
	LibNormalParser libParser = new LibNormalParser(REQUEST_CODE);

	private void request() {
		showLoadingLayout();
		VolleyHelper.post(new LibNormalRequest(columnId, 0, 10).make(),
				new ParseListener(libParser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						if (getActivity() != null
								&& !getActivity().isFinishing()) {
							if (libParser.errCode == JSONMessageType.SERVER_CODE_OK) {
								needLoadData = false;
								// programType = libParser.columnProgramType;
								// hasFilter = libParser.hasFilter;
								listContent = libParser.listContent;
								update();
							} else {
								showErrorLayout();
							}
						}
					}
				}, this);
	}

	private void update() {
		typeList.add(getString(R.string.select));
		fragments.add(LibSubProgramSelectFragment.newInstance(columnId));
		for (int i = 0; i < listContent.size(); i++) {
			typeList.add(listContent.get(i).name);
			fragments.add(LibSubProgramDetailFragment.newInstance(columnId,
					Integer.parseInt(listContent.get(i).type),
					listContent.get(i).id));
		}
		updateTabs(typeList, fragments, -1);
		if (typeList.size() <= 1) {
			tabs.setVisibility(View.GONE);
		}
		hideLoadingLayout();
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		request();
	}

	@Override
	public void onPageSelected(int position) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.vaultColumnsRecommendDetail);
	}
}
