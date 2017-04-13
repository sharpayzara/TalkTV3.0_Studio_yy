package com.sumavision.talktv2.http.listener.interactive;

import com.sumavision.talktv2.bean.interactive.GuessResult;

/**
 * 参与竞猜返回接口
 */
public interface OnInteractiveGuessJoinListener {
	/**
	 * 参与竞猜回调
	 * 
	 * @param errCode
	 * @param guessResult
	 */
	public void JoinGuessResult(int errCode, GuessResult guessResult);

}
