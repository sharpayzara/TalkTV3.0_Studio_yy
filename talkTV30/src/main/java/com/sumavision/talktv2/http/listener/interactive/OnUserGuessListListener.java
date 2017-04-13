package com.sumavision.talktv2.http.listener.interactive;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.interactive.InteractiveGuess;

/**
 * 用户竞猜返回接口
 */
public interface OnUserGuessListListener {
	/**
	 * 用户竞猜列表
	 * 
	 * @param errCode
	 * @param guessList
	 */
	public void userGuessList(int errCode, ArrayList<InteractiveGuess> guessList);

}
