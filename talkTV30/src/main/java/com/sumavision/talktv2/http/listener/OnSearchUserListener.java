package com.sumavision.talktv2.http.listener;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.User;

public interface OnSearchUserListener {

	public void getSearchUserList(int errCode, int count,ArrayList<User> userList);
}
