package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 私信列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class PrivateMsgAdapter extends IBaseAdapter<MailData> {

	public PrivateMsgAdapter(Context context, List<MailData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_my_msg, null);
		}
		if(position%2==0){
			convertView.setBackgroundResource(R.drawable.fav_item_even_bg);
		}else{
			convertView.setBackgroundResource(R.drawable.fav_item_odd_bg);
		}
		ImageView headpic = ViewHolder.get(convertView, R.id.user_header);
		ImageView vipMark = ViewHolder.get(convertView, R.id.user_vip_mark);
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.lastmsg);
		TextView unread = ViewHolder.get(convertView, R.id.tv_unread);
		TextView time = ViewHolder.get(convertView, R.id.time);
		MailData mail = getItem(position);
		String name = mail.sUserName;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = mail.content;
		if (intro != null) {
			introTxt.setText(intro);
		}
		String timeValue = mail.timeStemp;
		if (timeValue != null) {
			time.setText(timeValue);
		}
		StringBuffer keyBuf = new StringBuffer(Constants.key_privateMsg);
		keyBuf.append("_").append(mail.sid).append("-")
				.append(UserNow.current().userID);
		if (PreferencesUtils.getBoolean(context, Constants.pushMessage,
				keyBuf.toString())) {
			unread.setVisibility(View.VISIBLE);
			nameTxt.setSelected(true);
		} else {
			unread.setVisibility(View.GONE);
			nameTxt.setSelected(false);
		}
		loadImage(headpic, mail.sUserPhoto, R.drawable.list_headpic_default);
		if (mail.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		return convertView;
	}

}
