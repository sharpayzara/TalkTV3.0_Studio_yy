package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 收藏
 * 
 * @author suma-hpb
 * 
 */
public class FavAdapter extends IBaseAdapter<VodProgramData> {
	boolean isLive;

	public FavAdapter(Context context, boolean isLive,
			List<VodProgramData> objects) {
		super(context, objects);
		this.isLive = isLive;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_fav, null);
		}
		VodProgramData program = getItem(position);
		ImageView picView = ViewHolder.get(convertView, R.id.imgv_pic);
		ImageView updateView = ViewHolder.get(convertView, R.id.imgv_new);
		TextView titleTxt = ViewHolder.get(convertView, R.id.tv_title);
		TextView channelNameTxt = ViewHolder.get(convertView,
				R.id.tv_channel_name);
		TextView updateTxt = ViewHolder.get(convertView, R.id.tv_update);
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.fav_item_even_bg);
		} else {
			convertView.setBackgroundResource(R.drawable.fav_item_odd_bg);
		}
		if (isLive) {
			updateView.setVisibility(View.GONE);
			titleTxt.setText(program.cpName);
			channelNameTxt.setText(program.channelName);
			StringBuffer update = new StringBuffer();
			update.append(program.startTime).append("-")
					.append(program.endTime).append("(").append(program.cpDate)
					.append(")");
			updateTxt.setText(update.toString());
			String url = program.channelLogo;
			loadImage(picView, url, R.drawable.channel_tv_logo_bg);
		} else {
			titleTxt.setText(program.name);
			updateTxt.setText(program.updateName);
			StringBuffer itemKey = new StringBuffer(UserNow.current().userID);
			itemKey.append("_").append(program.id);
			int oldSubId = PreferencesUtils.getInt(getContext(),
					Constants.SP_FAV, itemKey.toString(), 0);
			if (!TextUtils.isEmpty(program.subId) && oldSubId > 0) {
				int subId = Integer.parseInt(program.subId);
				if (subId > oldSubId) {
					updateView.setVisibility(View.VISIBLE);
				} else {
					updateView.setVisibility(View.GONE);
				}
			} else {
				if (!TextUtils.isEmpty(program.subId) && oldSubId == 0) {
					PreferencesUtils
							.putInt(getContext(), Constants.SP_FAV,
									itemKey.toString(),
									Integer.parseInt(program.subId));
				}
				updateView.setVisibility(View.GONE);
			}
			loadImage(picView, program.pic, R.drawable.aadefault);
		}
		return convertView;
	}
}
