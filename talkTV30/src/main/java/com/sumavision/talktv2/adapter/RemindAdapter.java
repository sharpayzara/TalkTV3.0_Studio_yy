package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 预约列表适配器
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class RemindAdapter extends IBaseAdapter<VodProgramData> {
	private int userId;

	public RemindAdapter(Context context, int userId,
			List<VodProgramData> objects) {
		super(context, objects);
		this.userId = userId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mybook_list_item, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView tvNameTextView = ViewHolder.get(convertView, R.id.tvName);
		ImageView cancelTextView = ViewHolder.get(convertView, R.id.cancel);
		TextView timeTextView = ViewHolder.get(convertView, R.id.time);
		VodProgramData temp = getItem(position);
		String name = temp.cpName;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = temp.channelName;
		if (intro != null) {
			tvNameTextView.setText(intro);
		}
		String startTime = temp.startTime;
		String endTime = temp.endTime;
		if (startTime != null && endTime != null) {
			timeTextView.setText(startTime + "-" + endTime);
		}
		if (userId == UserNow.current().userID) {
			cancelTextView.setTag(R.id.item_remind_cpId, temp.cpId);
			cancelTextView.setTag(R.id.item_remind_pos, position);
			cancelTextView.setOnClickListener((OnClickListener) context);
		} else {
			cancelTextView.setVisibility(View.GONE);
		}

		loadImage(pic, temp.pic, R.drawable.list_star_default);
		return convertView;
	}

}
