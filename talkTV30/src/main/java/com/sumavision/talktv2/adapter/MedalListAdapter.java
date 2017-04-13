package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.PlayNewData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 徽章列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MedalListAdapter extends IBaseAdapter<PlayNewData> {

	public MedalListAdapter(Context context, List<PlayNewData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_medal,
					null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView medalPic = ViewHolder.get(convertView, R.id.pic);
		ImageView statusPic = ViewHolder.get(convertView, R.id.status);
		PlayNewData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}

		String intro = temp.intro;
		if (intro != null) {
			introTxt.setText(intro);
		}
		String url = temp.pic;
		medalPic.setTag(url);
		loadImage(medalPic, url, R.drawable.medal_default);
		if (temp.state == 2) {
			statusPic.setImageResource(R.drawable.activity_doing);
			if (temp.joinStatus == 3)
				statusPic.setImageResource(R.drawable.activity_get);
		} else if (temp.state == 3) {
			statusPic.setImageResource(R.drawable.activity_finish);
		} else if (temp.state == 1) {
			statusPic.setImageResource(R.drawable.activity_notbegin);
		}
		return convertView;
	}

}
