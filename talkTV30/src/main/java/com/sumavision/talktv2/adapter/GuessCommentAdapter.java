package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.CommentData;

/**
 * 竞猜评论适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class GuessCommentAdapter extends IBaseAdapter<CommentData> {

	public GuessCommentAdapter(Context context, List<CommentData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_guess_comment, null);
		}
		ImageView header = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.user_header);
		TextView name = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.user_name);
		TextView comment = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.user_comment);
		TextView timeTxt = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.time);
		CommentData data = getItem(position);
		loadImage(header, data.userURL, R.drawable.aadefault);
		name.setText(data.userName);
		if (data.commentTime.length() > 0) {
			String str = data.commentTime.split(" ")[0];
			timeTxt.setText(str);
		}

		comment.setText(data.content);

		return convertView;
	}
}
