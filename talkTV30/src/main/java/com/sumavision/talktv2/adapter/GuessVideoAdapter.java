package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;

/**
 * 竞猜视频适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessVideoAdapter extends IBaseAdapter<VodProgramData> {

	public GuessVideoAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView pic;
		TextView nameTxt, timeTxt, updateTxt, introTxt, countTxt;
		VodProgramData program = getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (program.playType == 1) {
				convertView = inflater.inflate(R.layout.item_guess_video_live,
						null);
			} else {
				convertView = inflater.inflate(R.layout.item_guess_video, null);
			}
		}
		if (program.playType == 1) {
			pic = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.pic);
			nameTxt = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.name);
			timeTxt = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.time);
			loadImage(pic, program.pic, R.drawable.aadefault);
			nameTxt.setText(program.cpName);
			timeTxt.setText(program.startTime + " — " + program.endTime);
		} else {
			// 非直播
			pic = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.pic);
			nameTxt = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.name);
			countTxt = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.play_count);
			updateTxt = com.sumavision.talktv2.utils.ViewHolder.get(
					convertView, R.id.update);
			introTxt = com.sumavision.talktv2.utils.ViewHolder.get(convertView,
					R.id.intro);
			loadImage(pic, program.pic, R.drawable.aadefault);
			nameTxt.setText(program.name);
			countTxt.setText(String.valueOf(program.playTimes));
			updateTxt.setText(program.updateName);
			introTxt.setText(program.shortIntro);
		}

		return convertView;
	}
}
