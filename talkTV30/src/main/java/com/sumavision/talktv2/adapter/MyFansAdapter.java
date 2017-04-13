package com.sumavision.talktv2.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.ViewHolder;

import org.apache.http.message.HeaderValueParser;

/**
 * 粉丝列表适配器
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MyFansAdapter extends IBaseAdapter<User> {
	private Fragment mFragment;

	public MyFansAdapter(Fragment mFragment, List<User> objects) {
		super(mFragment.getActivity(), objects);
		this.mFragment = mFragment;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_my_fans, null);
		}
		ImageView headpic = ViewHolder.get(convertView, R.id.user_header);
		headpic.setScaleType(ImageView.ScaleType.CENTER_CROP);
		ImageView vipMark = ViewHolder.get(convertView, R.id.user_vip_mark);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView fellowTextView = ViewHolder.get(convertView, R.id.guanzhu);
		ImageView sendMsgTextView = ViewHolder
				.get(convertView, R.id.privatemsg);
		final User temp = getItem(position);
		String name = temp.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = temp.intro;
		if (intro != null) {
			introTxt.setText(intro);
		}
		String signature = temp.signature;
		if (signature != null) {
			introTxt.setText(signature);
		}
		String url = temp.iconURL;
		loadImage(headpic, url, R.drawable.list_headpic_default);
		sendMsgTextView.setTag(temp);
		sendMsgTextView.setOnClickListener((OnClickListener) mFragment);
		if (temp.isFriend == 0 && temp.userId != UserNow.current().userID) {
			fellowTextView.setVisibility(View.VISIBLE);
			fellowTextView.setTag(R.id.item_fellow_userId, temp.userId);
			fellowTextView.setTag(R.id.item_fellow_pos, position);
			fellowTextView.setOnClickListener((OnClickListener) mFragment);
		} else {
			fellowTextView.setVisibility(View.GONE);
		}
		if (temp.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		return convertView;
	}

}
