package com.sumavision.talktv2.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ChannelDetailActivity;
import com.sumavision.talktv2.adapter.ChannelDetailAdapter;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnChannelDetailListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.Constants;

public class ChannelDetailFragment extends BaseFragment implements
		OnChannelDetailListener, OnItemClickListener {

	private int day;
	private int today;
	private int channelId;

	public static ChannelDetailFragment newInstance(int channelId, int day,
			int today) {
		ChannelDetailFragment fragment = new ChannelDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_channel_detail);
		bundle.putInt("day", day);
		bundle.putInt("today", today);
		bundle.putInt("channelId", channelId);
		fragment.setArguments(bundle);
		return fragment;

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		day = getArguments().getInt("day");
		today = getArguments().getInt("today");
		channelId = getArguments().getInt("channelId");
	}

	private ListView listView;
	public ChannelDetailAdapter adapter;

	@Override
	protected void initViews(View view) {
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		initLoadingLayout();
		showLoadingLayout();
		// if (today == day) {
		// loadData();
		// }
	}

	boolean isVisible;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		isVisible = isVisibleToUser;
		if (isVisibleToUser) {
			loadData();
			if (adapter != null && isVisible) {
				((ChannelDetailActivity) mActivity)
						.setChannelDetailAdapter(adapter);
			}
		}
	}

	@Override
	public void reloadData() {
		showLoadingLayout();
		getChannelData(channelId, getDate(day), getPlayType(day));
	}

	public boolean needLoad = true;

	private void loadData() {
		if (needLoad) {
			getChannelData(channelId, getDate(day), getPlayType(day));
		}
	}

	private void getChannelData(int channelId, String date, int isToday) {
		VolleyProgramRequest.getChannelDetail(UserNow.current().userID,
				channelId, date, this, isToday, this);
	}

	public String getDate(int position) {
		Date date = new Date();
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		Date dateNew = new Date(date.getTime() - (dayOfWeek - position) * 3600
				* 24 * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(dateNew);
	}

	private int getPlayType(int from) {
		if (from > today) {
			return 1;
		} else if (from == today) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public void channelDetailResult(int errCode, String errmsg,
			ChannelData channel, int playItemPos) {
		needLoad = false;
		switch (errCode) {
		case 2:
			showEmptyLayout(getString(R.string.no_prgroam_data));
			break;
		case JSONMessageType.SERVER_CODE_ERROR:
			if ("频道尚未发布".equals(errmsg)) {
				showEmptyLayout(getString(R.string.no_prgroam_data));
			} else {
				showErrorLayout();
			}
			break;
		case JSONMessageType.SERVER_CODE_OK:
			hideLoadingLayout();
			((ChannelDetailActivity) mActivity).channelData.netPlayDatas = channel.netPlayDatas;
			updateList(channel.cpList, playItemPos);
			break;
		default:
			break;
		}
	}

	private void updateList(ArrayList<CpData> list, int playItemPos) {
		if (list != null && list.size() > 0) {
			adapter = new ChannelDetailAdapter(mActivity, list);
			listView.setAdapter(adapter);
			if (adapter != null && isVisible) {
				((ChannelDetailActivity) mActivity)
						.setChannelDetailAdapter(adapter);
			}
			listView.setSelection(playItemPos);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CpData cpData = adapter.getItem((int) id);
		if (cpData.isPlaying == CpData.TYPE_LIVE) {
			((ChannelDetailActivity) getActivity()).onLiveBtnClick(position,
					true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.channelContent);
	}
}
