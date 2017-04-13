package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 搜索好友
 * @author suma-hpb
 *
 */
public class FriendSearchAdapter extends IBaseAdapter<User> {

	public FriendSearchAdapter(Context context, List<User> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_friend_search, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView headPic = ViewHolder.get(convertView, R.id.user_header);
		ImageView vipMark = ViewHolder.get(convertView, R.id.user_vip_mark);
		User user = getItem(position);
		nameTxt.setText(user.name);
		introTxt.setText(user.signature);
		loadImage(headPic, user.iconURL, R.drawable.list_headpic_default);
		if (user.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		return convertView;
	}
}
