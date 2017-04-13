package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 用户中心 事件适配器
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class UserEventAdapter extends IBaseAdapter<EventData> {

	public UserEventAdapter(Context context, List<EventData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_user_event, null);
		}
		ImageView picImageView = ViewHolder.get(convertView, R.id.pic);
		TextView contentText = ViewHolder.get(convertView, R.id.content);
		TextView timeText = ViewHolder.get(convertView, R.id.time);
		TextView pname = ViewHolder.get(convertView, R.id.pname);
		EventData data = getItem(position);

		if (data.toObjectType > 0) {
			picImageView.setVisibility(View.VISIBLE);
			pname.setVisibility(View.VISIBLE);
			timeText.setVisibility(View.VISIBLE);
			contentText.setText(data.eventTypeName);
			pname.setText(data.toObjectName);
			String time = data.createTime;
			if (time != null) {
				timeText.setText(time);
			}
			String url = data.toObjectPicUrl;
			picImageView.setTag(url);
			loadImage(picImageView, url, R.drawable.list_star_default);
		} else {
			picImageView.setVisibility(View.GONE);
			pname.setVisibility(View.GONE);
			timeText.setVisibility(View.GONE);
			contentText.setText(data.eventTypeName);
		}
		return convertView;
	}
}
