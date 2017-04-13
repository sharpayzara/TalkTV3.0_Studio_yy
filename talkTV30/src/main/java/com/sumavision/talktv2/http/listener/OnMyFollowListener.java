package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.User;

public interface OnMyFollowListener {

	public void getMyFollow(int errCode, int totalGuazhuCount,
			ArrayList<User> guanzhuList);
}
