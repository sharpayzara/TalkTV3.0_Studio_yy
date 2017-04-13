package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 更多搜索结果页
 * 
 * @author suma-hpb
 * 
 */
public class SearchMoreAdaper extends IBaseAdapter<VodProgramData> {

	public SearchMoreAdaper(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search_more, null);
		}
		TextView nameTxt = (TextView) ViewHolder.get(convertView, R.id.name);
		TextView typeTxt = (TextView) ViewHolder.get(convertView, R.id.type);
		TextView subNameTxt = (TextView)ViewHolder.get(convertView, R.id.sub_name);
		ImageView programPic = (ImageView) ViewHolder
				.get(convertView, R.id.pic);
		VodProgramData program = getItem(position);
		String typeName = VodProgramData.ProgramType.getTypeName(program.ptype);
		if (typeName != null) {
			nameTxt.setText(program.name);
			typeTxt.setVisibility(View.VISIBLE);
			typeTxt.setText(typeName);
		} else {
			nameTxt.setText(program.name);
			typeTxt.setVisibility(View.GONE);
		}
		String url = program.pic;
		if (program.subSearchType == 0) {
			loadImage(programPic, url, R.drawable.aadefault);
		} else if (program.subSearchType == VodProgramData.SEARCH_TYPE_VIDEO) {
			loadImage(programPic, null, R.drawable.search_video_bg);
			subNameTxt.setText(program.subName);
		}
		return convertView;
	}
}
