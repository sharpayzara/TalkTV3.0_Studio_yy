package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 节目详情页 频道适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ProgramDetailChannelAdapter extends IBaseAdapter<ChannelData> {

	public ProgramDetailChannelAdapter(Context context,
			List<ChannelData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_program_channel, null);
		}
		TextView textView = ViewHolder.get(convertView, R.id.name);
		TextView timeView = ViewHolder.get(convertView, R.id.time);
		TextView btnView = ViewHolder.get(convertView, R.id.play);
		ChannelData channelData = getItem(position);
		String name = channelData.channelName;
		if (name != null) {
			textView.setText(name);
		}
		String startTime = channelData.now.startTime;
		String endTime = channelData.now.endTime;
		if (startTime != null && endTime != null) {
			timeView.setText(startTime + "-" + endTime);
		}
		if (channelData.now.isPlaying == 1) {
			btnView.setText("立即播放");

		} else {
			if (channelData.now.order == 0) {
				btnView.setText("预约提醒");
			} else {
				btnView.setText("取消预约");
			}
		}
		return convertView;
	}
}
