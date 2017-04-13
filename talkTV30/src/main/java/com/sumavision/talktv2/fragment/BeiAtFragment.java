package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.CommentDetailActivity;
import com.sumavision.talktv2.adapter.CommentAdapter;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.listener.OnTalkAtListener;
import com.sumavision.talktv2.http.request.VolleyCommentRequest;

/**
 * 消息中心被@
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class BeiAtFragment extends BaseFragment implements OnClickListener, OnItemClickListener, OnRefreshListener2<ListView>, OnTalkAtListener {

	public static BeiAtFragment newInstance() {
		BeiAtFragment fragment = new BeiAtFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.msg_content_view);
		fragment.setArguments(bundle);
		return fragment;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				getMyCommentData(0, 10);
				needLoadData = false;
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void initViews(View view) {
		errText = (TextView) view.findViewById(R.id.err_text);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		myCommentListView = (PullToRefreshListView) view.findViewById(R.id.listView);
		errText.setOnClickListener(this);
		myCommentListView.setOnRefreshListener(this);
		myCommentListView.setOnItemClickListener(this);
		myCommentListView.setPullToRefreshOverScrollEnabled(false);
		needLoadData = true;
		adapter = new CommentAdapter(mActivity, list);
		myCommentListView.setAdapter(adapter);
	};

	boolean needLoadData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (needLoadData) {
			handler.sendEmptyMessageDelayed(1, 80);
		}
	}

	private TextView errText;
	private ProgressBar progressBar;
	private PullToRefreshListView myCommentListView;
	private ArrayList<CommentData> list = new ArrayList<CommentData>();

	private void getMyCommentData(int start, int count) {
		if (start == 0) {
			list.clear();
			myCommentListView.setMode(Mode.BOTH);
		}
		VolleyCommentRequest.talkAtList(start, count, this, this);
		if (list.size() == 0) {
			errText.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	private CommentAdapter adapter;

	private void updateUI(ArrayList<CommentData> talkList) {
		if (talkList != null) {
			list.addAll(talkList);
			if (list.size() == 0) {
				errText.setText("还没有人@你");
				errText.setVisibility(View.VISIBLE);
			} else {
				errText.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				myCommentListView.onRefreshComplete();
				if (talkCount == list.size()) {
					myCommentListView.setMode(Mode.PULL_FROM_START);
				}
			}

		} else {
			errText.setVisibility(View.VISIBLE);
		}
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.err_text:
			getMyCommentData(0, 10);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (position - 1 == list.size() || position - 1 < 0)
			return;
		CommentData.current().talkId = list.get(position - 1).talkId;
		Intent intent = new Intent(mActivity, CommentDetailActivity.class);
		intent.putExtra("from", FROM_MY_AT);
		startActivityForResult(intent, AT_REQUEST);
	}

	public static final int AT_REQUEST = 2;
	public static final int FROM_MY_AT = 221;

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getMyCommentData(0, 10);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		int start = 0;
		int count = list.size() + 10;
		getMyCommentData(start, count);

	}

	private int talkCount;

	@Override
	public void getTalkAtList(int errCode, int talkCount, ArrayList<CommentData> talkList) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			this.talkCount = talkCount;
			updateUI(talkList);
		} else {
			progressBar.setVisibility(View.GONE);
			if (list.size() == 0) {
				errText.setVisibility(View.VISIBLE);
			}
		}

	}

	@Override
	public void reloadData() {

	}
}
