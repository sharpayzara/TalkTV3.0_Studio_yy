package com.sumavision.talktv2.fragment.lib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.fragment.BaseFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.RankingUpdateEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.RankingDetailParser;
import com.sumavision.talktv2.http.json.RankingDetailRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ViewHolder;

import de.greenrobot.event.EventBus;

/**
 * 单个排行榜类型详情
 * 
 * @author suma-hpb
 * 
 */
public class RankingDetailFragment extends BaseFragment implements OnItemClickListener {
	public static RankingDetailFragment newInstance(int columnId) {
		RankingDetailFragment fragment = new RankingDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_ranking_detail);
		bundle.putInt("columnId", columnId);
		fragment.setArguments(bundle);
		return fragment;
	}

	private static final int MOVIE_ID = 55;
	private PullToRefreshListView ptrListView;
	private boolean needLoadData = true;
	private int columnId;

	private MyListAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		columnId = getArguments().getInt("columnId");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			doRequest();
		}
	}

	public void onEvent(RankingUpdateEvent e) {
		request(true);
	}

	public void doRequest() {
		if (needLoadData) {
			showLoadingLayout();
			request(true);
		}
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ptrListView = (PullToRefreshListView) view.findViewById(R.id.list_rank_program);
		ptrListView.setMode(Mode.BOTH);
		ptrListView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(lastUpdate);
		ptrListView.setMode(Mode.DISABLED);
		ptrListView.setOnItemClickListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		adapter = new MyListAdapter(getActivity(), listRankingDetail);
		ptrListView.setAdapter(adapter);
	}

	RankingDetailParser parser = new RankingDetailParser();
	private ArrayList<VodProgramData> listRankingDetail = new ArrayList<VodProgramData>();

	private void request(final boolean refresh) {
		VolleyHelper.post(new RankingDetailRequest(columnId).make(), new ParseListener(parser) {
			@Override
			public void onParse(BaseJsonParser sparser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					ptrListView.setVisibility(View.VISIBLE);
					ptrListView.onRefreshComplete();
					if (parser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						if (refresh) {
							listRankingDetail.clear();
						}
						listRankingDetail.addAll(parser.listRankingDetail);
						update();
						if (listRankingDetail == null || listRankingDetail.size() == 0) {
							showEmptyLayout("暂无数据");
						}
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	public void update() {
		adapter.notifyDataSetChanged();
		hideLoadingLayout();
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		showLoadingLayout();
		request(true);
	}

	/**
	 * 排行榜详情adapter
	 * */
	private class MyListAdapter extends IBaseAdapter<VodProgramData> {
		public MyListAdapter(Context context, List<VodProgramData> objects) {
			super(context, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_ranking_detail, null);
			}
			ImageView iconView = ViewHolder.get(convertView, R.id.pic);
			ImageView rankImg = ViewHolder.get(convertView, R.id.rank);
			TextView titleTxt = ViewHolder.get(convertView, R.id.title);
			TextView updateTxt = ViewHolder.get(convertView, R.id.update);
			TextView shortIntroTxt = ViewHolder.get(convertView, R.id.short_intro);
			TextView favouriteTxt = ViewHolder.get(convertView, R.id.favourite);

			VodProgramData data = getItem(position);
			loadImage(iconView, data.pic, R.drawable.aadefault);
			titleTxt.setText(data.name);
			if (position == 0) {
				rankImg.setVisibility(View.VISIBLE);
				rankImg.setImageResource(R.drawable.ic_rank_number1);
			} else if (position == 1) {
				rankImg.setVisibility(View.VISIBLE);
				rankImg.setImageResource(R.drawable.ic_rank_number2);
			} else if (position == 2) {
				rankImg.setVisibility(View.VISIBLE);
				rankImg.setImageResource(R.drawable.ic_rank_number3);
			} else {
				rankImg.setVisibility(View.GONE);
			}
			if (MOVIE_ID != columnId) {
				updateTxt.setVisibility(View.VISIBLE);
				updateTxt.setText(data.updateName);
			} else {
				updateTxt.setVisibility(View.GONE);
			}
			shortIntroTxt.setText(data.shortIntro);
			favouriteTxt.setText(data.monthGoodCount + "");
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		VodProgramData program = listRankingDetail.get((int) id);
		String programId = String.valueOf(program.id);
		String topicId = program.topicId;
		openProgramDetailActivity(programId, topicId, program.name, 0);
	}

	public void openProgramDetailActivity(String id, String topicId, String updateName, long cpId) {
		Intent intent = new Intent(getActivity(), PlayerActivity.class);
		long programId = Long.valueOf(id);
		long longTopicId = 0;
		if (!TextUtils.isEmpty(topicId)) {
			longTopicId = Long.valueOf(topicId);
		}
		intent.putExtra("isHalf", true);
		intent.putExtra("id", programId);
		intent.putExtra("topicId", longTopicId);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
		intent.putExtra("where",2);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		VolleyHelper.cancelRequest(Constants.columnProgramEvaluateList);
	}
}
