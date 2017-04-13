package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.ViewHolder;

public class FeedbackAdapter extends IBaseAdapter<String> {

	public FeedbackAdapter(Context context, String[] objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.item_feedback_problem, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.tv_name);
		nameTxt.setText(getItem(position));
		return convertView;
	}
}
