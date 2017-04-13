package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ChaseData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 追剧列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChaseAdapter extends IBaseAdapter<ChaseData> {
	private int userId;

	public ChaseAdapter(Context context, int userId, List<ChaseData> objects) {
		super(context, objects);
		this.userId = userId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_chase, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTextView = ViewHolder.get(convertView, R.id.tvName);
		ImageView cancelTextView = ViewHolder.get(convertView, R.id.cancel);
		ChaseData data = getItem(position);
		String name = data.programName;
		if (name != null && !name.equals("")) {
			nameTxt.setText(name);
		}

		String intro = data.latestSubName;
		if (intro != null) {
			introTextView.setText(intro);
		}
		if (userId == UserNow.current().userID) {
			cancelTextView.setOnClickListener((OnClickListener) context);
			cancelTextView.setTag(R.id.item_pos, position);
			cancelTextView.setTag(R.id.item_id, data.programId);
		} else {
			cancelTextView.setVisibility(View.GONE);
		}

		String url = data.programPic;
		pic.setTag(url);
		loadImage(pic, url, R.drawable.list_star_default);
		return convertView;
	}

}
