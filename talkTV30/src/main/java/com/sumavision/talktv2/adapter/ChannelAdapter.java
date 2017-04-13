package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.utils.ViewHolder;

public class ChannelAdapter extends IBaseAdapter<ShortChannelData> {

	Fragment fragment;

	public ChannelAdapter(Context context, Fragment fragment,
			List<ShortChannelData> objects) {
		super(context, objects);
		this.fragment = fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_channel, null);
		}
		RelativeLayout goPlay = ViewHolder.get(convertView, R.id.layout_goplay);
		RelativeLayout logoView = ViewHolder.get(convertView, R.id.tvframe);
		TextView tvNameView = ViewHolder.get(convertView, R.id.tvName);
		ImageView channelPic = ViewHolder.get(convertView, R.id.pic);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView timeTxt = ViewHolder.get(convertView, R.id.time);
		TextView channelDetailBtn = ViewHolder.get(convertView, R.id.infoBtn);
		ShortChannelData channel = getItem(position);
		String name = channel.programName;
		if (name != null) {
			nameTxt.setText(name);
		}
		goPlay.setTag(position);
		channelDetailBtn.setTag(position);
		logoView.setTag(position);
		logoView.setOnClickListener((OnClickListener) fragment);
		goPlay.setOnClickListener((OnClickListener) fragment);
		channelDetailBtn.setOnClickListener((OnClickListener) fragment);
		if (channel.haveProgram) {
			channelDetailBtn.setVisibility(View.VISIBLE);
		} else {
			channelDetailBtn.setVisibility(View.GONE);
		}
		SpannableString time = channel.spannableTimeString;
		if (time != null) {
			timeTxt.setText(time);
			timeTxt.setVisibility(View.VISIBLE);
		} else {
			timeTxt.setVisibility(View.GONE);
		}
		String url = channel.channelPicUrl;
		loadImageCacheDisk(channelPic, url, R.drawable.channel_tv_logo_default);
		if (channel.channelName != null)
			tvNameView.setText(channel.channelName);
		return convertView;
	}

}