package com.sumavision.talktv2.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.GuessCommentAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnCommentListListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 互动详情页-评论
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveCommentFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnCommentListListener {

	private int topicId;

	public static InteractiveCommentFragment newInstance(int topicId) {
		InteractiveCommentFragment fragment = new InteractiveCommentFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("topicId", topicId);
		bundle.putInt("resId", R.layout.fragment_interactive_comment);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InteractiveCommentFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InteractiveCommentFragment");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		topicId = getArguments().getInt("topicId", 0);
	}

	private RelativeLayout noDataLayout;
	private ImageView noDataImageTip;
	private TextView noDataTextTip;
	private TextView errTxt;
	private ProgressBar mprogressBar;
	private RelativeLayout errLayout;
	private PullToRefreshListView listView;
	private ArrayList<CommentData> mCommentList = new ArrayList<CommentData>();
	private GuessCommentAdapter commentAdapter;

	@Override
	protected void initViews(View view) {
		listView = (PullToRefreshListView) rootView.findViewById(R.id.list);listView.setPullToRefreshOverScrollEnabled(false);
		noDataLayout = (RelativeLayout) rootView.findViewById(R.id.no_list_tip);
		noDataImageTip = (ImageView) rootView.findViewById(R.id.no_list_pic);
		noDataTextTip = (TextView) rootView.findViewById(R.id.no_list_text);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		mprogressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		commentAdapter = new GuessCommentAdapter(getActivity(), mCommentList);
		listView.setAdapter(commentAdapter);
		listView.setOnRefreshListener(this);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		String label = getString(R.string.mylistview_update) + format.format(new Date());
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

	}

	private boolean firstLoad = true;

	public void loadData() {
		if (firstLoad) {
			errLayout.setVisibility(View.VISIBLE);
			getCommentList(0, 10);
		}
	}

	public void showLoadingLayout() {
		errLayout.setVisibility(View.VISIBLE);
	}

	public void refresh() {
		listView.setRefreshing();
		getCommentList(0, 10);
	}

	private boolean refresh;

	private void getCommentList(int start, int count) {
		if (start == 0) {
			refresh = true;
			listView.setMode(Mode.BOTH);
		} else {
			refresh = false;
		}
		VolleyCommentRequest.getCommentList(topicId, 0, start, count, this, this);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCommentList(0, 10);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCommentList(mCommentList.size(), 10);

	}

	@Override
	public void commentList(int errCode, int commentCount, ArrayList<CommentData> commentList) {
		firstLoad = false;
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			errLayout.setVisibility(View.VISIBLE);
			errTxt.setVisibility(View.VISIBLE);
			mprogressBar.setVisibility(View.GONE);
			break;
		case JSONMessageType.SERVER_CODE_OK:
			errLayout.setVisibility(View.GONE);
			if (refresh) {
				mCommentList.clear();
			}
			mCommentList.addAll(commentList);
			if (mCommentList.size() == 0) {
				listView.setVisibility(View.GONE);
				noDataLayout.setVisibility(View.VISIBLE);
				noDataImageTip.setImageResource(R.drawable.interactive_no_comment);
				noDataTextTip.setText(R.string.interactive_no_comment_tip);
			} else {
				noDataLayout.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				commentAdapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				if (mCommentList.size() == commentCount) {
					listView.setMode(Mode.PULL_FROM_START);
				}
			}
			break;
		default:
			break;
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
		VolleyHelper.cancelRequest(Constants.talkList);
	}

}
