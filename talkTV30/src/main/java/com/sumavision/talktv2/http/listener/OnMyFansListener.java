package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.User;

public interface OnMyFansListener {

	public void getMyFans(int errCode, ArrayList<User> userList);
}
