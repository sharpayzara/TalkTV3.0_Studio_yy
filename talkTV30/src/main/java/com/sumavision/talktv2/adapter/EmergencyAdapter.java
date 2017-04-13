package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.EmergencyDetailActivity;
import com.sumavision.talktv2.activity.ShowImageActivity;
import com.sumavision.talktv2.activity.StarDetailActivity;
import com.sumavision.talktv2.bean.EmergencyObjectData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.StringUtils;

public class EmergencyAdapter extends IBaseAdapter<EmergencyObjectData> {

	public EmergencyAdapter(Context context, List<EmergencyObjectData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (AppUtil.isPad(context)) {
				convertView = inflater.inflate(
						R.layout.emergency_story_item_pad, null);
			} else {
				convertView = inflater.inflate(R.layout.emergency_story_item,
						null);
			}
			// 新闻
			viewHolder.itemRetxt = (RelativeLayout) convertView
					.findViewById(R.id.wangyi_item_retxt);
			viewHolder.itemIv = (ImageView) convertView
					.findViewById(R.id.item_iv);
			viewHolder.itemTxt = (TextView) convertView
					.findViewById(R.id.item_txt);
			viewHolder.itemDetailtxt = (TextView) convertView
					.findViewById(R.id.item_txt_detail);
			viewHolder.itemCommentxt = (TextView) convertView
					.findViewById(R.id.item_txt_comment);
			// 视频
			viewHolder.itemRevideotxt = (RelativeLayout) convertView
					.findViewById(R.id.wangyi_item_revideo);
			viewHolder.itemvideoIv = (ImageView) convertView
					.findViewById(R.id.item_video_iv);
			viewHolder.itemvideoTxt = (TextView) convertView
					.findViewById(R.id.item_video_txt);
			viewHolder.itemvideoDetailtxt = (TextView) convertView
					.findViewById(R.id.item_video_txt_detail);
			viewHolder.itemvideoCommentxt = (TextView) convertView
					.findViewById(R.id.item_video_txt_comment);

			// 明星图片
			viewHolder.itemRepic = (RelativeLayout) convertView
					.findViewById(R.id.wangyi_item_repic);
			viewHolder.itemIv1Rel = (RelativeLayout) convertView
					.findViewById(R.id.item_iv1_re);
			viewHolder.itemIv2Rel = (RelativeLayout) convertView
					.findViewById(R.id.item_iv2_re);
			viewHolder.itemIv3Rel = (RelativeLayout) convertView
					.findViewById(R.id.item_iv3_re);
			viewHolder.itemIv1 = (ImageView) convertView
					.findViewById(R.id.item_iv1);
			viewHolder.itemIv2 = (ImageView) convertView
					.findViewById(R.id.item_iv2);
			viewHolder.itemIv3 = (ImageView) convertView
					.findViewById(R.id.item_iv3);
			// 花絮
			viewHolder.itemRepic1 = (RelativeLayout) convertView
					.findViewById(R.id.wangyi_item_repic1);
			viewHolder.itemIv11 = (ImageView) convertView
					.findViewById(R.id.item_iv11);
			viewHolder.itemIv12 = (ImageView) convertView
					.findViewById(R.id.item_iv12);
			viewHolder.itemIv13 = (ImageView) convertView
					.findViewById(R.id.item_iv13);
			viewHolder.itemtitle1 = (TextView) convertView
					.findViewById(R.id.wangyi_item_repic1_title);
			viewHolder.itembottom = (TextView) convertView
					.findViewById(R.id.wangyi_item_repic1_bottom);
			viewHolder.itembottom1 = (TextView) convertView
					.findViewById(R.id.wangyi_item_repic1_bottom1);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		EmergencyObjectData emergencyData = getItem(position);
		if (emergencyData.objectType == 1) {// 新闻
			viewHolder.itemRetxt.setVisibility(View.VISIBLE);
			viewHolder.itemRepic.setVisibility(View.GONE);
			viewHolder.itemRepic1.setVisibility(View.GONE);
			viewHolder.itemRevideotxt.setVisibility(View.GONE);
			try {
				if (emergencyData.objectPic.get(0).objectpicpic != null
						&& !emergencyData.objectPic.get(0).objectpicpic
								.equals("")) {
					String picUrl = Constants.picUrlFor
							+ emergencyData.objectPic.get(0).objectpicpic
							+ "m.jpg";
					loadImage(viewHolder.itemIv, picUrl,
							R.drawable.emergency_txt_bg_pic);

				}
				viewHolder.itemCommentxt.setText(emergencyData.objectTalkcount
						+ "评论");
				viewHolder.itemDetailtxt.setText(emergencyData.objectIntro);
				// viewHolder.itemDetailBelowtxt.setText("");
				viewHolder.itemTxt.setText(emergencyData.objectTitle);
				viewHolder.itemRetxt.setTag(R.id.item_pos, position);
				viewHolder.itemRetxt.setOnClickListener(new ImageClick());
			} catch (Exception e) {

			}
		} else if (emergencyData.objectType == 2) {// 视频
			viewHolder.itemRevideotxt.setVisibility(View.VISIBLE);
			viewHolder.itemRetxt.setVisibility(View.GONE);
			viewHolder.itemRepic.setVisibility(View.GONE);
			viewHolder.itemRepic1.setVisibility(View.GONE);
			try {
				if (emergencyData.objectPic.get(0).objectpicpic != null
						&& !emergencyData.objectPic.get(0).objectpicpic
								.equals("")) {
					String picUrl = Constants.picUrlFor
							+ emergencyData.objectPic.get(0).objectpicpic
							+ "m.jpg";
					loadImage(viewHolder.itemvideoIv, picUrl,
							R.drawable.emergency_txt_bg_pic);

				}
				viewHolder.itemvideoCommentxt.setText("视频");
				viewHolder.itemvideoDetailtxt
						.setText(emergencyData.objectIntro);
				viewHolder.itemvideoTxt.setText(emergencyData.objectTitle);
				viewHolder.itemRevideotxt.setTag(R.id.item_pos, position);
				viewHolder.itemRevideotxt.setOnClickListener(new ImageClick());
			} catch (Exception e) {

			}
		} else if (emergencyData.objectType == 3) {// 明星
			viewHolder.itemRetxt.setVisibility(View.GONE);
			viewHolder.itemRepic.setVisibility(View.VISIBLE);
			viewHolder.itemRepic1.setVisibility(View.GONE);
			viewHolder.itemRevideotxt.setVisibility(View.GONE);
			if (StringUtils
					.isNotEmpty(emergencyData.objectPic.get(0).objectpicpic)) {
				String picUrl = Constants.picUrlFor
						+ emergencyData.objectPic.get(0).objectpicpic + "m.jpg";
				loadImage(viewHolder.itemIv1, picUrl,
						R.drawable.emergency_txt_bg_pic);

			}
			if (StringUtils
					.isNotEmpty(emergencyData.objectPic.get(1).objectpicpic)) {
				String picUrl = Constants.picUrlFor
						+ emergencyData.objectPic.get(1).objectpicpic + "m.jpg";
				loadImage(viewHolder.itemIv2, picUrl,
						R.drawable.emergency_txt_bg_pic);

			}
			if (emergencyData.objectPic.size() > 2
					&& StringUtils
							.isNotEmpty(emergencyData.objectPic.get(2).objectpicpic)) {
				String picUrl = Constants.picUrlFor
						+ emergencyData.objectPic.get(2).objectpicpic + "m.jpg";
				loadImage(viewHolder.itemIv3, picUrl,
						R.drawable.emergency_txt_bg_pic);

			}
			viewHolder.itemIv1Rel.setTag(R.id.item_pos, position);
			viewHolder.itemIv1Rel.setTag(R.id.item_id, 0);
			viewHolder.itemIv2Rel.setTag(R.id.item_pos, position);
			viewHolder.itemIv2Rel.setTag(R.id.item_id, 1);
			viewHolder.itemIv3Rel.setTag(R.id.item_pos, position);
			viewHolder.itemIv3Rel.setTag(R.id.item_id, 2);
			viewHolder.itemIv1Rel.setOnClickListener(new ImageClick());
			viewHolder.itemIv2Rel.setOnClickListener(new ImageClick());
			viewHolder.itemIv3Rel.setOnClickListener(new ImageClick());
		} else if (emergencyData.objectType == 4) {// 花絮
			viewHolder.itemRetxt.setVisibility(View.GONE);
			viewHolder.itemRepic.setVisibility(View.GONE);
			viewHolder.itemRepic1.setVisibility(View.VISIBLE);
			viewHolder.itemRevideotxt.setVisibility(View.GONE);
			viewHolder.itemtitle1.setText(emergencyData.objectTitle);
			viewHolder.itembottom.setText(emergencyData.objectPiccount + "图");
			viewHolder.itembottom1.setText(emergencyData.objectTalkcount + "评");
			if (StringUtils
					.isNotEmpty(emergencyData.objectPic.get(0).objectpicpic)) {
				String picUrl = Constants.picUrlFor
						+ emergencyData.objectPic.get(0).objectpicpic + "m.jpg";
				loadImage(viewHolder.itemIv11, picUrl,
						R.drawable.emergency_txt_bg_pic);

			}
			if (StringUtils
					.isNotEmpty(emergencyData.objectPic.get(1).objectpicpic)) {
				String picUrl = Constants.picUrlFor
						+ emergencyData.objectPic.get(1).objectpicpic + "m.jpg";
				loadImage(viewHolder.itemIv12, picUrl,
						R.drawable.emergency_txt_bg_pic);

			}
			String pic2 = emergencyData.objectPic.get(0).objectpicpic;
			if (StringUtils.isNotEmpty(pic2)) {
				String picUrl = Constants.picUrlFor + pic2 + "m.jpg";
				loadImage(viewHolder.itemIv13, picUrl,
						R.drawable.emergency_txt_bg_pic);
			}
			viewHolder.itemRepic1.setTag(R.id.item_pos, position);
			viewHolder.itemRepic1.setOnClickListener(new ImageClick());
		}
		return convertView;

	}

	class ViewHolder {
		// 新闻
		public RelativeLayout itemRetxt;// 普通
		public ImageView itemIv;
		public TextView itemTxt;
		public TextView itemDetailtxt;
		// public TextView itemDetailBelowtxt;
		public TextView itemCommentxt;
		// 视频
		public RelativeLayout itemRevideotxt;// 普通
		public ImageView itemvideoIv;
		public TextView itemvideoTxt;
		public TextView itemvideoDetailtxt;
		// public TextView itemDetailBelowtxt;
		public TextView itemvideoCommentxt;

		// 明星
		public RelativeLayout itemRepic;// 明星剧照
		public ImageView itemIv1;
		public ImageView itemIv2;
		public ImageView itemIv3;
		public RelativeLayout itemIv1Rel;
		public RelativeLayout itemIv2Rel;
		public RelativeLayout itemIv3Rel;
		// 花絮
		public RelativeLayout itemRepic1;// 花絮
		public ImageView itemIv11;
		public ImageView itemIv12;
		public ImageView itemIv13;
		public TextView itemtitle1;
		public TextView itembottom;
		public TextView itembottom1;
	}

	class ImageClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = null;
			int position = (Integer) v.getTag(R.id.item_pos);
			switch (v.getId()) {
			case R.id.wangyi_item_retxt:
			case R.id.wangyi_item_revideo:
				intent = new Intent(context, EmergencyDetailActivity.class);
				intent.putExtra("objectId", getItem(position).objectId);
				intent.putExtra("type", getItem(position).objectType);
				intent.putExtra("zoneId", UserNow.current().zoneId);
				intent.putExtra("way", 1);
				context.startActivity(intent);
				break;
			case R.id.wangyi_item_repic1:
				intent = new Intent(context, ShowImageActivity.class);
				intent.putExtra("objectId", getItem(position).objectId);
				intent.putExtra("type", getItem(position).objectType);
				intent.putExtra("zoneId", UserNow.current().zoneId);
				intent.putExtra("way", 1);
				context.startActivity(intent);
				break;
			default:
				intent = new Intent(context, StarDetailActivity.class);
				int imgId = (Integer) v.getTag(R.id.item_id);
				int starId = Integer.parseInt(getItem(position).objectPic
						.get(imgId).objectpicid);
				intent.putExtra("starId", starId);
				context.startActivity(intent);
				break;
			}

		}
	}
}
