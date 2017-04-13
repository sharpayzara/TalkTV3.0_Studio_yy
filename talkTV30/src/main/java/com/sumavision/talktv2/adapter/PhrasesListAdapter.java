package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * @author 郭鹏
 * @version 2.0
 * @description 常用短语适配
 * @createTime 2012-6-8
 * @changeLog
 */
public class PhrasesListAdapter extends IBaseAdapter<String> {

	public PhrasesListAdapter(Context context, List<String> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_phrase, null);
		}
		TextView t = ViewHolder.get(convertView, R.id.phrase_list_item_txt);
		t.setText(getItem(position));
		return convertView;
	}

}
