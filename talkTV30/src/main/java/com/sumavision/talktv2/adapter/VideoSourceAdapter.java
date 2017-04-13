package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

/**
 * 追剧列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class VideoSourceAdapter extends BaseAdapter {
	Context context;
	List<SourcePlatform> objects;
	ImageLoaderHelper mImageLoaderHelper;
	public VideoSourceAdapter(Context context, List<SourcePlatform> objects) {
		this.context = context;
		this.objects = objects;
		mImageLoaderHelper = new ImageLoaderHelper();
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public SourcePlatform getItem(int i) {
		return objects.get(i);
	}

	@Override
	public long getItemId(int i) {
		return objects.get(i).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_spinner_source, null);
		}
		ImageView logo = ViewHolder.get(convertView, R.id.spinner_item_image);
		mImageLoaderHelper.loadImage(logo, getItem(position).pic, R.drawable.play_source_default);
		TextView nameView = ViewHolder.get(convertView, R.id.spinner_item_text);
		nameView.setText(TextUtils.isEmpty(getItem(position).name) ? "互联网":getItem(position).name);
		return convertView;
	}

}
