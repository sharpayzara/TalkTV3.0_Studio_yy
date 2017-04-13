package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.SourceAdapter;
import com.sumavision.talktv2.bean.SimpleSourcePlatform;

public class SourceDialogFragment extends BaseDialogFragment implements
		OnItemClickListener {
	ArrayList<SimpleSourcePlatform> plats;

	public static SourceDialogFragment newInstance(
			ArrayList<SimpleSourcePlatform> plats) {
		SourceDialogFragment frag = new SourceDialogFragment();
		Bundle b = new Bundle();
		b.putInt("resId", R.layout.dialog_source);
		b.putSerializable("list", plats);
		frag.setArguments(b);
		return frag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.MyDialog);
		plats = (ArrayList<SimpleSourcePlatform>) getArguments()
				.getSerializable("list");
	}

	public interface OnSourceItemClickListener {
		public void OnSourceItemClick(int position);
	}

	OnSourceItemClickListener listener;

	public void setListener(OnSourceItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	protected void initViews(View view) {
		ListView listView = (ListView) view.findViewById(R.id.list);
		listView.setAdapter(new SourceAdapter(getActivity(), plats));
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (listener != null) {
			listener.OnSourceItemClick(position);
		}
		dismiss();

	}

}
