package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.CommentDetailActivity;
import com.sumavision.talktv2.adapter.CommentAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnCommentListListener;
import com.sumavision.talktv2.http.listener.OnUserTalkListListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;

import java.util.ArrayList;

public class CommentFragment extends BaseFragment implements OnCommentListListener, OnRefreshListener2<ListView>, OnItemClickListener, OnUserTalkListListener, OnClickListener {

	public static CommentFragment newInstance(int pid, int userId, int topicId) {
		CommentFragment fragment = new CommentFragment();
		Bundle b = new Bundle();
		b.putInt("resId", R.layout.fragment_comment);
		b.putInt("userId", userId);
		b.putInt("pid", pid);
		b.putInt("topicId", topicId);
		fragment.setArguments(b);
		return fragment;
	}

	int userId, topicId, programId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getInt("userId");
		topicId = getArguments().getInt("topicId");
		programId = getArguments().getInt("pid");
	}

	PullToRefreshListView commentListView;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		commentListView = (PullToRefreshListView) view.findViewById(R.id.listView);
		commentListView.setOnItemClickListener(this);
		commentListView.setOnRefreshListener(this);
		commentListView.setPullToRefreshOverScrollEnabled(false);
		adapter = new CommentAdapter(getActivity(), comments);
		adapter.setFragment(this);
		commentListView.setAdapter(adapter);
		getDefaultComment();

	}

	private ArrayList<CommentData> comments = new ArrayList<CommentData>();
	private CommentAdapter adapter;

	boolean refresh;

	public void getDefaultComment() {
		commentListView.setMode(Mode.BOTH);
		if (adapter.getCount()<=0){
			showLoadingLayout();
		}
		getComment(topicId, userId, 0l, 0, 20);
	}

	private void getComment(long topicId, int userId, long cpId, int first, int count) {
		if (first == 0) {
			refresh = true;
		} else {
			refresh = false;
		}
		if (userId > 0) {
			VolleyCommentRequest.userTalkList(userId, first, count, this, this);
		} else {
			VolleyCommentRequest.getCommentList((int) topicId, (int) cpId, first, count, this, this);
		}

	}

	@Override
	public void reloadData() {

	}

	@Override
	public void getUserTalkList(int errCode, int talkCount, ArrayList<CommentData> talkList, boolean isVip) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
			commentListView.setVisibility(View.VISIBLE);
			if (talkCount == 0) {
				showEmptyLayout("您还没发表过评论");
				commentListView.setMode(Mode.DISABLED);
			} else {
				if (refresh) {
					comments.clear();
				}
				comments.addAll(talkList);
				adapter.notifyDataSetChanged();
				commentListView.onRefreshComplete();
				if (talkCount == comments.size()) {
					commentListView.setMode(Mode.PULL_FROM_START);
				}
			}
		} else {
			if (comments.size() == 0) {
				showErrorLayout();
			} else {
				DialogUtil.alertToast(getActivity(), "加载更多失败!");
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		setCurrentComment((int) id);
		openCommentDetailActivity();
	}
	
	private void setCurrentComment(int pos) {
		CommentData temp = comments.get(pos);
		CommentData.current().talkId = temp.talkId;
		CommentData.current().userName = temp.userName;
		CommentData.current().content = temp.content;
		CommentData.current().source = temp.source;
		CommentData.current().replyCount = temp.replyCount;
		CommentData.current().forwardCount = temp.forwardCount;
		CommentData.current().commentTime = temp.commentTime;
		CommentData.current().rootTalk = temp.rootTalk;
		CommentData.current().hasRootTalk = temp.hasRootTalk;
		CommentData.current().userId = temp.userId;
		CommentData.current().talkType = temp.talkType;
		CommentData.current().contentURL = temp.contentURL;
		CommentData.current().userURL = temp.userURL;
		CommentData.current().audioURL = temp.audioURL;
	}

	private void openCommentDetailActivity() {
		Intent intent = new Intent(getActivity(), CommentDetailActivity.class);
		intent.putExtra("programId", String.valueOf(programId));
		startActivity(intent);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getDefaultComment();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int start = comments.size();
		getComment(topicId, userId, 0l, start, 20);
	}

	@Override
	public void commentList(int errCode, int commentCount, ArrayList<CommentData> commenList) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			if (comments.size() == 0) {
				showErrorLayout();
			} else {
				DialogUtil.alertToast(getActivity(), "加载更多失败!");
			}
			break;
		case JSONMessageType.SERVER_CODE_OK:
			hideLoadingLayout();
			commentListView.setVisibility(View.VISIBLE);
			if (commentCount == 0) {
				showEmptyLayout("还没有人评论，快来抢沙发吧！");
			} else {
				if (refresh) {
					comments.clear();
				}
				comments.addAll(commenList);
				adapter.notifyDataSetChanged();
				commentListView.onRefreshComplete();
				if (commentCount == comments.size()) {
					commentListView.setMode(Mode.PULL_FROM_START);
				}
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.userTalkList);
		VolleyHelper.cancelRequest(Constants.talkList);
	}

	@Override
	public void onClick(View v) {
		setCurrentComment((Integer)v.getTag());
		Intent intent = new Intent(getActivity(), CommentDetailActivity.class);
		intent.putExtra("programId", String.valueOf(programId));
		if (v.getId() == R.id.llayout_forward) {
			intent.putExtra("page", 1);
		} else if (v.getId() == R.id.llayout_reply) {
			intent.putExtra("page", 0);
		}
		startActivity(intent);
	}

}
