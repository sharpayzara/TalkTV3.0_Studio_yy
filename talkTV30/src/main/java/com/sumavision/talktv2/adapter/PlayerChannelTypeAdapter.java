package com.sumavision.talktv2.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 直播左侧列表
 * 
 * @version
 * @description
 */
public class PlayerChannelTypeAdapter extends IBaseAdapter<String> {
	
	private int selectedPos = 0;

	public PlayerChannelTypeAdapter(Context context, List<String> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_changetv_type, null);
		}
		TextView txt = ViewHolder.get(convertView, R.id.name);
		String name = getItem(position);
		if (name.equals(getWeekDayString())) {
			txt.setText("今天");
		} else {
			txt.setText(name);
		}
		if (position == selectedPos) {
			txt.setSelected(true);
		} else {
			txt.setSelected(false);
		}
		return convertView;
	}

	public int getSelectedPos() {
		return selectedPos;
	}

	public void setSelectedPos(int selectedPos) {
		this.selectedPos = selectedPos;
	}

	private String getWeekDayString() {
		String weekString = "";
		final String dayNames[] = {"周日","周一","周二","周三","周四","周五","周六"}; 
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		calendar.setTime(date); 
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		weekString = dayNames[dayOfWeek - 1];
		return weekString;
	}
}
