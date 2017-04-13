package com.sumavision.talktv2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.CommentActivity;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.fragment.CommentFragment;
import com.sumavision.talktv2.utils.Txt2Image;

/**
 * 评论列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CommentAdapter extends IBaseAdapter<CommentData> {
	
	private CommentFragment fragment;
	boolean isVip;
	public CommentAdapter(Context context, List<CommentData> objects) {
		super(context, objects);
		comments = (ArrayList<CommentData>) objects;
	}

	private ArrayList<CommentData> comments;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater
					.inflate(R.layout.item_comment, null);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.name);
			viewHolder.headpicImageView = (ImageView) convertView
					.findViewById(R.id.user_header);
			viewHolder.vipMark = (ImageView) convertView.findViewById(R.id.user_vip_mark);
			viewHolder.contentTextView = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.picImageView = (ImageView) convertView
					.findViewById(R.id.pic);
			viewHolder.rootLayout = (RelativeLayout) convertView
					.findViewById(R.id.root_layout);
			viewHolder.rootTextView = (TextView) convertView
					.findViewById(R.id.root_content);
			viewHolder.rootPicImageView = (ImageView) convertView
					.findViewById(R.id.root_pic);
			viewHolder.replayCountView = (TextView) convertView
					.findViewById(R.id.reply);
			viewHolder.zhuanfaCountView = (TextView) convertView
					.findViewById(R.id.zhuanfa);
			viewHolder.replyLayout = (RelativeLayout) convertView
					.findViewById(R.id.llayout_reply);
			viewHolder.forwardlayout = (RelativeLayout) convertView
					.findViewById(R.id.llayout_forward);
			viewHolder.from = (TextView) convertView.findViewById(R.id.from);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);

			// 语音评论
			viewHolder.audioFrame = (RelativeLayout) convertView
					.findViewById(R.id.comment_audio_btn);
			viewHolder.audioBtn = (ImageView) convertView
					.findViewById(R.id.comment_item_content_audio_pic);
			viewHolder.audioPb = (ProgressBar) convertView
					.findViewById(R.id.comment_item_progressBar);

			// 语音评论
			viewHolder.rootAudioFrame = (RelativeLayout) convertView
					.findViewById(R.id.comment_audio_btn_root);
			viewHolder.rootAudioBtn = (ImageView) convertView
					.findViewById(R.id.comment_item_content_audio_pic_root);
			viewHolder.rootAudioPb = (ProgressBar) convertView
					.findViewById(R.id.comment_item_progressBar_root);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.forwardlayout.setClickable(true);
		viewHolder.replyLayout.setClickable(true);
		viewHolder.forwardlayout.setTag(position);
		viewHolder.replyLayout.setTag(position);
		viewHolder.forwardlayout.setOnClickListener((OnClickListener)fragment);
		viewHolder.replyLayout.setOnClickListener((OnClickListener)fragment);

		final CommentData temp = comments.get(position);
		String userHeadPic = temp.userURL;
		if (userHeadPic != null) {
			loadImage(viewHolder.headpicImageView, userHeadPic,
					R.drawable.list_headpic_default);
		}
		if (temp.isVip){
			viewHolder.vipMark.setVisibility(View.VISIBLE);
		} else {
			viewHolder.vipMark.setVisibility(View.GONE);
		}
		String userName = temp.userName;
		if (userName != null) {
			viewHolder.nameTextView.setText(userName);
		}

		String fromString = temp.source;
		if (fromString != null) {
			viewHolder.from.setText(fromString);
		}
		String time = temp.commentTime;
		if (time != null) {
			viewHolder.time.setText(time);
		}

		if (temp.replyCount > 0) {
			viewHolder.replayCountView.setVisibility(View.VISIBLE);
			String replayCount = String.valueOf(temp.replyCount);
			viewHolder.replayCountView.setText(replayCount);
		} else {
			viewHolder.replayCountView.setVisibility(View.INVISIBLE);
		}

		if (temp.forwardCount > 0) {
			viewHolder.zhuanfaCountView.setVisibility(View.VISIBLE);
			String zhuanfaCount = String.valueOf(temp.forwardCount);
			viewHolder.zhuanfaCountView.setText(zhuanfaCount);
		} else {
			viewHolder.zhuanfaCountView.setVisibility(View.INVISIBLE);
		}
		if (temp.talkType != 4) {
			viewHolder.audioFrame.setVisibility(View.GONE);
			String content = temp.content;
			if (content != null) {
				viewHolder.contentTextView.setText(content);
				SpannableString contentString = Txt2Image.text2Emotion(context,
						content);
				viewHolder.contentTextView.setText(contentString);
			}

			if (temp.talkType == 1) {
				String picUrl = temp.contentURL;
				loadImage(viewHolder.picImageView, picUrl,
						R.drawable.list_star_default);
				viewHolder.picImageView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.picImageView.setVisibility(View.GONE);
			}
		} else {
			final ProgressBar pb = viewHolder.audioPb;
			viewHolder.contentTextView.setVisibility(View.GONE);
			viewHolder.picImageView.setVisibility(View.GONE);
			viewHolder.audioFrame.setVisibility(View.VISIBLE);
			viewHolder.audioBtn.setTag(R.id.item_url, temp.audioURL);
			viewHolder.audioBtn.setTag(R.id.item_obj, pb);
			if (context instanceof CommentActivity) {
				viewHolder.audioBtn
						.setOnClickListener((OnClickListener) context);
			}
		}
		if (temp.hasRootTalk) {
			viewHolder.rootLayout.setVisibility(View.VISIBLE);
			if (temp.rootTalk.talkType != 4) {
				viewHolder.rootAudioFrame.setVisibility(View.GONE);
				String rootContent = temp.rootTalk.content;
				if (rootContent != null) {
					SpannableString contentString = Txt2Image.text2Emotion(
							context, rootContent);
					viewHolder.rootTextView.setText(contentString);
				}
				if (temp.rootTalk.talkType == 1) {
					String rootheadPicUrl = temp.rootTalk.contentURL;
					loadImage(viewHolder.rootPicImageView, rootheadPicUrl,
							R.drawable.list_headpic_default);
					viewHolder.rootPicImageView.setVisibility(View.VISIBLE);
				} else {
					viewHolder.rootPicImageView.setVisibility(View.GONE);
				}
			} else {
				final ProgressBar pb = viewHolder.rootAudioPb;
				viewHolder.rootTextView.setVisibility(View.GONE);
				viewHolder.rootPicImageView.setVisibility(View.GONE);
				viewHolder.rootAudioFrame.setVisibility(View.VISIBLE);
				viewHolder.rootAudioBtn.setTag(R.id.item_url, temp.audioURL);
				viewHolder.rootAudioBtn.setTag(R.id.item_obj, pb);
				if (context instanceof CommentActivity) {
					viewHolder.rootAudioBtn
							.setOnClickListener((OnClickListener) context);
				}
			}
		} else {
			viewHolder.rootLayout.setVisibility(View.GONE);
		}
		return convertView;
	}
	public void setVip(boolean flag){
		isVip = flag;
	}

	public CommentFragment getFragment() {
		return fragment;
	}

	public void setFragment(CommentFragment fragment) {
		this.fragment = fragment;
	}
}

class ViewHolder {
	public TextView nameTextView, contentTextView, rootTextView;
	public ImageView headpicImageView, picImageView, audioBtn,vipMark;
	public RelativeLayout rootLayout, audioFrame, replyLayout, forwardlayout,
			rootAudioFrame;
	public ImageView rootPicImageView, rootAudioBtn;
	public ProgressBar rootAudioPb, audioPb;
	public TextView replayCountView, zhuanfaCountView, from, time;
}


