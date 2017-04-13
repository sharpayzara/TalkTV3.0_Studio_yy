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
 * 明星页 节目适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StarProgramAdapter extends IBaseAdapter<VodProgramData> {

	public StarProgramAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_start_program, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.star_p_list_name);
		TextView typeTxt = ViewHolder.get(convertView, R.id.star_p_list_update);
		ImageView programPic = ViewHolder
				.get(convertView, R.id.star_p_list_pic);
		VodProgramData program = getItem(position);
		String name = program.name;
		if (program.name != null) {
			nameTxt.setText(name);
		}
		String typeName = program.contentTypeName;
		if (typeName != null) {
			typeTxt.setText(typeName);
		}
		String url = program.pic;
		programPic.setTag(url);
		loadImage(programPic, url, R.drawable.list_star_default);
		return convertView;
	}

}
