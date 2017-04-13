package com.sumavision.talktv2.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.sumavision.talktv2.adapter.MyGuessingAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveGuessListListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 互动详情页-竞猜列表
 * 
 * @author suma-hpb
 * 
 */
public class InteractiveGuessFragment extends BaseFragment implements OnRefreshListener2<ListView>, OnItemClickListener, OnClickListener, OnInteractiveGuessListListener {

	private int activityId;

	public static InteractiveGuessFragment newInstance(int activityId) {
		InteractiveGuessFragment fragment = new InteractiveGuessFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("activityId", activityId);
		bundle.putInt("resId", R.layout.fragment_interactive_guess);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("InteractiveGuessFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("InteractiveGuessFragment");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityId = getArguments().getInt("activityId", 0);
	}

	private RelativeLayout noDataLayout;
	private ImageView noDataImageTip;
	private TextView noDataTextTip;
	private RelativeLayout errLayout;
	private TextView errTxt;
	private ProgressBar mprogressBar;
	private PullToRefreshListView listView;
	private ArrayList<InteractiveGuess> mGuessList = new ArrayList<InteractiveGuess>();
	private MyGuessingAdapter guessAdapter;
	private boolean firstLoad = true;

	@Override
	protected void initViews(View view) {
		listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
		listView.setPullToRefreshOverScrollEnabled(false);
		noDataLayout = (RelativeLayout) rootView.findViewById(R.id.no_list_tip);
		noDataImageTip = (ImageView) rootView.findViewById(R.id.no_list_pic);
		noDataTextTip = (TextView) rootView.findViewById(R.id.no_list_text);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		errTxt.setOnClickListener(this);
		mprogressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		guessAdapter = new MyGuessingAdapter(getActivity(), mGuessList);
		listView.setAdapter(guessAdapter);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		String label = getString(R.string.mylistview_update) + format.format(new Date());
		listView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(this);
		loadData();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private void loadData() {
		if (firstLoad) {
			getGuessList(0, 10);
		}
	}

	public void refresh() {
		// listView.setRefreshing();
		getGuessList(0, 10);
	}

	private boolean refresh = false;

	private void getGuessList(int start, int count) {
		if (start == 0) {
			refresh = true;
			listView.setMode(Mode.BOTH);
		} else {
			refresh = false;
		}
		VolleyInteractionRequest.guessingList(this, this, start, count, activityId);
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getGuessList(0, 10);

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getGuessList(mGuessList.size(), 10);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		InteractiveGuess guess = (InteractiveGuess) parent.getItemAtPosition(position);
		showDialog(guess);
	}

	GuessDialogFragment guessDialog;

	private void showDialog(InteractiveGuess guess) {
		guessDialog = GuessDialogFragment.newInstance(guess.id, guess.userJoin);
		guessDialog.show(getActivity().getSupportFragmentManager(), "guess");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.err_text) {
			loadData();
		}

	}

	@Override
	public void onGetGuessingList(int errCode, ArrayList<InteractiveGuess> guessingList) {
		errLayout.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			firstLoad = false;
			if (refresh) {
				mGuessList.clear();
			}
			mGuessList.addAll(guessingList);
			if (mGuessList.size() == 0) {
				listView.setVisibility(View.GONE);
				noDataLayout.setVisibility(View.VISIBLE);
				noDataImageTip.setImageResource(R.drawable.interactive_no_guess);
				noDataTextTip.setText(R.string.interactive_no_cguess_tip);
			} else {
				noDataLayout.setVisibility(View.GONE);
				guessAdapter.notifyDataSetChanged();
				listView.onRefreshComplete();
				if (guessingList.size() < 10) {
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
		VolleyHelper.cancelRequest(Constants.interactiveGuessList);
	}
}
