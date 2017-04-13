package com.sumavision.talktv2.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.activity.WebBrowserActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.interactive.InteractiveCyclopedia;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.interactive.OnInteractiveCyclopediaListener;
import com.sumavision.talktv2.http.request.VolleyInteractionRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.KeywordsFlow;
import com.umeng.analytics.MobclickAgent;

/**
 * 竞猜百科
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessingCyclopediaFragment extends BaseFragment implements OnClickListener, OnInteractiveCyclopediaListener {
	private EditText keyEdit;
	private Button searhBtn;
	private KeywordsFlow hotkeyLayout;
	ArrayList<InteractiveCyclopedia> cyclopediaList = new ArrayList<InteractiveCyclopedia>();
	HashMap<String, String> showList = new HashMap<String, String>();
	private RelativeLayout errLayout;
	private TextView errTxt;
	private ProgressBar mProgressBar;
	private LinearLayout searchLayout;

	public static GuessingCyclopediaFragment newInstance() {
		GuessingCyclopediaFragment fragment = new GuessingCyclopediaFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_guessing_cyclopedia);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("GuessingCyclopediaFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("GuessingCyclopediaFragment");
	}
	@Override
	protected void initViews(View view) {
		keyEdit = (EditText) rootView.findViewById(R.id.keyword);
		searhBtn = (Button) rootView.findViewById(R.id.search);
		searhBtn.setOnClickListener(this);
		hotkeyLayout = (KeywordsFlow) rootView.findViewById(R.id.hotkeyLayout);
		errLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		searchLayout = (LinearLayout) rootView.findViewById(R.id.search_layout);
		errTxt = (TextView) rootView.findViewById(R.id.err_text);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		hotkeyLayout.setOnItemClickListener(this);
	}

	private boolean needLoadData = true;

	public void getData(int start, int count) {
		if (needLoadData) {
			showLoading();
			VolleyInteractionRequest.cyclopediaList(this, start, count, this);
		}
	}

	private void showLoading() {
		errLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		errTxt.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getActivity(), WebBrowserActivity.class);
		intent.putExtra("title", R.string.guessing_baike);
		if (v.getId() == R.id.search) {
			String input = keyEdit.getText().toString().trim();
			if (keyEdit.getText().toString().isEmpty()) {
				Toast.makeText(getActivity(), getString(R.string.input_tip), Toast.LENGTH_SHORT).show();
				return;
			}
			StringBuilder url = new StringBuilder("http://m.baidu.com/s?word=");
			url.append(input);
			intent.putExtra("url", url.toString());
			startActivity(intent);
		} else if (v instanceof TextView) {
			String keyword = ((TextView) v).getText().toString();
			intent.putExtra("url", showList.get(keyword));
			startActivity(intent);
		}

	}

	@Override
	public void cyclopediaKeywords(int errCode, ArrayList<InteractiveCyclopedia> keyWordsList) {
		errLayout.setVisibility(View.GONE);
		searchLayout.setVisibility(View.VISIBLE);
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			needLoadData = false;
			ArrayList<InteractiveCyclopedia> list = null;
			if (cyclopediaList.size() > KeywordsFlow.MAX) {
				list = (ArrayList<InteractiveCyclopedia>) keyWordsList.subList(0, KeywordsFlow.MAX);
			} else {
				list = keyWordsList;
			}
			for (InteractiveCyclopedia pedia : list) {
				showList.put(pedia.keyword, pedia.webUrl);
				hotkeyLayout.feedKeyword(pedia.keyword);
			}
			hotkeyLayout.go2Show(KeywordsFlow.ANIMATION_IN);
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
		VolleyHelper.cancelRequest(Constants.interactiveBaikeList);
	}

}
