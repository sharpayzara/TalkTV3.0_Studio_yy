package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.sumavision.talktv2.http.listener.OnReplyByListListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;
import com.sumavision.talktv2.utils.Constants;

/**
 * 被回复列表
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ReplyFragment extends BaseFragment implements OnItemClickListener,
		OnRefreshListener2<ListView>, OnReplyByListListener {

	public static ReplyFragment newInstance(int userId) {
		ReplyFragment fragment = new ReplyFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_reply);
		bundle.putInt("userId", userId);
		fragment.setArguments(bundle);
		return fragment;
	}

	int userId;
	boolean refresh = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userId = getArguments().getInt("userId");
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		commentListView = (PullToRefreshListView) view
				.findViewById(R.id.listView);
		commentListView.setOnRefreshListener(this);
		commentListView.setOnItemClickListener(this);
		adapter = new CommentAdapter(mActivity, commentList);
		commentListView.setAdapter(adapter);
		commentListView.setVisibility(View.GONE);
		commentListView.setMode(Mode.BOTH);
		commentListView.setPullToRefreshOverScrollEnabled(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getCommentData(0, 10);
	}

	private PullToRefreshListView commentListView;
	private ArrayList<CommentData> commentList = new ArrayList<CommentData>();

	private void getCommentData(int start, int count) {
		VolleyCommentRequest.getReplyByList(userId, start, count, this, this);
	}

	private CommentAdapter adapter;

	private void updateUI(ArrayList<CommentData> replyList) {
		if (replyList != null) {
			if (refresh) {
				commentList.clear();
				commentListView.setMode(Mode.BOTH);
			}
			commentList.addAll(replyList);
			if (commentList.size() == 0) {
				showEmptyLayout("还没有人回复你");
				commentListView.setMode(Mode.DISABLED);
			} else {
				adapter.notifyDataSetChanged();
				commentListView.onRefreshComplete();
				if (replyList.size() < 10) {
					commentListView.setMode(Mode.PULL_FROM_START);
				}
			}
		}
	}

	public static final int REPLY_REQ = 1;
	public static final int FROM_MY_REPLY = 222;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		CommentData.current().talkId = commentList.get((int) id).talkId;
		Intent intent = new Intent(mActivity, CommentDetailActivity.class);
		intent.putExtra("from", FROM_MY_REPLY);
		startActivityForResult(intent, REPLY_REQ);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		refresh = true;
		getCommentData(0, 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		refresh = false;
		getCommentData(commentList.size(), 10);

	}

	@Override
	public void getReplyByList(int errCode, int replyCount,
			ArrayList<CommentData> replyList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			// this.replyCount = replyCount;
			commentListView.setVisibility(View.VISIBLE);
			updateUI(replyList);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void reloadData() {
		getCommentData(0, 10);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.replyByList);
	}

}
