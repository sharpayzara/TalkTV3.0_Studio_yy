package com.sumavision.talktv2.http.listener.interactive;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.interactive.InteractiveGuess;

/**
 * 竞猜列表接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnInteractiveGuessListListener {

	/**
	 * 
	 * @param errCode
	 * @param guessingList
	 *            常规正在进行竞猜列表
	 */
	public void onGetGuessingList(int errCode,
			ArrayList<InteractiveGuess> guessingList);
}
