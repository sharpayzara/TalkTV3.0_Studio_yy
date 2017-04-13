package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 子节目列表展示适配 //FIXME
 * 
 * @author suma-hpb
 * 
 */
public class SubProgramAdapter extends IBaseAdapter<VodProgramData> {

	public SubProgramAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lib_sub, null);
		}
		ImageView picView = ViewHolder.get(convertView, R.id.imagv_sub_pic);
		TextView timeView = ViewHolder.get(convertView, R.id.tv_sub_time);
		TextView titleView = ViewHolder.get(convertView, R.id.tv_sub_title);
		VodProgramData program = getItem(position);
		loadImage(picView, program.pic, R.drawable.aadefault);
		
		String time = formatPlayLength(program.playLength);
		if (!time.equals("00:00:00")) {
			timeView.setVisibility(View.VISIBLE);
			timeView.setText(time);
		} else {
			timeView.setVisibility(View.GONE);
		}
		titleView.setText(program.name);
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.fav_item_even_bg);
		} else {
			convertView.setBackgroundResource(R.drawable.fav_item_odd_bg);
		}
		return convertView;
	}
	
	private String formatPlayLength(int second) {
		int hour = second / 3600;
		int min = (second - hour * 3600) / 60;
		int sec = second - hour * 3600 - min * 60;
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}

}
