package com.sumavision.talktv2.http.listener.interactive;

import com.sumavision.talktv2.bean.interactive.InteractiveGuess;

/**
 * 竞猜详情返回接口
 */
public interface OnInteractiveGuessDetailListener {
	/**
	 * 
	 * @param errCode
	 * @param guess
	 */
	public void guessDetailResult(int errCode, InteractiveGuess guess);

}
