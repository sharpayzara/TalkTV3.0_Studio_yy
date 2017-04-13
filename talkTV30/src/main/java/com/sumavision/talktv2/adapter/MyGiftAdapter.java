package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ExchangeGood;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

public class MyGiftAdapter extends IBaseAdapter<ExchangeGood> {
	public MyGiftAdapter(Context context, List<ExchangeGood> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_my_gift, null);
		}
		ImageView logoImg = ViewHolder.get(convertView, R.id.logo);
		TextView receiveBtn = ViewHolder.get(convertView, R.id.tv_receive);
		final ExchangeGood gift = getItem(position);
		if (gift.pic != null && gift.pic.length() > 0) {
			loadImage(logoImg, Constants.picUrlFor + gift.pic
					+ Constants.PIC_SUFF, R.drawable.emergency_txt_bg_pic);
		}
		if (gift.status == ExchangeGood.STATUS_OVER) {
			receiveBtn.setText(R.string.over);
			receiveBtn.setTextColor(context.getResources().getColor(
					R.color.gray));
			receiveBtn.setBackgroundResource(R.drawable.my_gift_text_over_bg);
		} else if (gift.status == ExchangeGood.STATUS_RECEIVED) {
			receiveBtn.setText(R.string.received);
			receiveBtn.setTextColor(context.getResources().getColor(
					R.color.gray));
			receiveBtn.setBackgroundResource(R.drawable.my_gift_text_over_bg);
		} else {
			receiveBtn
					.setBackgroundResource(R.drawable.my_gift_text_receive_bg);
			receiveBtn.setText(R.string.receive);
			receiveBtn.setTextColor(context.getResources().getColor(
					R.color.navigator_bg_color));
		}
		if (gift.isHolidayGoods){
			receiveBtn
					.setBackgroundResource(R.drawable.my_gift_text_receive_bg);
			receiveBtn.setText("查看");
			receiveBtn.setTextColor(context.getResources().getColor(
					R.color.navigator_bg_color));
		}
		return convertView;
	}
}
