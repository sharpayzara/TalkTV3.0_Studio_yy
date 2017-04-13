package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 热门播放历史适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HotHistoryGridAdapter extends IBaseAdapter<VodProgramData> {

	public HotHistoryGridAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.playhistory_hot_list_item,
					null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		VodProgramData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		return convertView;
	}

}
