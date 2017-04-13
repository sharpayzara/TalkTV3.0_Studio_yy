package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ReceiverInfo;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 获奖名单适配
 * 
 * @author suma-hpb
 * 
 */
public class ReceiverAdapter extends IBaseAdapter<ReceiverInfo> {

	public ReceiverAdapter(Context context, List<ReceiverInfo> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView nameText;
		TextView phoneText;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_receiver, null);
		}
		nameText = ViewHolder.get(convertView, R.id.name);
		phoneText = ViewHolder.get(convertView, R.id.tv_phone);
		nameText.setText(getItem(position).name);
		String reward = getItem(position).remark;
		phoneText.setText(reward);
		return convertView;
	}
}
