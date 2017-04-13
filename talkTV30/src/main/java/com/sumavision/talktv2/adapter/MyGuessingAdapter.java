package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.interactive.InteractiveGuess;

public class MyGuessingAdapter extends ArrayAdapter<InteractiveGuess> {

	public MyGuessingAdapter(Context context, List<InteractiveGuess> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_my_guessing, null);
		}
		RelativeLayout divider = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.img_flag);
		TextView type = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.guessing_type);
		TextView name = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.guessing_title);
		TextView endTime = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.guessing_endTime);
		ImageView status = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.guessing_status);

		InteractiveGuess data = getItem(position);
		if (data.type == InteractiveGuess.TYPE_INTIME) {
			type.setText(R.string.interact_guess);
			int color = getContext().getResources().getColor(
					R.color.interactive_intime_divider);
			type.setTextColor(color);
			divider.setBackgroundColor(color);
		} else {
			if (data.prizeType == InteractiveGuess.PRIZE_TYPE_DIAMOND) {
				type.setText(R.string.diamond_guess);
				int color = getContext().getResources().getColor(
						R.color.guess_diamond);
				type.setTextColor(color);
				divider.setBackgroundColor(color);
			} else {
				int color = getContext().getResources().getColor(
						R.color.guess_point);
				type.setTextColor(color);
				divider.setBackgroundColor(color);
				type.setText(R.string.point_guess);
			}
		}

		name.setText(data.title);
		String day = data.endTime.substring(0, 10);
		endTime.setText(getContext().getResources().getString(
				R.string.guess_endtime, day));
		if (data.status) {
			if (data.userJoin) {
				status.setImageResource(R.drawable.guess_joined);
			}
		} else {
			if (data.userWin) {
				status.setImageResource(R.drawable.guess_win);
			} else {
				status.setImageResource(R.drawable.guess_lose);
			}
		}

		return convertView;
	}
}