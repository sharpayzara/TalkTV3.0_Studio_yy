package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 专题 列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialAdapter extends IBaseAdapter<ColumnData> {

	public SpecialAdapter(Context context, List<ColumnData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_special, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView programPic = ViewHolder.get(convertView, R.id.pic);
		TextView viewCountText = ViewHolder.get(convertView, R.id.viewercount);
		ColumnData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = temp.intro;
		if (intro != null) {
			introTxt.setText(intro);
		}
		int viewerCount = temp.playTimes;
		viewCountText.setText(CommonUtils.processPlayCount(viewerCount));
		String url = temp.pic;
		programPic.setTag(url);
		loadImage(programPic, url, R.drawable.emergency_iv_bg_pic);
		return convertView;
	}
}
