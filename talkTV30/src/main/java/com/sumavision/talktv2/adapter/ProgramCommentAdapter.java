package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.bean.CommentData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.ReplyListRequest;
import com.sumavision.talktv2.http.json.ReplyParser;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 */
public class ProgramCommentAdapter extends IBaseAdapter<CommentData> {
	
	private Context context;
	ProgramDetailHalfActivity fragment;

	public ProgramCommentAdapter(Context context, List<CommentData> objects, ProgramDetailHalfActivity fragment) {
		super(context, objects);
		this.context = context;
		this.fragment = fragment;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.programhalf_comment_item, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.user_header);
		ImageView vipMark = ViewHolder.get(convertView, R.id.user_vip_mark);
		TextView username = ViewHolder.get(convertView, R.id.username);
		TextView time = ViewHolder.get(convertView, R.id.time);
		Button forward = ViewHolder.get(convertView, R.id.forward);
		Button reply = ViewHolder.get(convertView, R.id.reply);
		Button more = ViewHolder.get(convertView, R.id.more);
		TextView replyNumber = ViewHolder.get(convertView, R.id.reply_number);
		TextView content = ViewHolder.get(convertView, R.id.comment_content);
		
		final LinearLayout replyLayout = ViewHolder.get(convertView, R.id.reply_layout);
		TextView forwardText = ViewHolder.get(convertView, R.id.forward_layout);;
		final TextView replyText = ViewHolder.get(convertView, R.id.reply_text);
		
		final CommentData comment = getItem(position);
		loadImage(pic, comment.pic, R.drawable.list_headpic_default);
		if (comment.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		username.setText(comment.userName);
		time.setText(comment.time);
		if (comment.reply.size() > 0) {
			replyNumber.setText(comment.replyCount + "");
			replyLayout.setVisibility(View.VISIBLE);
			if (comment.reply.size() == comment.replyCount || comment.reply.size() < 5) {
				more.setVisibility(View.GONE);
			} else {
				more.setVisibility(View.VISIBLE);
			}
		} else {
			replyNumber.setText("");
			replyLayout.setVisibility(View.GONE);
		}
		if (comment.forward.size() > 0) {
			forwardText.setVisibility(View.VISIBLE);
		} else {
			forwardText.setVisibility(View.GONE);
		}
		content.setText(comment.content);
		
		String replyStr = "";
		for (int i = 0; i < comment.reply.size(); i++) {
			replyStr += (comment.reply.get(i).userName + ":" + comment.reply.get(i).content+"\n");
		}
		if(replyStr.endsWith("\n")&&replyStr.length()>2){
			replyStr = replyStr.substring(0, replyStr.length()-1);
		}
		replyText.setText(replyStr);
		
		String forwardStr = "";
		for (int i = 0; i < comment.forward.size(); i++) {
			forwardStr += (comment.forward.get(i).userName + ":" + comment.forward.get(i).content+"\n");
		}
		if(forwardStr.endsWith("\n")&&forwardStr.length()>2){
			forwardStr = forwardStr.substring(0, forwardStr.length()-1);
		}
		forwardText.setText(forwardStr);
		
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentData.current().forwardId = comment.talkId;
				CommentData.current().rootId = (comment.rootId == 0 ? comment.talkId:comment.rootId);
				fragment.commentType = ProgramDetailHalfActivity.COMMENT_FORWARD;
				fragment.commentInput.requestFocus();
				fragment.commentInput.setHint("转发");
				fragment.showCommentInput(fragment.commentInput);
			}
		});
		reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentData.current().userId = comment.userId;
				CommentData.current().talkId = comment.talkId;
				fragment.commentType = ProgramDetailHalfActivity.COMMENT_COMMENT;
				fragment.commentInput.requestFocus();
				fragment.commentInput.setHint("回复");
				fragment.showCommentInput(fragment.commentInput);
			}
		});
		more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				CommentData.current().talkId = comment.talkId;
//				getReply(comment.reply.size(), 10, replyText, v);
				getReply(comment,comment.reply.size(), 10, replyText, v);
				v.setClickable(false);
			}
		});
		return convertView;
	}
	
	ReplyParser rparser = new ReplyParser();
	private void getReply(final CommentData comment,int first, final int count, final TextView view,final View v) {
		VolleyHelper.post(new ReplyListRequest(first, count).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				v.setClickable(true);
				if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					if(comment.reply.size()<comment.replyCount){
						comment.reply.addAll(rparser.commentList);
						notifyDataSetChanged();
					}else{
						v.setVisibility(View.GONE);
					}
//					String replyStr = "\n";
//					for (int i = 0; i < rparser.commentList.size(); i++) {
//						replyStr += (rparser.commentList.get(i).userName  + ":" + rparser.commentList.get(i).content+"\n");
//					}
//					if(replyStr.endsWith("\n")&&replyStr.length()>2){
//						replyStr = replyStr.substring(0, replyStr.length()-2);
//					}
//					view.append(replyStr);
					if(rparser.commentList.size()<count){
						v.setVisibility(View.GONE);
					}
//					replyLayout.removeAllViews();
//					for (int i = 0; i < rparser.commentList.size(); i++) {
//						replyLayout.addView(getReplyView(rparser.commentList.get(i).userName  + ":" + rparser.commentList.get(i).content));
//					}
				}
			}
		}, null);
	}

}
