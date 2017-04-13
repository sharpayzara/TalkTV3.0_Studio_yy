package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.SimpleSourcePlatform;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 播放源适配
 * 
 * @author suma-hpb
 * 
 */
public class SourceAdapter extends IBaseAdapter<SimpleSourcePlatform> {

	public SourceAdapter(Context context, List<SimpleSourcePlatform> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.soucre_spinner_dropdown_item, null);
		}
		ImageView logo = ViewHolder.get(convertView, R.id.imgv__dropdown_source);
		loadImage(logo, getItem(position).pic, R.drawable.play_source_default);
		TextView nameView = ViewHolder.get(convertView, R.id.tv_name);
		nameView.setText("来自 " + getItem(position).name);
		return convertView;
	}

}
