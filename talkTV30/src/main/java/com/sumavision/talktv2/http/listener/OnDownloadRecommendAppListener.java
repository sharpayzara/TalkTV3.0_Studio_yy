package com.sumavision.talktv2.http.listener;

/**
 * 推荐软件下载ui接口
 * 
 * @author suma-hpb
 * 
 */
public interface OnDownloadRecommendAppListener {

	/**
	 * 推荐软件下载结果
	 * 
	 * @param errCode
	 */
	public void OnDownloadRecommendApp(int errCode, int totalPoint);
}
