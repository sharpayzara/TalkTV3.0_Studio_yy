package com.sumavision.talktv2.fragment.lib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.SpecialActivity;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.fragment.BaseFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.SpecialParser;
import com.sumavision.talktv2.http.json.SpecialRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ViewHolder;
import com.umeng.analytics.MobclickAgent;

/**
 * 专题列表
 * 
 * @author suma-hpb
 * 
 */
public class SpecialListFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener {

	private int columnId;
	public static int SUB_PROGRAM = 15;
	private boolean needLoadData = true;

	public static SpecialListFragment newInstance(int columnId) {
		SpecialListFragment fragment = new SpecialListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_special_list);
		bundle.putInt("columnId", columnId);
		fragment.setArguments(bundle);
		return fragment;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		columnId = getArguments().getInt("columnId");
	};

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SpecialListFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SpecialListFragment");
	}

	PullToRefreshListView specialListView;
	SpecialListAdapter specialAdapter;
	private ArrayList<ColumnData> specialList = new ArrayList<ColumnData>();

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		specialListView = (PullToRefreshListView) view.findViewById(R.id.list_special);
		specialListView.setMode(Mode.PULL_FROM_END);
		specialListView.setOnItemClickListener(this);
		specialListView.setOnRefreshListener(this);
		specialListView.setPullToRefreshOverScrollEnabled(false);
		specialAdapter = new SpecialListAdapter(mActivity, specialList);
		specialListView.setAdapter(specialAdapter);
	}

	public void onStart() {
		super.onStart();
		if (needLoadData) {
			showLoadingLayout();
			request(0, 10);
		}
	};

	SpecialParser sParser = new SpecialParser();

	private void request(int first, int count) {
		VolleyHelper.post(new SpecialRequest(columnId, first, count).make(), new ParseListener(sParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					specialListView.onRefreshComplete();
					if (sParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						specialList.addAll(sParser.columnList);
						if (sParser.columnList.size() < 10) {
							specialListView.setMode(Mode.DISABLED);
						}
						if (specialList != null && specialList.size() > 0) {
							update();
						} else {
							showEmptyLayout("暂无数据");
						}
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	private void update() {
		specialAdapter.notifyDataSetChanged();
		hideLoadingLayout();
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		showLoadingLayout();
		request(0, 10);
	}

	class SpecialListAdapter extends IBaseAdapter<ColumnData> {

		public SpecialListAdapter(Context context, List<ColumnData> objects) {
			super(context, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_special_list, null);
			}
			TextView titleTxt = ViewHolder.get(convertView, R.id.tv_title);
			ImageView horizPic = ViewHolder.get(convertView, R.id.imagv_pic);
			titleTxt.setText(getItem(position).name);
			loadImage(horizPic, getItem(position).pic, R.drawable.emergency_pic_bg_detail);
			return convertView;
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		request(specialList.size(), 10);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
		int realPosition = (int) id;
		int columnId = specialList.get(realPosition).id;
		int type = specialList.get(realPosition).type;
		String headPic = specialList.get(realPosition).pic;
		Intent intent = new Intent(getActivity(), SpecialActivity.class);
		intent.putExtra("columnId", columnId);
		intent.putExtra("pic", headPic);
		if (type == SUB_PROGRAM) {
			intent.putExtra("isSub", true);
		} else {
			intent.putExtra("isSub", false);
		}
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.subColumnList);
	}

}
