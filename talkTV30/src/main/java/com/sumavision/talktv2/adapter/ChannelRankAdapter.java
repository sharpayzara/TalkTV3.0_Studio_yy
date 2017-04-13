package com.sumavision.talktv2.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 适配器:频道收视率
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelRankAdapter extends IBaseAdapter<ShortChannelData> {

	private Fragment fragment;

	public ChannelRankAdapter(Fragment fragment, List<ShortChannelData> objects) {
		super(fragment.getActivity(), objects);
		this.fragment = fragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.item_channel_rank, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		ImageView channelPic = ViewHolder.get(convertView, R.id.pic);
		TextView time = ViewHolder.get(convertView, R.id.time);
		TextView rankInfoView = ViewHolder.get(convertView, R.id.rankingInfo);
		TextView tvNameView = ViewHolder.get(convertView, R.id.tvName);
		TextView viewCount = ViewHolder.get(convertView, R.id.viewercount);
		ShortChannelData temp = getItem(position);
		String name = temp.programName;
		if (name != null) {
			nameTxt.setText(name);
		}
		SpannableString timeInfo = temp.spannableTimeInfo;
		if (timeInfo != null && timeInfo.length() > 4) {
			time.setText(timeInfo);
			time.setVisibility(View.VISIBLE);
		} else {
			time.setVisibility(View.GONE);
		}
		int viewerCount = temp.viewCount;
		viewCount.setText(CommonUtils.processPlayCount(viewerCount));
		String url = temp.channelPicUrl;
		loadImage(channelPic, url, R.drawable.channel_tv_logo_default);
		channelPic.setTag(position);
		channelPic.setOnClickListener((OnClickListener) fragment);
		String channelName = temp.channelName;
		if (channelName != null) {
			tvNameView.setText(channelName);
		}
		String rankingInfo = temp.programInfo;
		if (rankingInfo != null) {
			rankInfoView.setText("(" + rankingInfo + ")");
		}
		return convertView;
	}
}
