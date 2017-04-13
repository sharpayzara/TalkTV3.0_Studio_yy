package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.components.ResizableImageView;
import com.sumavision.talktv2.utils.ViewHolder;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class ShakeProgramAdapter extends IBaseAdapter<List<ProgramData>>{

	public ShakeProgramAdapter(Context context, List<List<ProgramData>> objects) {
		super(context, objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResizableImageView imageView, imageView1;
		ImageView userPic, userPic1;
		TextView name, intro, name1, intro1,titleView,titleView2;
		View subjectView,subjectView2;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_rcmd_column_normal,
					null);
		}
		final List<ProgramData> list = getItem(position);
		imageView = ViewHolder.get(convertView, R.id.imageView);
		name = ViewHolder.get(convertView, R.id.name);
		intro = ViewHolder.get(convertView, R.id.intro);
		userPic = ViewHolder.get(convertView, R.id.user_pic);
		userPic.setVisibility(View.VISIBLE);
		titleView = ViewHolder.get(convertView, R.id.title);
		subjectView = ViewHolder.get(convertView, R.id.tv_special_flag);
		titleView2 = ViewHolder.get(convertView, R.id.title1);
		subjectView2 = ViewHolder.get(convertView, R.id.tv_special_flag1);
		titleView.setVisibility(View.GONE);
		titleView2.setVisibility(View.GONE);
		subjectView.setVisibility(View.GONE);
		subjectView2.setVisibility(View.GONE);
		
		imageView1 = ViewHolder.get(convertView, R.id.imageView1);
		name1 = ViewHolder.get(convertView, R.id.name1);
		intro1 = ViewHolder.get(convertView, R.id.intro1);
		userPic1 = ViewHolder.get(convertView, R.id.user_pic1);
		
		if (list.size() == 2) {
			loadImage(imageView, list.get(0).pic, R.drawable.aadefault);
			loadImage(userPic, list.get(0).userPic, R.drawable.icon);
			name.setText(list.get(0).name);
			intro.setText(list.get(0).userName);
			
			loadImage(imageView1, list.get(1).pic, R.drawable.aadefault);
			loadImage(userPic1, list.get(1).userPic, R.drawable.icon);
			name1.setText(list.get(1).name);
			intro1.setText(list.get(1).userName);
			imageView1.setVisibility(View.VISIBLE);
			userPic1.setVisibility(View.VISIBLE);
			name1.setVisibility(View.VISIBLE);
			intro1.setVisibility(View.VISIBLE);
		} else if (list.size() == 1) {
			loadImage(imageView, list.get(0).pic, R.drawable.aadefault);
			loadImage(userPic, list.get(0).userPic, R.drawable.icon);
			name.setText(list.get(0).name);
			intro.setText(list.get(0).userName);
			imageView1.setVisibility(View.INVISIBLE);
			userPic1.setVisibility(View.GONE);
			name1.setVisibility(View.GONE);
			intro1.setVisibility(View.GONE);
		}
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(context, "yjpkanjiapian");
				Intent playerIntent = new Intent(context,PlayerActivity.class);
				playerIntent.putExtra("isHalf", true);
				if (list.get(0).cpId>0){
					playerIntent.putExtra("id", list.get(0).cpId);
					playerIntent.putExtra("subid",(int)list.get(0).topicId);
					playerIntent.putExtra("playType", PlayerActivity.VOD_PLAY);
				} else {
					playerIntent.putExtra("id", list.get(0).programId);
					playerIntent.putExtra("playType", PlayerActivity.SHAKE_PLAY);
				}
				context.startActivity(playerIntent);
			}
		});
		imageView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MobclickAgent.onEvent(context, "yjpkanjiapian");
				Intent playerIntent = new Intent(context,PlayerActivity.class);
				playerIntent.putExtra("isHalf",true);
				if (list.get(1).cpId>0){
					playerIntent.putExtra("id", list.get(1).cpId);
					playerIntent.putExtra("subid",(int)list.get(1).topicId);
					playerIntent.putExtra("playType", PlayerActivity.VOD_PLAY);
				} else {
					playerIntent.putExtra("id", list.get(1).programId);
					playerIntent.putExtra("playType", PlayerActivity.SHAKE_PLAY);
				}
				context.startActivity(playerIntent);
			}
		});
		return convertView;
	}
}
