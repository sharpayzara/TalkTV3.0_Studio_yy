package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 专题栏目 适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SpecialColumnAdapter extends IBaseAdapter<VodProgramData> {

	public SpecialColumnAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_special_column, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView updateTxt = ViewHolder.get(convertView, R.id.update);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		TextView viewerCount = ViewHolder.get(convertView, R.id.viewercount);
		ImageView programPic = ViewHolder.get(convertView, R.id.pic);
		TextView scoreView = ViewHolder.get(convertView, R.id.score);
		VodProgramData temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String updateText = temp.updateName;
		if (updateText != null && temp.ptype != 2) {
			updateTxt.setText(updateText);
		}
		//type=2即电影类节目时不显示updatename
		if (temp.ptype == 2) {
			updateTxt.setText(" ");
		}
		viewerCount.setText(CommonUtils.processPlayCount(temp.playTimes));
		String intro = temp.shortIntro;
		if (intro != null) {
			introTxt.setText(intro);
		}
		String score = temp.point;
		if (score != null) {
			if (score.length() > 3) {
				score = score.substring(0, 3);
			}
			scoreView.setText(score + "分");
			scoreView.setVisibility(View.VISIBLE);
		} else {
			scoreView.setVisibility(View.GONE);
		}
		String url = temp.pic;
		programPic.setTag(url);
		loadImage(programPic, url, R.drawable.pd_program_pic);
		return convertView;
	}

}
