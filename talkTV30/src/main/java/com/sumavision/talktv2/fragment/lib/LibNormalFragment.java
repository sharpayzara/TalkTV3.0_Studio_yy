package com.sumavision.talktv2.fragment.lib;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ContentType;
import com.sumavision.talktv2.bean.EventMessage;
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

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 电视剧、海外剧、综艺、动漫类型
 * 
 * @author suma-hpb
 * 
 */
public class LibNormalFragment extends TabFragment {

	private int columnId;
	private int programType;
	private int tagId;
	private boolean needLoadData = true;
	private boolean hasFilter;
	public ArrayList<ContentType> listContent;
	private FilterListener mListener;
	public static final int REQUEST_CODE = 0;

	public static LibNormalFragment newInstance(int columnId, int programType,int tagId) {
		LibNormalFragment fragment = new LibNormalFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_lib_tab);
		bundle.putInt("columnId", columnId);
		bundle.putInt("programType", programType);
		bundle.putInt("tagId",tagId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("LibNormalFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("LibNormalFragment");
	}
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mListener = (FilterListener) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnId = getArguments().getInt("columnId");
		programType = getArguments().getInt("programType");
		tagId = getArguments().getInt("tagId");
		EventBus.getDefault().register(this);
	}

	public void onStart() {
		super.onStart();
		if (needLoadData) {
			request();
		}
	};

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
								hasFilter = libParser.hasFilter;
								listContent = libParser.listContent;
								if (hasFilter || listContent != null) {
									update();
								}
							} else {
								showErrorLayout();
							}
						}
					}
				}, this);
	}

	ViewPager mViewPager;
	PagerSlidingTabStrip tabs;
	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
		tabs = (PagerSlidingTabStrip) view
				.findViewById(R.id.tabs);
		initViewpageAndTabs(mViewPager, tabs);
	}

	private void update() {
		int contentType;
		int initPos = 0;
		ArrayList<String> titles = new ArrayList<String>();
//		titles.add(getActivity().getString(R.string.select));
//		fragments.add(LibNormalSelectFragment
//				.newInstance(columnId, programType, 0, 0));
		for (int i = 0; i < listContent.size(); i++) {
			contentType = listContent.get(i).id;
			if (tagId>0 && contentType == tagId){
				initPos = i;
			}
			titles.add(listContent.get(i).name);
			if (listContent.get(i).style >1){
				fragments.add(LibNormalSelectFragment.newInstance(columnId,
						programType, contentType, Integer.parseInt(listContent.get(i).type)));
			} else {
				if (listContent.get(i).style == 0){
					fragments.add(LibRecBoardFragment
							.newInstance(columnId, programType, 0, 0));
				}else {
					fragments.add(LibRecBoardFragment.newInstance(columnId,
							programType, contentType, Integer.parseInt(listContent.get(i).type)));
				}
			}
		}
		if (hasFilter) {
			titles.add(0, getActivity().getString(R.string.filter));
			fragments.add(0, FilterFragment.newInstance(programType, columnId));
		}
		updateTabs(titles, fragments, -1);
		if (titles.size() <= 1) {
			tabs.setVisibility(View.GONE);
		}
		if (hasFilter) {
			mViewPager.setCurrentItem(initPos+1);
		} else {
			mViewPager.setCurrentItem(initPos);
		}
		hideLoadingLayout();

		mListener.setFilterBtn(hasFilter);
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		request();
	}

	@Override
	public void onPageSelected(int position) {
	}

	public void changePagerToFilter() {
		if (hasFilter && mViewPager != null) {
			mViewPager.setCurrentItem(0);
		}
	}

	public interface FilterListener {
		public void setFilterBtn(boolean visible);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.vaultColumnsRecommendDetail);
		EventBus.getDefault().unregister(this);
	}
	public void onEvent(EventMessage msg){
		if (msg.name.equals("LibNormalFragment")){
			tagId = msg.bundle.getInt("tagId");
			if (tagId != 0){
				for (int i=0; i<listContent.size(); i++){
					if (tagId>0 && listContent.get(i).id == tagId){
						if (hasFilter) {
							mViewPager.setCurrentItem(i+1);
						} else {
							mViewPager.setCurrentItem(i);
						}
						return;
					}
				}
			}
		}
	}

}
