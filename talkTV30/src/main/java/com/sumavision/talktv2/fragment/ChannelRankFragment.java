package com.sumavision.talktv2.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ChannelDetailActivity;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.ChannelRankAdapter;
import com.sumavision.talktv2.bean.ChannelType;
import com.sumavision.talktv2.bean.ShortChannelData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.umeng.analytics.MobclickAgent;

/**
 * 收视率fragment
 * 
 * @author suma-hpb
 * @version
 * @description
 */
@SuppressLint("ValidFragment")
public class ChannelRankFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener {
	private ChannelType channelType;

	public ChannelRankFragment(ChannelType channelType) {
		this.channelType = channelType;
		Bundle mBundle = new Bundle();
		mBundle.putInt("resId", R.layout.fragment_channel_ranking);
		setArguments(mBundle);
	}

	private ListView listView;

	private ChannelRankAdapter rankAdapter;

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ChannelRankFragment");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ChannelRankFragment");
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		listView = (ListView) view.findViewById(R.id.listView);
		rankAdapter = new ChannelRankAdapter(this, channelType.channelList);
		listView.setAdapter(rankAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.pic) {
			int pos = (Integer) v.getTag();
			openChannel(rankAdapter.getItem(pos));
		}

	}

	private void openChannel(ShortChannelData shortChannelData) {
		Intent i = new Intent(mActivity, ChannelDetailActivity.class);
		i.putExtra("tvName", shortChannelData.channelName);
		i.putExtra("channelId", shortChannelData.channelId);
		i.putExtra("pic", shortChannelData.channelPicUrl);
		startActivity(i);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ShortChannelData channelData = rankAdapter.getItem(position);
		VodProgramData vpd = new VodProgramData();
		vpd.cpId = channelData.cpId;
		vpd.id = String.valueOf(channelData.programId);
		vpd.topicId = channelData.topicId;
		vpd.name = channelData.programName;
		MobclickAgent.onEvent(mActivity, "tvpr", vpd.name);
		openProgram(vpd);
	}

	private void openProgram(VodProgramData vpd) {
		if (vpd.topicId == null
				|| vpd.topicId.equals("")
				|| Integer.parseInt(TextUtils.isEmpty(vpd.topicId) ? "-1"
						: vpd.topicId) == 0) {
			Toast.makeText(mActivity, "暂无此节目相关信息", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(mActivity, PlayerActivity.class);
			long programId = Long.valueOf(vpd.id);
			long longTopicId = Long.valueOf(vpd.topicId);
			intent.putExtra("id", programId);
			intent.putExtra("topicId", longTopicId);
			intent.putExtra("cpId", vpd.cpId);
			intent.putExtra("type", vpd.ptype);
			intent.putExtra("isHalf", true);
			intent.putExtra("updateName", vpd.updateName);
			intent.putExtra("where",2);
			startActivity(intent);
		}
	}

	@Override
	public void reloadData() {
		// TODO Auto-generated method stub

	}
}
