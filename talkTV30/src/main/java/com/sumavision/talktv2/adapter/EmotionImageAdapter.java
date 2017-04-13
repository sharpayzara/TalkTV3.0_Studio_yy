package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.EmotionData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * @author 郭鹏
 * @version 2.0
 * @description 表情适配
 * @createTime 2012-6-8
 * @changeLog
 */
public class EmotionImageAdapter extends IBaseAdapter<EmotionData> {

	public EmotionImageAdapter(Context context, List<EmotionData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_emotion, null);
		}
		ImageView img = ViewHolder.get(convertView, R.id.emotion_grid_img);
		img.setImageResource(getItem(position).getId());
		return convertView;
	}

}
