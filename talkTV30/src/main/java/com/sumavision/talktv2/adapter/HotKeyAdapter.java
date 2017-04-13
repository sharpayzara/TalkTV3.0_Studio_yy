package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.KeyWordData;
import com.sumavision.talktv2.utils.StringUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 热门搜索词适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class HotKeyAdapter extends IBaseAdapter<KeyWordData> {
	public HotKeyAdapter(Context context, List<KeyWordData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.playhistory_hot_list_item,
					null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		KeyWordData temp = getItem(position);
		String name = temp.name;
		if (StringUtils.isNotEmpty(name)) {
			nameTxt.setText(name);
		}
		return convertView;
	}

}
