package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.CommonUtils;

/**
 * 图片浏览页适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ShowImageAdapter extends IBaseAdapter<String> {
	public ShowImageAdapter(Context context, Point point, String[] objects) {
		super(context, objects);
		this.point = point;
	}

	private Point point;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_show_image, null);
		}
		ImageView imageView = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.showpic_imageView);
		String url = getItem(position);

		int h = CommonUtils.dip2px(
				context,
				CommonUtils.px2dip(context,
						point.y - 40 - CommonUtils.dip2px(context, 48)));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				point.x, h);
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.CENTER_CROP);

		imageView.setTag(url);
		loadImage(imageView, url, R.drawable.program_pic_default);
		return convertView;
	}

}