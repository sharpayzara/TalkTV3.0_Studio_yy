package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

/**
 * 图片adapter
 * 
 * 
 * 
 */
public class RecommendImageAdapter extends IBaseAdapter<String> {
	public RecommendImageAdapter(List<String> objects, Context context) {
		super(context, objects);
		this.imageUrls = objects;
	}

	private List<String> imageUrls; // 图片地址list

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
//		return imageUrls.size();
	}

	@Override
	public String getItem(int position) {
		return imageUrls.get(position % imageUrls.size());
	}

	@Override
	public long getItemId(int position) {
		return position % imageUrls.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.gallery_item, null);
		}
		ImageView imageView = ViewHolder.get(convertView, R.id.gallery_image);
		position = position % imageUrls.size();
		loadImageCacheDisk(imageView, imageUrls.get(position),
				R.drawable.emergency_pic_bg_detail);
		return convertView;

	}

}
