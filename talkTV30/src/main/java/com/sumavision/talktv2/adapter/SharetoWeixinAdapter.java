package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 微信分享页适配:sharetoweixin
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SharetoWeixinAdapter extends IBaseAdapter<String> {
	public SharetoWeixinAdapter(Context context, List<String> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_share_weixin, null);
		}
		TextView textView = ViewHolder.get(convertView, R.id.name);
		String name = getItem(position);
		if (name != null) {
			textView.setText(name);
		}
		return convertView;
	}

}
