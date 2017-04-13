package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 我的徽章适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyMedalListAdapter extends IBaseAdapter<BadgeData> {

	public MyMedalListAdapter(Context context, List<BadgeData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_badge_medal, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView medalPic = ViewHolder.get(convertView, R.id.pic);
		BadgeData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = temp.createTime;
		if (intro != null) {
			introTxt.setText(intro);
		}
		String url = temp.picPath;
		medalPic.setTag(url);
		loadImage(medalPic, url, R.drawable.medal_default);

		return convertView;
	}

}
