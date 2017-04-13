package com.sumavision.talktv2.fragment;

import android.os.Bundle;
import android.view.View;

import com.sumavision.talktv2.R;

public class NewProgramDetailFragment extends BaseFragment {

	public static NewProgramDetailFragment newInstance() {
		NewProgramDetailFragment fragment = new NewProgramDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.program_detail_half_programdetail);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void initViews(View view) {
		
	}

	@Override
	public void reloadData() {
	}

}
