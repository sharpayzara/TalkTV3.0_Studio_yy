package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.activity.WebBrowserActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.EmergencyStoryActivity;
import com.sumavision.talktv2.activity.ExchangeLimitActivity;
import com.sumavision.talktv2.activity.SpecialActivity;
import com.sumavision.talktv2.bean.EmotionData;
import com.sumavision.talktv2.bean.MailData;
import com.sumavision.talktv2.bean.MakeEmotionsList;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.StringUtils;
import com.sumavision.talktv2.utils.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 与好友私信列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class MailWithOtherAdapter extends IBaseAdapter<MailData> {

	public MailWithOtherAdapter(Context context, String otherUserIconURL,
			List<MailData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String imageUrl = null;
		String picUrl = null;
		RelativeLayout receiveContentLayout = null;
		if (getItem(position).isFromSelf) {
			convertView = inflater.inflate(R.layout.item_message_send, null);
		} else {
			if (TextUtils.isEmpty(getItem(position).pic)) {
				convertView = inflater.inflate(R.layout.item_message_receive,
						null);
			} else {
				convertView = inflater.inflate(R.layout.item_msg_pic, null);
			}
		}
		ImageView imageView = ViewHolder.get(convertView, R.id.user_header);
		ImageView vipMark = ViewHolder.get(convertView,R.id.user_vip_mark);
		TextView txt = ViewHolder.get(convertView, R.id.content);
		TextView time = ViewHolder.get(convertView, R.id.time);
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		receiveContentLayout = ViewHolder.get(convertView, R.id.content_layout);

		if (time != null) {
			time.setText(getItem(position).timeStemp);
		}
		txt.setText(getItem(position).content);
		text2Emotion(txt);
		final MailData imageAndText = getItem(position);

		if (receiveContentLayout != null && imageAndText.type != 1) {
			receiveContentLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onReceiveClick(imageAndText);
				}
			});
		}

		if (imageView != null) {
			if (getItem(position).isFromSelf) {
				imageUrl = UserNow.current().iconURL;
			} else {
				imageUrl = getItem(position).sUserPhoto;
			}
			imageView.setTag(imageUrl);
			loadImage(imageView, imageUrl, R.drawable.list_headpic_default);
		}
		if (vipMark != null){
			if (getItem(position).isVip){
				vipMark.setVisibility(View.VISIBLE);
			} else {
				vipMark.setVisibility(View.GONE);
			}
		}
		picUrl = imageAndText.pic;
		if (StringUtils.isNotEmpty(picUrl)) {
			pic.setVisibility(View.VISIBLE);
			pic.setTag(picUrl);
			loadImage(pic, picUrl, R.drawable.fen_status_pic_default);
		}
		return convertView;
	}

	private void text2Emotion(TextView txt) {
		String text = txt.getText().toString();
		SpannableString spannable = new SpannableString(text);
		int start = 0;
		int t = 0;
		ImageSpan span;
		Drawable drawable;
		List<EmotionData> le = MakeEmotionsList.current().getLe();
		for (int i = 0; i < le.size(); i++) {

			int l = le.get(i).getPhrase().length();
			for (start = 0; (start + l) <= text.length(); start += l) {

				t = text.indexOf(le.get(i).getPhrase(), start);
				if (t != -1) {

					drawable = context.getResources().getDrawable(
							le.get(i).getId());
					drawable.setBounds(5, 5, drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight());
					span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

					spannable.setSpan(span, t, t + l,
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

				}
			}
		}
		txt.setText(spannable);
	}

	private void onReceiveClick(MailData mail) {
		Intent intent = new Intent();
		switch (mail.type) {
		case MailData.TYPE_WEB:
			intent.setClass(context, WebBrowserActivity.class);
			intent.putExtra("url", mail.url);
			intent.putExtra("title", mail.url);
			intent.putExtra("where",5);
			context.startActivity(intent);
			break;
		case MailData.TYPE_PROGRAM:
			intent.setClass(context, PlayerActivity.class);
			intent.putExtra("id", mail.otherId);
			intent.putExtra("isHalf", true);
			context.startActivity(intent);
			break;
		case MailData.TYPE_PLAY:
			if (TextUtils.isEmpty(mail.playVideopath)
					&& TextUtils.isEmpty(mail.highPath)
					&& TextUtils.isEmpty(mail.superPath)) {
				intent.putExtra("url", mail.url);
				intent.putExtra(PlayerActivity.TAG_INTENT_PLAYTYPE,
						PlayerActivity.VOD_PLAY);
				intent.setClass(context, WebAvoidActivity.class);
			} else {
				String path = mail.playVideopath;
				if (TextUtils.isEmpty(path)) {
					path = mail.highPath;
					if (TextUtils.isEmpty(path)) {
						path = mail.superPath;
					}
				}
				if (!TextUtils.isEmpty(path)) {
					intent.setClass(context, WebAvoidPicActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("playType", PlayerActivity.VOD_PLAY);
				}
			}
			context.startActivity(intent);
			break;
		case MailData.TYPE_LIVE_PLAY:
			intent.setClass(context, WebAvoidPicActivity.class);
			intent.putExtra("path", mail.playVideopath);
			intent.putExtra("channelId",(int)mail.otherId);
			intent.putExtra("title","电视直播");
			intent.putExtra("playType", 1);
			ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
			NetPlayData temp = new NetPlayData();
			temp.videoPath = mail.playVideopath;
			temp.url = mail.playVideopath;
			playList.add(temp);
			intent.putExtra("NetPlayData", playList);
			context.startActivity(intent);
			break;
		case MailData.TYPE_ACTIVITY:
			if (mail.otherId > 0) {
				intent.setClass(context, ActivityActivity.class);
				intent.putExtra("activityId", mail.otherId);
				context.startActivity(intent);
			}
			break;
		case MailData.TYPE_SPECIAL:
			intent.setClass(context, SpecialActivity.class);
			if (mail.specialType == MailData.TYPE_SPECIAL_PROGRAM) {
				intent.putExtra("isSub", false);
			} else {
				intent.putExtra("isSub", true);
			}
			intent.putExtra("columnId", (int) mail.otherId);
			intent.putExtra("pic", mail.pic);
			context.startActivity(intent);
			// FIXME
			break;
		case MailData.TYPE_ZONE:
			intent.setClass(context, EmergencyStoryActivity.class);
			intent.putExtra("zoneId", mail.otherId);
			context.startActivity(intent);
			break;
		case MailData.TYPE_USHOW:
//			new UshowManager(context).launchHall();
//			context.startActivity(new Intent(context,FanxingActivity.class));
//			AdStatisticsUtil.adCount(context, 3);
			break;
		case MailData.TYPE_EXHCNAGE_GOODS:
			intent.setClass(context, ExchangeLimitActivity.class);
			intent.putExtra("hotGoodsId", mail.otherId);
			context.startActivity(intent);
			break;

		default:
			break;
		}
	}
}
