package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.interactive.InteractiveActivity;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.widget.sticky.StickyListHeadersAdapter;

/**
 * 互动活动列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class InteractiveActivityAdapter extends
		IBaseAdapter<InteractiveActivity> implements StickyListHeadersAdapter {

	public InteractiveActivityAdapter(Context context,
			List<InteractiveActivity> objects) {
		super(context, objects);
	}

	Resources mResources;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (mResources == null) {
			mResources = getContext().getResources();
		}
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_guess_sports, null);
			holder.titleText = (TextView) convertView.findViewById(R.id.title);
			holder.leftTeamLogo = (ImageView) convertView
					.findViewById(R.id.left_team_logo);
			holder.leftTeamNameText = (TextView) convertView
					.findViewById(R.id.left_team_name);
			holder.rightTeamLogo = (ImageView) convertView
					.findViewById(R.id.right_team_logo);
			holder.rightTeamNameText = (TextView) convertView
					.findViewById(R.id.right_team_name);
			holder.interactCountText = (TextView) convertView
					.findViewById(R.id.interact_count);
			holder.interactStatusText = (TextView) convertView
					.findViewById(R.id.interact_status);
			holder.topFlag = (ImageView) convertView.findViewById(R.id.top);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		InteractiveActivity guess = getItem(position);
		if (guess.isTop) {
			holder.topFlag.setVisibility(View.VISIBLE);
		} else {
			holder.topFlag.setVisibility(View.GONE);
		}
		holder.titleText.setText(guess.title);
		holder.interactCountText.setText(mResources.getString(
				R.string.join_count, guess.personCount));
		switch (guess.interactStatus) {
		case InteractiveActivity.INTERACTION_STATUS_NO:
			holder.interactStatusText.setText(R.string.no_start);
			holder.interactStatusText.setTextColor(mResources
					.getColor(R.color.guess_team_name));
			break;
		case InteractiveActivity.INTERACTION_STATUS_ROOM:
			holder.interactStatusText.setText(R.string.go_to_room);
			holder.interactStatusText.setTextColor(mResources
					.getColor(R.color.navigator_bg_color));
			break;
		case InteractiveActivity.INTERACTION_STATUS_INTIME:
			holder.interactStatusText
					.setBackgroundResource(R.drawable.guess_intime_bg);
			holder.interactStatusText.setText(R.string.intime_interact);
			holder.interactStatusText.setTextColor(mResources
					.getColor(R.color.white));
			break;
		}
		holder.leftTeamNameText.setText(guess.leftTeamName);
		holder.rightTeamNameText.setText(guess.rightTeamName);
		loadImage(holder.leftTeamLogo, Constants.picUrlFor + guess.leftTeamLogo
				+ Constants.PIC_SMALL, R.drawable.aadefault);
		loadImage(holder.rightTeamLogo, Constants.picUrlFor
				+ guess.rightTeamLogo + Constants.PIC_SMALL,
				R.drawable.aadefault);
		return convertView;
	}

	static class ViewHolder {
		public TextView titleText;

		public TextView leftTeamNameText;

		public TextView rightTeamNameText;

		public TextView interactCountText;

		public TextView interactStatusText;

		public ImageView topFlag, leftTeamLogo, rightTeamLogo;
	}

	static class HeadViewHolder {
		public TextView timeText;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeadViewHolder holder = null;
		if (mResources == null) {
			mResources = getContext().getResources();
		}
		if (convertView == null) {
			holder = new HeadViewHolder();
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_guess_head, parent,
					false);
			holder.timeText = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (HeadViewHolder) convertView.getTag();
		}
		InteractiveActivity guess = getItem(position);
		String show = guess.showTime;
		if (show != null) {
			if (show.contains("null")) {
				show = show.replace("null", "");
			}
			holder.timeText.setText(show);
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return getItem(position).id;
	}
}
