package com.sumavision.talktv2.fragment.lib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.SpecialProgramActivity;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.adapter.ProgramDetailChannelAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.fragment.BaseFragment;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.SpecialDetailParser;
import com.sumavision.talktv2.http.json.SpecialDetailRequest;
import com.sumavision.talktv2.http.json.SpecialSubDetailParser;
import com.sumavision.talktv2.http.json.SpecialSubDetailRequest;
import com.sumavision.talktv2.http.json.SubjectProgramListParser;
import com.sumavision.talktv2.http.json.SubjectProgramListRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ViewHolder;
import com.umeng.analytics.MobclickAgent;

/**
 * 专题详情
 * 
 * @author suma-hpb
 * 
 */
public class SpecialDetailFragment extends BaseFragment implements OnItemClickListener, OnRefreshListener2<ListView>, OnScrollListener {

	public static SpecialDetailFragment newInstance(boolean isSub, int programId, int columnId, String headPic, String title) {
		SpecialDetailFragment fragment = new SpecialDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_special_detail);
		bundle.putBoolean("isSub", isSub);
		bundle.putString("pic", headPic);
		bundle.putString("title", title);
		bundle.putInt("columnId", columnId);
		bundle.putInt("programId", programId);
		fragment.setArguments(bundle);
		return fragment;
	}

	private boolean isSub;
	private int programId;
	private int columnId;
	private String headPic;
	private String longIntro;
	private boolean needLoadData = true;
	private String title;
	PullToRefreshListView specialListView;
	SpecialDetailAdapter specialAdapter;
	private ArrayList<VodProgramData> specialList = new ArrayList<VodProgramData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isSub = getArguments().getBoolean("isSub", false);
		columnId = getArguments().getInt("columnId");
		headPic = getArguments().getString("pic");
		programId = getArguments().getInt("programId", -1);
		title = getArguments().getString("title");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SpecialDetailFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SpecialDetailFragment");
	}

	private View headerview;
	private ImageView introImg;
	private TextView introTxt;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		headerview = inflater.inflate(R.layout.header_special_detail, null);
		introImg = (ImageView) headerview.findViewById(R.id.img_intro_pic);
		introTxt = (TextView) headerview.findViewById(R.id.tv_intro);
		specialListView = (PullToRefreshListView) view.findViewById(R.id.list_special);
		specialListView.setMode(Mode.PULL_FROM_START);
		specialListView.setOnRefreshListener(this);
		specialListView.setPullToRefreshOverScrollEnabled(false);
		specialAdapter = new SpecialDetailAdapter(mActivity, isSub, specialList);
		specialListView.setAdapter(specialAdapter);
		specialListView.setOnItemClickListener(this);
		specialListView.setOnScrollListener(this);
		specialListView.getRefreshableView().addHeaderView(headerview);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (needLoadData) {
			showLoadingLayout();
			if (!isSub) {
				if (programId != -1) {
					if (title != null) {
						getSherlockActivity().getSupportActionBar().setTitle(title);
					}
					requestProgram(0, 10, true);
				} else {
					getSherlockActivity().getSupportActionBar().setTitle("专题详情");
					request(0, 10, true);
				}
			} else {
				getSherlockActivity().getSupportActionBar().setTitle("子节目专题详情");
				requestSub(0, 10, true);
			}
		}
	}

	SpecialDetailParser sParser = new SpecialDetailParser();

	private void request(int first, int count, final boolean refresh) {
		VolleyHelper.post(new SpecialDetailRequest(columnId, first, count).make(), new ParseListener(sParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					specialListView.onRefreshComplete();
					if (sParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						if (refresh) {
							specialList.clear();
						}
						specialList.addAll(sParser.columnList);
						if (sParser.columnList.size() < 10) {
							overloading = true;
						} else {
							overloading = false;
						}
						longIntro = sParser.longIntro;
						update(true);
					} else {
						introTxt.setVisibility(View.GONE);
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	SpecialSubDetailParser dParser = new SpecialSubDetailParser();

	private void requestSub(int first, int count, final boolean refresh) {
		VolleyHelper.post(new SpecialSubDetailRequest(columnId, first, count).make(), new ParseListener(dParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					specialListView.onRefreshComplete();
					if (dParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						if (refresh) {
							specialList.clear();
						}
						specialList.addAll(dParser.columnList);
						if (dParser.columnList.size() < 10) {
							overloading = true;
						} else {
							overloading = false;
						}
						longIntro = dParser.longIntro;
						update(true);
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	SubjectProgramListParser lParser = new SubjectProgramListParser();

	private void requestProgram(int first, int count, final boolean refresh) {
		VolleyHelper.post(new SubjectProgramListRequest(programId, first, count).make(), new ParseListener(lParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					specialListView.onRefreshComplete();
					if (lParser.errCode == JSONMessageType.SERVER_CODE_OK) {
						needLoadData = false;
						if (refresh) {
							specialList.clear();
						}
						specialList.addAll(lParser.programList);
						if (lParser.programList.size() < 10) {
							overloading = true;
						} else {
							overloading = false;
						}
						update(false);
					} else {
						showErrorLayout();
					}
				}
			}
		}, this);
	}

	private void update(boolean showHeader) {
		hideLoadingLayout();
		if (specialList == null || specialList.size() == 0) {
			showEmptyLayout("暂无数据");
			return;
		}
		if (showHeader) {
			headerview.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(headPic)) {
				introImg.setVisibility(View.VISIBLE);
				loadImage(introImg, headPic, R.drawable.emergency_pic_bg_detail);
			} else {
				introImg.setVisibility(View.GONE);
			}
			if (!TextUtils.isEmpty(longIntro)) {
				introTxt.setVisibility(View.VISIBLE);
				introTxt.setText(longIntro);
			} else {
				introTxt.setVisibility(View.GONE);
			}
		} else {
			headerview.setVisibility(View.GONE);
			specialListView.getRefreshableView().removeHeaderView(headerview);
		}
		specialAdapter.notifyDataSetChanged();
		loading = false;
	}

	@Override
	public void reloadData() {
		needLoadData = true;
		if (needLoadData) {
			showLoadingLayout();
			if (!isSub) {
				if (programId != -1) {
					requestProgram(0, 10, true);
				} else {
					request(0, 10, true);
				}
			} else {
				requestSub(0, 10, true);
			}
		}
	}

	class SpecialDetailAdapter extends IBaseAdapter<VodProgramData> {
		boolean isSub;

		public SpecialDetailAdapter(Context context, boolean isSub, List<VodProgramData> objects) {
			super(context, objects);
			this.isSub = isSub;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_special_detail, null);
			}
			TextView titleTxt = ViewHolder.get(convertView, R.id.tv_title);
			TextView totalTimeTxt = ViewHolder.get(convertView, R.id.tv_total_time);
			ImageView picView = ViewHolder.get(convertView, R.id.img_pic);
			titleTxt.setText(getItem(position).name);
			loadImage(picView, getItem(position).pic, R.drawable.aadefault);

			if (isSub) {
				totalTimeTxt.setVisibility(View.VISIBLE);
				String time = formatPlayLength(getItem(position).playLength);
				if (!time.equals("00:00:00")) {
					totalTimeTxt.setText(time);
				} else {
					totalTimeTxt.setVisibility(View.GONE);
				}
			} else {
				totalTimeTxt.setVisibility(View.GONE);
			}
			if (position % 2 == 0) {
				convertView.setBackgroundResource(R.drawable.fav_item_even_bg);
			} else {
				convertView.setBackgroundResource(R.drawable.fav_item_odd_bg);
			}
			return convertView;
		}

		private String formatPlayLength(int second) {
			int hour = second / 3600;
			int min = (second - hour * 3600) / 60;
			int sec = second - hour * 3600 - min * 60;
			return String.format("%02d:%02d:%02d", hour, min, sec);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (id < 0) {
			return;
		}
		int realPosition = (int) id;
		if (isSub) {
			// open playerActivity
			VodProgramData data = specialList.get(realPosition);
			if (TextUtils.isEmpty(data.subVideoPath) && TextUtils.isEmpty(data.playUrl)) {
				Toast.makeText(mActivity, "播放路径为空", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			if (!TextUtils.isEmpty(data.subVideoPath)) {
				intent.setClass(mActivity, WebAvoidPicActivity.class);
				intent.putExtra("path", data.subVideoPath);
				intent.putExtra("url", data.subVideoPath);
				intent.putExtra("where",2);
			} else {
				intent.setClass(mActivity, WebAvoidActivity.class);
				intent.putExtra("url", data.playUrl);
				intent.putExtra("where",2);
			}
			long topicId = 0;
			if (!TextUtils.isEmpty(data.topicId)) {
				topicId = Long.parseLong(data.topicId);
			}
			intent.putExtra("topicId", topicId);
			intent.putExtra("hideFav", true);
			intent.putExtra("id", Integer.parseInt(data.id));
			intent.putExtra("playType", PlayerActivity.VOD_PLAY);
			intent.putExtra("title", data.name);
			startActivity(intent);
		} else if (programId == -1) {
			if (specialList.get(realPosition).pcount > 0) {
				Intent intent = new Intent(mActivity, SpecialProgramActivity.class);
				intent.putExtra("isSub", false);
				intent.putExtra("title", specialList.get(realPosition).name);
				intent.putExtra("programId", Integer.parseInt(specialList.get(realPosition).id));
				startActivity(intent);
			} else {
				VodProgramData program = specialList.get(realPosition);
				openProgramActivity(program);
			}
		} else {
			// 跳转节目页
			VodProgramData program = specialList.get(realPosition);
			openProgramActivity(program);
		}
	}

	private void onRefresh(int first, int count, final boolean refresh) {
		if (!isSub) {
			if (programId == -1) {
				request(first, count, refresh);
			} else {
				requestProgram(first, count, refresh);
			}
		} else if (isSub) {
			requestSub(first, count, refresh);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		onRefresh(0, 10, true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	private void openProgramActivity(VodProgramData program) {
		String programId = String.valueOf(program.id);
		String topicId = program.topicId;
		openProgramDetailActivity(programId, topicId, program.name, 0);
	}

	public void openProgramDetailActivity(String id, String topicId, String updateName, long cpId) {
		Intent intent = new Intent(getActivity(), PlayerActivity.class);
		long programId = Long.valueOf(id);
		long longTopicId = 0;
		try {
			longTopicId = Long.valueOf(topicId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		intent.putExtra("isHalf", true);
		intent.putExtra("id", programId);
		intent.putExtra("topicId", longTopicId);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
		intent.putExtra("where",2);

		getActivity().startActivity(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.columnVideoList);
		VolleyHelper.cancelRequest(Constants.columnProgramSubList);
		VolleyHelper.cancelRequest(Constants.subjectProgramList);
	}

	private boolean loading = false;
	private boolean overloading = false;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (view.getLastVisiblePosition() == view.getCount() - 1 && overloading) {
				ToastHelper.showToast(getActivity(), "已经滑动到底部");
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view.getLastVisiblePosition() > (totalItemCount - 3) && !loading
				&& !overloading && totalItemCount > 0 && !needLoadData) {
			loading = true;
			onRefresh(specialList.size(), 10, false);
		}
	}
}
