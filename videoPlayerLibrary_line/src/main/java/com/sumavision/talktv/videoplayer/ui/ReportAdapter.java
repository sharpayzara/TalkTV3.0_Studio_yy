package com.sumavision.talktv.videoplayer.ui;

import com.sumavision.talktv.videoplayer.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ReportAdapter extends ArrayAdapter<String> {
	
	private boolean canReport = true;

	public ReportAdapter(Context context, String[] objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_error, null);
		}
		TextView errTxt = (TextView) convertView.findViewById(R.id.tv_report);
		errTxt.setText(getItem(position));
		if (position == 0) {
			if(!canReport) {
				errTxt.setTextColor(Color.GRAY);
				errTxt.setEnabled(false);
			} else {
				errTxt.setTextColor(Color.WHITE);
				errTxt.setEnabled(true);
			}
		}
		return convertView;
	}
	
	public void setCanReport(boolean canReport) {
		this.canReport = canReport;
	}

	public boolean isCanReport() {
		return canReport;
	}
}
