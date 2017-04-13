package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

public class PlayHistoryAdapter extends IBaseAdapter<NetPlayData> {
	
	private Context mContext;

	public PlayHistoryAdapter(Context context, List<NetPlayData> objects) {
		super(context, objects);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_play_history, null);
		}
		TextView nameTxt = ViewHolder.get(convertView, R.id.name);
		TextView introTxt = ViewHolder.get(convertView, R.id.intro);
		ImageView cacheView = ViewHolder.get(convertView, R.id.imgv_cache);
		ImageView info = ViewHolder.get(convertView, R.id.infoBtn);
		info.setVisibility(View.GONE);
		final NetPlayData data = getItem(position);
		String name = data.name;
		if (name != null) {
			nameTxt.setText(name);
		}
		String intro = data.intro;
		if (data.dbposition == -1) {
			intro = "观看至结束";
		} else {
			int hour = data.dbposition / 3600;
			int min = (int) (data.dbposition - (hour * 3600)) / 60;
			int sec = data.dbposition - (hour * 3600) - min * 60;
			StringBuffer times = new StringBuffer("观看至");
			if (hour < 10) {
				times.append("0").append(hour).append(":");
			} else {
				times.append(hour).append(":");
			}
			if (min < 10) {
				times.append("0").append(min).append(":");
			} else {
				times.append(min).append(":");
			}
			if (sec < 10) {
				times.append("0").append(sec);
			} else {
				times.append(+sec);
			}
			intro = times.toString();
		}
		introTxt.setText(intro);
		if (!TextUtils.isEmpty(data.isdownload) && data.isdownload.equals("1")) {
			cacheView.setVisibility(View.VISIBLE);
		} else {
			cacheView.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	private void openProgramActivity(NetPlayData program) {
		String programId = String.valueOf(program.id);
		String topicId = program.topicId;
		openProgramDetailActivity(programId, topicId,
				program.name, 0);
	}
	
	public void openProgramDetailActivity(String id, String topicId,
			String updateName, long cpId) {
		Intent intent = new Intent(mContext, PlayerActivity.class);
		long programId = Long.valueOf(id);
		long longTopicId = 0;
		try {
			longTopicId = Long.valueOf(topicId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		intent.putExtra("isHalf", true);
		intent.putExtra("id", programId);
		intent.putExtra("topicId", longTopicId);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
		intent.putExtra("where",5);

		mContext.startActivity(intent);
	}
}
