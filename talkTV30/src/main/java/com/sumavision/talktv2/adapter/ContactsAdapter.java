package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ConnectBean;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 联系人适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ContactsAdapter extends IBaseAdapter<ConnectBean> {

	public ContactsAdapter(Context context, List<ConnectBean> objects) {
		super(context, objects);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_contacts, null);
		}
		ImageView image = ViewHolder.get(convertView, R.id.color_image);
		TextView title = ViewHolder.get(convertView, R.id.color_title);
		ConnectBean cb = getItem(position);
		if (cb.mContactsPhoto != null)
			image.setImageBitmap(cb.mContactsPhoto);
		else
			image.setImageResource(R.drawable.activity_contants_list_icon);
		title.setText(cb.mContactsName);
		return convertView;
	}

}
