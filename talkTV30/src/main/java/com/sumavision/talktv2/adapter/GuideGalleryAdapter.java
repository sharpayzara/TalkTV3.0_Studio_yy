package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumavision.talktv2.R;

/**
 * 图片adapter
 * 
 * 
 * @author:zhaohongru
 * 
 * @time:2013-12-5 下午2:35:23
 */
public class GuideGalleryAdapter extends IBaseAdapter<String> {
	Uri uri;
	Intent intent;
	ImageView imageView;
	List<String> imageUrls;

	public GuideGalleryAdapter(List<String> imageUrls, Context context) {
		super(context, imageUrls);
		this.imageUrls = imageUrls;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View View = convertView;
		if (View == null) {
			View = LayoutInflater.from(context).inflate(R.layout.item_gallery,
					null);
			imageView = (ImageView) View.findViewById(R.id.gallery_image);

		}
		position = position % imageUrls.size();
		loadImage(imageView, imageUrls.get(position), R.drawable.program_pic_default);
		return View;

	}

}
