package com.sumavision.talktv2.activity.help;

import android.app.Activity;
import android.view.View;

import com.sumavision.talktv2.R;
import com.taobao.munion.controller.interstitial.AdBaseInfo;
import com.taobao.munion.view.banner.MunionBannerView;
import com.taobao.munion.view.interstitial.MunionInterstitialView;
import com.taobao.munion.view.interstitial.MunionInterstitialView.InterstitialState;
import com.taobao.munion.view.interstitial.MunionInterstitialView.OnStateChangeCallBackListener;
import com.taobao.newxp.common.AlimmContext;
import com.taobao.newxp.common.ExchangeConstants;
import com.taobao.newxp.controller.ExchangeDataService;
import com.taobao.newxp.controller.XpListenersCenter.EntryOnClickListener;
import com.taobao.newxp.view.ExchangeViewManager;
import com.taobao.newxp.view.feed.Feed;
import com.taobao.newxp.view.feed.FeedViewFactory;
import com.taobao.newxp.view.feed.FeedsManager;
import com.umeng.analytics.MobclickAgent;

/**
 * 阿里妈妈接入
 * 
 * @author suma-hpb
 * 
 */
public class AlimamaHelper {
	private static final String BANNER_ID = "60911";
	private static final String INSERT_ID = "60914";
	private static final String TAOBAO_WALL_ID = "64004";
	private static final String SLOT_ID = "63864";
	Activity mActivity;
	View root;
	public FeedsManager feedsManager;
	public Feed feed;

	public AlimamaHelper(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public AlimamaHelper(View root) {
		this.root = root;
	}

	private MunionBannerView bannerView;

	public void initBannerView() {
		if (mActivity != null) {
			bannerView = (MunionBannerView) mActivity
					.findViewById(R.id.bannerView);
		} else {
			bannerView = (MunionBannerView) root.findViewById(R.id.bannerView);
		}
		bannerView.setMunionId(BANNER_ID);
	}

	public void cloaeBannerView() {
		bannerView.close();
	}

	private MunionInterstitialView videoInterstitialView;
	private OnvideoInterstitialListener onvideoInterstitialListener;

	public void setOnvideoInterstitialListener(
			OnvideoInterstitialListener onvideoInterstitialListener) {
		this.onvideoInterstitialListener = onvideoInterstitialListener;
	}

	/**
	 * 横屏广告回调
	 * 
	 * @author suma-hpb
	 * 
	 */
	public interface OnvideoInterstitialListener {
		/**
		 * 点击关闭按钮
		 */
		public void onClickClose();

		/**
		 * 图片加载成功
		 */
		public void onReady();
	}

	public void initInterstitialView() {
		if (root != null) {
			videoInterstitialView = (MunionInterstitialView) root
					.findViewById(R.id.videoInterstitialView);
		} else {
			videoInterstitialView = (MunionInterstitialView) mActivity
					.findViewById(R.id.videoInterstitialView);
		}
		videoInterstitialView
				.setOnStateChangeCallBackListener(new OnStateChangeCallBackListener() {
					@Override
					public void onStateChanged(InterstitialState state) {
						switch (state) {
						case CLOSE:
							if (onvideoInterstitialListener != null) {
								onvideoInterstitialListener.onClickClose();
							}
							break;
						case READY:
							if (onvideoInterstitialListener != null) {
								onvideoInterstitialListener.onReady();
							}
						case ERROR:
							break;
						default:
							break;
						}
					}
				});
	}

	public void loadInterstitialView() {
		AdBaseInfo adInfo = AdBaseInfo.getInsVideoInstance(INSERT_ID, "爸爸去哪儿",
				"100", "综艺节目");
		videoInterstitialView.load(adInfo);
	}

	public void closeInterstitialView() {
		videoInterstitialView.close();
	}

	public boolean onBackPressed() {
		boolean interrupt = false;
		if (bannerView != null) {
			interrupt = bannerView.onBackPressed();
		}
		return interrupt;
	}

	private ExchangeViewManager exchangeViewManager;

	public void addTaobaoWall(View view) {
		final ExchangeDataService dateService = new ExchangeDataService(
				TAOBAO_WALL_ID);
		exchangeViewManager = new ExchangeViewManager(mActivity, dateService);
		exchangeViewManager.addView(ExchangeConstants.type_list_curtain, view);
		exchangeViewManager.setEntryOnClickListener(new EntryOnClickListener() {

			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(mActivity, "guanggaoqiang");
			}
		});
	}
	
	public void initFeed() {
		AlimmContext.getAliContext().init(mActivity);
		feedsManager = new FeedsManager(mActivity);
		feedsManager.addMaterial(SLOT_ID, SLOT_ID);
		feedsManager.incubate();
	}
	
	public Feed getFeed() {
		if (feedsManager != null) {
			return feedsManager.getProduct(SLOT_ID);
		} else {
			return null;
		}
	}
	
	public View getFeedView(Feed feed) {
		try {
			if (feed != null) {
				feed.cleanReportFlag();
				FeedViewFactory.context = mActivity.getApplicationContext();
				View feedView = FeedViewFactory.getFeedView(mActivity, feed);
				return feedView;
			}
		} catch (Exception e) {
		}
		return null;
	}
}