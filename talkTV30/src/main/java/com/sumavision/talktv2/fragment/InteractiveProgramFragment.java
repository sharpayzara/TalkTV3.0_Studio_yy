package com.sumavision.talktv2.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.InteractiveDetailActivity;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.GuessVideoAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnGuessProgramListListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 互动详情-节目
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveProgramFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener, OnGuessProgramListListener {

	private int activityId;
	private int interactiveStatus;

	public static InteractiveProgramFragment newInstance(int activityId, int interactiveStatus) {
		InteractiveProgramFragment fragment = new InteractiveProgramFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("activityId", activityId);
		bundle.putInt("interactiveStatus", interactiveStatus);
		bundle.putInt("resId", R.layout.fragment_interactive_comment);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InteractiveProgramFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InteractiveProgramFragment");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityId = getArguments().getInt("activityId", 0);
		interactiveStatus = getArguments().getInt("interactiveStatus", 0);
	}

	private RelativeLayout noDataLayout;
	private ImageView noDataImageTip;
	private TextView noDataTextTip;
	private RelativeLayout errLayout;
	private TextView errTxt;
	private ProgressBar mprogressBar;
	private PullToRefreshListView listView;
	private ArrayList<VodProgramData> mProgramList = new ArrayList<VodProgramData>();
	private GuessVideoAdapter videoAdapter;

	@Override
	protected void initViews(View view) {
		listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
		listView.setPullToRefreshOverScrollEnabled(false);
		noDataLayout = (RelativeLayout) rootView.findViewById(R.id.no_list_tip);
		noDataImageTip = (ImageView) rootView.findViewById(R.id.no_list_pic);
		noDataTextTip = (TextView) rootView.findViewById(R.id.no_list_text);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		errLayout.setClickable(false);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		mprogressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		videoAdapter = new GuessVideoAdapter(getActivity(), mProgramList);
		listView.setAdapter(videoAdapter);
		listView.setOnItemClickListener(this);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		String label = getString(R.string.mylistview_update) + format.format(new Date());
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		listView.setOnRefreshListener(this);

	}

	private boolean firstLoad = true;
	private boolean refresh = false;

	public void loadData() {
		if (firstLoad) {
			getProgramList(0, 10);
		}
	}

	private void getProgramList(int start, int count) {
		if (start == 0) {
			refresh = true;
			listView.setMode(Mode.BOTH);
		} else {
			refresh = false;
		}
		VolleyInteractionRequest.guessProgramList(this, start, count, activityId, this);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getProgramList(0, 10);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getProgramList(mProgramList.size(), 10);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VodProgramData program = (VodProgramData) parent.getItemAtPosition(position);
		if (program.playType != 1) {
			openProgramActivity(program);
		} else {
			openPlayerActivity(program);
		}

	}

	private void openPlayerActivity(VodProgramData programData) {
		NetPlayData liveData = programData.netPlayDatas.get(0);
		// liveData.videoPath =
		// "http://hot.vrs.sohu.com/ipad907366_4566197119545_228498.m3u8";
		if (TextUtils.isEmpty(liveData.url) && TextUtils.isEmpty(liveData.videoPath)) {
			Toast.makeText(getActivity(), "无播放地址", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = null;
		if (TextUtils.isEmpty(liveData.url) || liveData.url.endsWith(".png") || liveData.url.endsWith(".jpg")) {
			intent = new Intent(getActivity(), WebAvoidPicActivity.class);
		} else {
			intent = new Intent(getActivity(), WebAvoidActivity.class);
		}
		intent.putExtra("programPic", programData.pic);
		intent.putExtra("path", liveData.videoPath);
		intent.putExtra("url", liveData.url);
		// intent.putExtra("fromString", src);
		intent.putExtra("playType", 1);
		intent.putExtra("title", "电视直播");
		intent.putExtra("id", programData.id);
		intent.putExtra("nameHolder", programData.name);
		intent.putExtra("interactiveStatus", interactiveStatus);
		intent.putExtra("activityId", activityId);
		long delayOver = ((InteractiveDetailActivity) getActivity()).delayInteractiveOver;
		intent.putExtra("delayOver", delayOver);
		startActivity(intent);
	}

	private void openProgramActivity(VodProgramData program) {
		Intent intent = new Intent(getActivity(), ProgramDetailHalfActivity.class);
		intent.putExtra("programId", Long.parseLong(program.id));
		long topicId = 0l;
		if (!TextUtils.isEmpty(program.topicId)) {
			topicId = Long.parseLong(program.topicId);
		}
		intent.putExtra("topicId", topicId);
		intent.putExtra("type", program.ptype);
		intent.putExtra("interactiveStatus", interactiveStatus);
		intent.putExtra("activityId", activityId);

		startActivity(intent);
	}

	@Override
	public void onGetGuessProgramlist(int errCode, ArrayList<VodProgramData> programList) {
		firstLoad = false;
		errLayout.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (refresh) {
				mProgramList.clear();
			}
			mProgramList.addAll(programList);
			if (mProgramList.size() == 0) {
				listView.setVisibility(View.GONE);
				noDataLayout.setVisibility(View.VISIBLE);
				noDataImageTip.setImageResource(R.drawable.interactive_no_video);
				noDataTextTip.setText(R.string.interactive_no_video_tip);
			} else {
				noDataLayout.setVisibility(View.GONE);
				videoAdapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				if (programList.size() < 10) {
					listView.setMode(Mode.PULL_FROM_START);
				}
			}

		} else {
			errLayout.setVisibility(View.VISIBLE);
			errTxt.setVisibility(View.VISIBLE);
			mprogressBar.setVisibility(View.GONE);
		}

	}

	@Override
	public void reloadData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.interactiveGuessProgramList);
	}
}
