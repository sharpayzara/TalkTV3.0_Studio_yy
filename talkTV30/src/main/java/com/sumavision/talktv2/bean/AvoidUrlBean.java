package com.sumavision.talktv2.bean;

import java.util.ArrayList;

import com.sumavision.talktv2.R;

public class AvoidUrlBean {

	private static AvoidUrlBean instance;

	public static AvoidUrlBean getInstance() {
		if (instance == null) {
			instance = new AvoidUrlBean();
		}
		return instance;
	}

	private AvoidUrlBean() {
	}

	public ArrayList<NetworkLiveBean> getLiveList(int size) {
		size = size > 5 ? 5 : size;
		ArrayList<NetworkLiveBean> liveList = new ArrayList<NetworkLiveBean>();

		if (size >= 1) {
			liveList.add(new NetworkLiveBean("网络1", R.drawable.network_live1));
		}
		if (size >= 2) {
			liveList.add(new NetworkLiveBean("网络2", R.drawable.network_live2));
		}
		if (size >= 3) {
			liveList.add(new NetworkLiveBean("网络3", R.drawable.network_live3));
		}
		if (size >= 4) {
			liveList.add(new NetworkLiveBean("网络4", R.drawable.network_live4));
		}
		if (size >= 5) {
			liveList.add(new NetworkLiveBean("网络5", R.drawable.network_live4));
		}

		return liveList;
	}

	// 显示条目
	public static final int AVOID_SIGN_SIZE = 5;

	public ArrayList<NetPlayData> resultList(
			ArrayList<NetPlayData> netPlayDataList, int position) {
		ArrayList<NetPlayData> newlist = new ArrayList<NetPlayData>();// 有直播播放地址的list集合
		for (int i = 0; i < position; i++) {
			NetPlayData result = netPlayDataList.remove(0);
			if (i < position) {
				newlist.add(result);
			}
		}
		netPlayDataList.addAll(newlist);
		return netPlayDataList;
	}
}
