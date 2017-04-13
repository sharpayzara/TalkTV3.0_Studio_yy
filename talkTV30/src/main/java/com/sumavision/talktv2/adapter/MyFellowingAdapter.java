package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.UserMailActivity;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.ViewHolder;

public class MyFellowingAdapter extends IBaseAdapter<User> {

	private Fragment fragment;
	private int id;

	public MyFellowingAdapter(Fragment fragment, int id, List<User> objects) {
		super(fragment.getActivity(), objects);
		this.fragment = fragment;
		this.id = id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_myfellowing, null);
		}
		ImageView headpic = (ImageView) ViewHolder.get(convertView, R.id.user_header);
		headpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ImageView vipMark = ViewHolder.get(convertView, R.id.user_vip_mark);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView cancelFellowTextView = ViewHolder.get(convertView,
				R.id.guanzhu);
		ImageView sendMsgTextView = ViewHolder
				.get(convertView, R.id.privatemsg);
		final User temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = temp.intro;
		if (intro != null && !intro.equals("")) {
			introTxt.setText(intro);
		} else {
			introTxt.setText("这个家伙神马也木有留下");
		}
		String signature = temp.signature;
		if (signature != null) {
			introTxt.setText(signature);
		}
		String url = temp.iconURL;
		loadImage(headpic, url, R.drawable.list_headpic_default);

		if (temp.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		if (id == UserNow.current().userID) {
			sendMsgTextView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, UserMailActivity.class);
					intent.putExtra("otherUserName", temp.name);
					intent.putExtra("otherUserId", temp.userId);
					context.startActivity(intent);
				}
			});
			cancelFellowTextView.setTag(R.id.item_pos, position);
			cancelFellowTextView.setTag(R.id.item_id, temp.userId);
			cancelFellowTextView.setOnClickListener((OnClickListener) fragment);
		} else {
			sendMsgTextView.setVisibility(View.GONE);
			cancelFellowTextView.setVisibility(View.GONE);
		}
		return convertView;
	}
}
