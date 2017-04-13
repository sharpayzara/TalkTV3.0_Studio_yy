package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.MyGuessingAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;
import com.sumavision.talktv2.fragment.GuessDialogFragment;
import com.sumavision.talktv2.fragment.InteractionFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnUserGuessListListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的竞猜
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyGuessingActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnRefreshListener2<ListView>, OnUserGuessListListener {

	private RelativeLayout errLayout;
	private ProgressBar loadProgress;
	private TextView errTxt;

	private PullToRefreshListView listView;
	private RelativeLayout noGuessLayout;
	private TextView txtTip;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_my_guess);
		getSupportActionBar().setTitle(R.string.my_guessing);
		initeView();
		getGuessingList(0, 20);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private MyGuessingAdapter adapter;
	private ArrayList<InteractiveGuess> guessList = new ArrayList<InteractiveGuess>();

	private void initeView() {
		listView = (PullToRefreshListView) findViewById(R.id.guessing_list);
		listView.setMode(Mode.DISABLED);
		listView.setPullToRefreshOverScrollEnabled(false);
		txtTip = (TextView) findViewById(R.id.tip);
		adapter = new MyGuessingAdapter(this, guessList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(this);
		noGuessLayout = (RelativeLayout) findViewById(R.id.no_guessing);
		errLayout = (RelativeLayout) findViewById(R.id.errLayout);
		loadProgress = (ProgressBar) findViewById(R.id.progressBar);
		errTxt = (TextView) findViewById(R.id.err_text);
		errTxt.setOnClickListener(this);
	}

	@Override
	public void onResume() {
		MobclickAgent.onPageStart("MyGuessingActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("MyGuessingActivity");
		super.onPause();
	}

	private void getGuessingList(int start, int count) {
		showLoading();
		if (start == 0) {
			listView.setMode(Mode.PULL_FROM_END);
		}
		VolleyInteractionRequest.userGuessList(this, start, count, this);
	}

	private void showLoading() {
		errLayout.setVisibility(View.VISIBLE);
		loadProgress.setVisibility(View.VISIBLE);
		errTxt.setVisibility(View.GONE);
	}

	private void showErrLayout() {
		errLayout.setVisibility(View.VISIBLE);
		loadProgress.setVisibility(View.GONE);
		errTxt.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.err_text:
			getGuessingList(0, 20);
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		InteractiveGuess guess = (InteractiveGuess) parent.getItemAtPosition(position);
		if (guess.type == InteractiveGuess.TYPE_NORMAL) {
			GuessDialogFragment guessDialog = GuessDialogFragment.newInstance(guess.id, guess.userJoin);
			guessDialog.show(getSupportFragmentManager(), "guess");
		} else {
			boolean isPortrait = this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			InteractionFragment mInteractionFragment = InteractionFragment.newInstance(isPortrait, 0, true, guess, false);
			mInteractionFragment.show(getSupportFragmentManager(), "interaction");
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getGuessingList(guessList.size(), 20);

	}

	@Override
	public void userGuessList(int errCode, ArrayList<InteractiveGuess> useGuessList) {
		errLayout.setVisibility(View.GONE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			guessList.addAll(useGuessList);
			adapter.notifyDataSetChanged();
			listView.onRefreshComplete();
			if (guessList.size() == 0) {
				listView.setVisibility(View.GONE);
				txtTip.setVisibility(View.GONE);
				noGuessLayout.setVisibility(View.VISIBLE);
			}
			if (useGuessList.size() < 20) {
				listView.setMode(Mode.DISABLED);
			}
		} else {
			showErrLayout();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.userInteractiveGuessList);
	}
}
