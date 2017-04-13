package com.sumavision.talktv2.http.listener.interactive;

import java.util.ArrayList;

import com.sumavision.talktv2.bean.interactive.InteractiveCyclopedia;

/**
 * 百科返回接口
 */
public interface OnInteractiveCyclopediaListener {
	/**
	 * 
	 * @param errCode
	 * @param keyWordsList
	 */
	public void cyclopediaKeywords(int errCode,
			ArrayList<InteractiveCyclopedia> keyWordsList);

}
