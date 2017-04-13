package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;

public class ReportAdapter extends IBaseAdapter<String> {

	public ReportAdapter(Context context, String[] objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_error, null);
		}
		TextView errTxt = (TextView) convertView.findViewById(R.id.tv_report);
		errTxt.setText(getItem(position));
		return convertView;
	}

}
