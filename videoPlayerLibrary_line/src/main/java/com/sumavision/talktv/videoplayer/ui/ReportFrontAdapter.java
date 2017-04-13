package com.sumavision.talktv.videoplayer.ui;

import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv2.utils.PreferencesUtils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportFrontAdapter extends ArrayAdapter<String> {

	private boolean canReport = true;
	private Context context;

	public ReportFrontAdapter(Context context, String[] objects) {
		super(context, 0, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_error_before, null);
		}
		TextView errTxt = (TextView) convertView.findViewById(R.id.tv_report);
		ImageView img = (ImageView) convertView.findViewById(R.id.err_icon);
		ImageView check = (ImageView) convertView.findViewById(R.id.imgv_checkd);
		errTxt.setText(getItem(position));
		if (position == 0) {
			if (!canReport) {
				errTxt.setTextColor(Color.GRAY);
				errTxt.setEnabled(false);
			} else {
				errTxt.setTextColor(Color.WHITE);
				errTxt.setEnabled(true);
			}
			img.setImageResource(R.drawable.icon_err);
			check.setVisibility(View.GONE);
			errTxt.setText("报错");
		} else if (position == 1) {
			img.setImageResource(R.drawable.icon_enlarge);
			check.setVisibility(View.GONE);
			errTxt.setText("画面伸缩");
		} else if (position == 2) {
			img.setImageResource(R.drawable.icon_dlna);
			check.setVisibility(View.GONE);
			errTxt.setText("DLNA");
		} else if (position == 3) {
			img.setImageResource(R.drawable.feedback_unselect);
			if (PreferencesUtils.getBoolean(context,
					null, "half_auto", true)) {
				check.setVisibility(View.VISIBLE);
			} else {
				check.setVisibility(View.GONE);
			}
			errTxt.setText(context.getResources().getString(R.string.auto_play));
		} else if (position == 4) {
			img.setImageResource(R.drawable.feedback_unselect);
			if (PreferencesUtils.getBoolean(context,
					PlayerActivity.SP_AUTO_PLAY, "auto", true)) {
				check.setVisibility(View.VISIBLE);
			} else {
				check.setVisibility(View.GONE);
			}
			errTxt.setText(context.getResources().getString(
					R.string.sequence_play));
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
