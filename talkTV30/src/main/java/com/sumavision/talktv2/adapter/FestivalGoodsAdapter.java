package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.utils.ViewHolder;

public class FestivalGoodsAdapter extends IBaseAdapter<HotGoodsBean> {

	public FestivalGoodsAdapter(Context context, List<HotGoodsBean> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_goods_detail, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		TextView title = ViewHolder.get(convertView, R.id.title);
		TextView intro = ViewHolder.get(convertView, R.id.intro);

		HotGoodsBean data = getItem(position);
		loadImage(pic, data.pic, R.drawable.aadefault);
		title.setText(data.name);
		intro.setText(data.intro);
		
		return convertView;
	}
}
