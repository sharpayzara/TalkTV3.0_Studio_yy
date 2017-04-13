package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.UserCenterActivity;
import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 所有人事件列表适配----原所有人页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class EventAdapter extends IBaseAdapter<EventData> {

	public EventAdapter(Context context, List<EventData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_friend_all, null);
		}
		ImageView picImageView = ViewHolder.get(convertView, R.id.pic);
		TextView contentText = ViewHolder.get(convertView, R.id.content);
		TextView userNameText = ViewHolder.get(convertView, R.id.name);
		TextView timeText = ViewHolder.get(convertView, R.id.time);
		TextView pname = ViewHolder.get(convertView, R.id.pname);
		final EventData data = getItem(position);
		userNameText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		userNameText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserNow.current().userID == 0) {
					Intent intent = new Intent(context, LoginActivity.class);
					context.startActivity(intent);
				} else {
					int uId = data.userId;
					Intent intent = new Intent(context,
							UserCenterActivity.class);
					intent.putExtra("id", uId);
					context.startActivity(intent);
				}
			}
		});
		if (data.toObjectType > 0) {
			pname.setVisibility(View.VISIBLE);
			picImageView.setVisibility(View.VISIBLE);
			timeText.setVisibility(View.VISIBLE);
			pname.setText(data.toObjectName);
			userNameText.setText(data.userName);
			contentText.setText(data.eventTypeName);
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
			userNameText.setText(data.userName);
			contentText.setText(data.eventTypeName);
		}

		return convertView;
	}
}
