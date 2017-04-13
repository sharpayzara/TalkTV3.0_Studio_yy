package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.fragment.VideoControllerFragment;
import com.sumavision.talktv2.utils.ViewHolder;

public class ProgramListAdapter extends IBaseAdapter<CpData> {
	
	private int selectedPosition;
	private VideoControllerFragment fragment;
	
	public ProgramListAdapter(Context context, List<CpData> objects) {
		super(context, objects);
	}
	
	public void setFragment(VideoControllerFragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_program_list, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.program_name);
		TextView time = ViewHolder.get(convertView, R.id.program_time);
		TextView liveBtn = ViewHolder.get(convertView, R.id.live_btn);
		CpData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String startTime = temp.startTime;
		if (startTime != null) {
			time.setVisibility(View.VISIBLE);
			time.setText(startTime);
		} else {
			time.setVisibility(View.GONE);
		}

		liveBtn.setOnClickListener((OnClickListener) fragment);
		liveBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					fragment.setHideDelay();
				} else {
					fragment.cancelHideDelay();
				}
				return false;
			}
		});
		switch (temp.isPlaying) {
		case CpData.TYPE_LIVE:
			liveBtn.setVisibility(View.VISIBLE);
			liveBtn.setText("直播");
			if (selectedPosition == position) {
				nameTxt.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
				time.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
			} else {
				nameTxt.setTextColor(Color.WHITE);
				time.setTextColor(Color.WHITE);
			}
			liveBtn.setTag(R.id.item_pos, position);
			liveBtn.setTag(R.id.item_bool, temp.isPlaying);
			liveBtn.setTag(R.id.item_back, "");
			liveBtn.setBackgroundResource(R.drawable.live_btn_normal);
			liveBtn.setTextColor(Color.parseColor("#5fd5ff"));
			break;
		case CpData.TYPE_REVIEW:
			liveBtn.setVisibility(View.GONE);
			liveBtn.setTag(R.id.item_pos, position);
			liveBtn.setTag(R.id.item_back, temp.backUrl);
			liveBtn.setTag(R.id.item_bool, temp.isPlaying);
			if (!TextUtils.isEmpty(temp.backUrl)) {
				liveBtn.setVisibility(View.VISIBLE);
				liveBtn.setText("回看");
			} else {
				liveBtn.setVisibility(View.GONE);
			}
			if (selectedPosition == position) {
				nameTxt.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
				time.setTextColor(context.getResources().getColor(R.color.navigator_bg_color));
			} else {
				nameTxt.setTextColor(Color.parseColor("#878787"));
				time.setTextColor(Color.parseColor("#878787"));
			}
			liveBtn.setBackgroundResource(R.drawable.live_btn_normal);
			liveBtn.setTextColor(Color.parseColor("#5fd5ff"));
			break;
		case CpData.TYPE_UNPLAY:
			liveBtn.setVisibility(View.VISIBLE);
			liveBtn.setText("预约");
			nameTxt.setTextColor(Color.WHITE);
			time.setTextColor(Color.WHITE);
			if (temp.order == 1) {
				liveBtn.setBackgroundResource(R.drawable.live_btn_selected);
				liveBtn.setTextColor(Color.parseColor("#53a45a"));
			} else {
				liveBtn.setBackgroundResource(R.drawable.live_btn_normal);
				liveBtn.setTextColor(Color.parseColor("#5fd5ff"));
			}
			liveBtn.setTag(R.id.item_pos, position);
			liveBtn.setTag(R.id.item_bool, temp.isPlaying);
			liveBtn.setTag(R.id.item_back, "");
			break;
		}
		return convertView;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
	
}