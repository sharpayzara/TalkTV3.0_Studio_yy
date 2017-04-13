package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.utils.ViewHolder;

public class ChannelDetailAdapter extends IBaseAdapter<CpData> {

	public ChannelDetailAdapter(Context context, List<CpData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_channel_detail, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView time = ViewHolder.get(convertView, R.id.time);
		ImageView liveBtn = ViewHolder.get(convertView, R.id.item_liveBtn);
		Button reviewBtn = ViewHolder.get(convertView, R.id.item_review);
		CpData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}

		String startTime = temp.startTime;
		String endTime = temp.endTime;
		if (startTime != null && endTime != null) {
			time.setVisibility(View.VISIBLE);
		} else {
			time.setVisibility(View.GONE);
		}
		convertView.setBackgroundResource(R.drawable.channel_program_odd_bg);
		switch (temp.isPlaying) {
		case CpData.TYPE_LIVE:
			liveBtn.setImageResource(R.drawable.channel_live_btn);
			liveBtn.setVisibility(View.VISIBLE);
			SpannableString sp2 = new SpannableString(startTime + "-" + endTime);
			sp2.setSpan(
					new ForegroundColorSpan(Color.argb(0xff, 0xc2, 0x4d, 0x37)),
					0, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			time.setText(sp2);
			liveBtn.setTag(R.id.item_pos, position);
			liveBtn.setTag(R.id.item_bool, temp.isPlaying);
			liveBtn.setTag(R.id.item_back, "");
			liveBtn.setOnClickListener((OnClickListener) context);
			reviewBtn.setVisibility(View.GONE);
			nameTxt.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
			break;
		case CpData.TYPE_REVIEW:
			liveBtn.setVisibility(View.GONE);
			reviewBtn.setTag(R.id.item_back, temp.backUrl);
			reviewBtn.setTag(R.id.item_pos, position);
			reviewBtn.setTag(R.id.item_bool, temp.isPlaying);
			nameTxt.setTextColor(Color.argb(0xff, 0x99, 0x99, 0x99));
			SpannableString sp1 = new SpannableString(startTime + "-" + endTime);
			sp1.setSpan(
					new ForegroundColorSpan(Color.argb(0xff, 0x99, 0x99, 0x99)),
					0, sp1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			time.setText(sp1);
			if (!TextUtils.isEmpty(temp.backUrl)) {
				reviewBtn.setVisibility(View.VISIBLE);
				reviewBtn.setOnClickListener((OnClickListener) context);
			} else {
				reviewBtn.setVisibility(View.GONE);
			}
			break;
		case CpData.TYPE_UNPLAY:
			SpannableString sp = new SpannableString(startTime + "-" + endTime);
			nameTxt.setTextColor(context.getResources().getColor(R.color.black));
			sp.setSpan(
					new ForegroundColorSpan(Color.argb(0xff, 0xc2, 0x4d, 0x37)),
					0, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			time.setText(sp);
			liveBtn.setVisibility(View.VISIBLE);
			if (temp.order == 1) {
				liveBtn.setImageResource(R.drawable.channel_program_faved);
			} else {
				liveBtn.setImageResource(R.drawable.channel_program_fav);
			}
			liveBtn.setTag(R.id.item_pos, position);
			liveBtn.setTag(R.id.item_bool, temp.isPlaying);
			liveBtn.setTag(R.id.item_back, "");
			liveBtn.setOnClickListener((OnClickListener) context);
			reviewBtn.setVisibility(View.GONE);
			break;
		}
		return convertView;
	}

}