package com.sumavision.talktv2.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.ProgramHalfRecommendAdapter;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ProgramHalfRecommendParser;
import com.sumavision.talktv2.http.json.ProgramHalfRecommendRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

import java.util.ArrayList;

public class ProgramRecommendFragment extends Fragment {
	
	private GridView episodeGrid;
	private ProgramHalfRecommendAdapter adapter;
	private TextView back,title;
	
	private ProgramData programData;
	
	private ArrayList<VodProgramData> recommendData = new ArrayList<VodProgramData>();
	View loading;
	public static ProgramRecommendFragment newInstance() {
		ProgramRecommendFragment fragment = new ProgramRecommendFragment();
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getExtras();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_program_recommend, null);
		episodeGrid = (GridView) view.findViewById(R.id.episode_grid);
		back = (TextView) view.findViewById(R.id.episode_fragment_back);
		title = (TextView) view.findViewById(R.id.episode_fragment_name);
		title.setText("推荐");
		loading = view.findViewById(R.id.loading);
		
		updateView();
		loading.setVisibility(View.VISIBLE);
		getRecommendData();
		initEvents();
		return view;
	}

	String version;
	private void getExtras() {
		programData = ((ProgramDetailHalfActivity) getParentFragment()).programData;
	}
	
	private void updateView() {
		adapter = new ProgramHalfRecommendAdapter(getActivity(), recommendData);
		episodeGrid.setVisibility(View.VISIBLE);
		episodeGrid.setAdapter(adapter);
	}
	
	private void getRecommendData() {
		final ProgramHalfRecommendParser rparser = new ProgramHalfRecommendParser();
		VolleyHelper.post(new ProgramHalfRecommendRequest((int)programData.programId).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				loading.setVisibility(View.GONE);
				version = rparser.recVersion;
				recommendData.clear();
				recommendData.addAll(rparser.recommendData);
				updateRecommendView();
			}
		}, null);
	}
	
	private void updateRecommendView() {
		adapter.notifyDataSetChanged();
	}
	
	private void initEvents() {
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		episodeGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VodProgramData data = recommendData.get(position);
				int programId = Integer.parseInt(data.id);
				if (programId == 0 || programId == (int)programData.programId) {
					return;
				}
				((ProgramDetailHalfActivity)getParentFragment()).loadingLayout.setVisibility(View.VISIBLE);
				int tempId = 0;
				if (programData.pType == 1 || programData.pType == 11){
					tempId = PreferencesUtils.getInt(getActivity(), Constants.SP_PLAY_RECORD, "" + programId, 0);
				}
				ProgramDetailHalfActivity fragment = (ProgramDetailHalfActivity)getParentFragment();
				fragment.platformChanged = false;
				fragment.clickPlatformId = -1;
				fragment.setProgramId(programId);
				fragment.switchVideo(programId, tempId,version);
				getFragmentManager().popBackStack();
			}
		});
	}
}
