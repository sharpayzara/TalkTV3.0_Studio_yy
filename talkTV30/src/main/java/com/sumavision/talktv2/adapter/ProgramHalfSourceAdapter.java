package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 半屏源列表适配器
 * 
 * 
 */
public class ProgramHalfSourceAdapter extends IBaseAdapter<String> {
	
	private int selectedPos;
	private List<String> objects = new ArrayList<String>();

	public ProgramHalfSourceAdapter(Context context, List<String> objects) {
		super(context, objects);
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.item_pdhp_sourcelist, null);
			viewHolder = new ViewHolder();
			viewHolder.line = (TextView) convertView.findViewById(R.id.pdhp_sourceline);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(objects.get(position))){
			viewHolder.line.setText(objects.get(position));
		}
		if (selectedPos == position) {
			viewHolder.line.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
		} else {
			viewHolder.line.setTextColor(context.getResources().getColor(R.color.gray));
		}
		return convertView;
	}
	
	public int getSelectedPos() {
		return selectedPos;
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
	}

	private class ViewHolder {
		TextView line;
	}

}
