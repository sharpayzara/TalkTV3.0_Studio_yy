package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 频道分类列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class PlayerChannelAdapter extends IBaseAdapter<ShortChannelData> {
	
	private int selectedPos = 0;

	public PlayerChannelAdapter(Context context, List<ShortChannelData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_changetv_program, null);
		}
		TextView txt = ViewHolder.get(convertView, R.id.channel_name);
		TextView txtprogram = ViewHolder.get(convertView, R.id.channel_program);
		ShortChannelData data = getItem(position);
		String name = data.channelName;
		String program = data.programName;
		txt.setText(name);
		txtprogram.setText(program);
		if (position == selectedPos) {
			txt.setSelected(true);
			txtprogram.setSelected(true);
		} else {
			txt.setSelected(false);
			txtprogram.setSelected(false);
		}
		return convertView;
	}

	public int getSelectedPos() {
		return selectedPos;
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
	}

}
