package com.sumavision.talktv2.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;

public class ProgramHalfRecommendAdapter extends IBaseAdapter<VodProgramData> {

	public ProgramHalfRecommendAdapter(Context context, ArrayList<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_programhalf_recommend, null);
		}
		ImageView pic = (ImageView) convertView.findViewById(R.id.pic);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		VodProgramData data = getItem(position);
		loadImage(pic, data.pic, R.drawable.item_recommend_player);
		name.setText(data.name);
		return convertView;
	}
}
