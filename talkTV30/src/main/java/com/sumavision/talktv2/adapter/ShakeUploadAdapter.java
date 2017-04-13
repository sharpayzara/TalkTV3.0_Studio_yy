package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

public class ShakeUploadAdapter extends IBaseAdapter<VodProgramData>{
	
	private boolean editMode = false;

	public ShakeUploadAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_uploaded_program, null);
		}
		VodProgramData v = getItem(position);
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		TextView praise = ViewHolder.get(convertView, R.id.zan_number);
		TextView title = ViewHolder.get(convertView, R.id.title);
		CheckBox box = ViewHolder.get(convertView, R.id.checkbox);
		
		loadImage(pic, v.pic, R.drawable.aadefault);
		praise.setText(v.monthGoodCount + "");
		title.setText(v.name);
		if (editMode) {
			box.setVisibility(View.VISIBLE);
		} else {
			box.setVisibility(View.GONE);
		}
		box.setChecked(v.selected);
		return convertView;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}
