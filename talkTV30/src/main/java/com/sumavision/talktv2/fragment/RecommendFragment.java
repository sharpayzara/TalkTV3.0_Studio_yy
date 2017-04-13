package com.sumavision.talktv2.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kugou.fanxing.core.FanxingManager;
import com.mediav.ads.sdk.adcore.Mvad;
import com.mediav.ads.sdk.interfaces.IMvNativeAd;
import com.mediav.ads.sdk.interfaces.IMvNativeAdListener;
import com.mediav.ads.sdk.interfaces.IMvNativeAdLoader;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LibDetailActivity;
import com.sumavision.talktv2.activity.RecommandAppActivity;
import com.sumavision.talktv2.activity.RecommendActivityActivity;
import com.sumavision.talktv2.activity.ShakeActivity;
import com.sumavision.talktv2.activity.SlidingMainActivity;
import com.sumavision.talktv2.activity.SpecialRecommendColumnActivity;
import com.sumavision.talktv2.activity.help.AlimamaHelper;
import com.sumavision.talktv2.adapter.RecommendListAdapter;
import com.sumavision.talktv2.bean.AppData;
import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.RecommendCommonData;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.RecommendPageNewData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.RecommendDetailParser;
import com.sumavision.talktv2.http.json.RecommendDetailRequest;
import com.sumavision.talktv2.http.listener.OnDownloadRecommendAppListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnRecommendDetailListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.service.AppDownloadService;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.taobao.newxp.view.feed.Feed;
import com.taobao.newxp.view.feed.FeedsManager.IncubatedListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author yanzhidan
 * @createTime 2014-6-26
 * @description 首页推荐
 * 
 */
public class RecommendFragment extends FocusLayoutFragment implements
		OnClickListener, OnHttpErrorListener, OnRecommendDetailListener,
		OnRefreshListener<ListView>, OnDownloadRecommendAppListener {
	private View appsView;
	private LinearLayout appsLayout;
	private List<ColumnData> columnDatas;
	private List<List<RecommendCommonData>> recommendDatas = new ArrayList<List<RecommendCommonData>>();
	private PullToRefreshListView listView;
	private RecommendListAdapter adapter;
	private RecommendPageNewData rpd;
	private boolean needLoadData = true;
	private RelativeLayout advertLayout;
	public static Feed feed;
	View advert;
	private boolean firstLoading = true;

	@Override
	public void onStart() {
		super.onStart();
	}

	MyInstalledReceiver installedReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	boolean pageAnalytic;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (needLoadData) {
				getRecommandDetail(true);
			}
			pageAnalytic = true;
			MobclickAgent.onEvent(getActivity(), "shouye");
			MobclickAgent.onPageStart("RecommendFragment");
		} else {
			if (pageAnalytic) {
				pageAnalytic = false;
				MobclickAgent.onPageEnd("RecommendFragment");
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (pageAnalytic) {
			pageAnalytic = false;
			MobclickAgent.onPageEnd("RecommendFragment");
		}
	}

	public static RecommendFragment newInstance() {
		RecommendFragment fragment = new RecommendFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_recommend);
		fragment.setArguments(bundle);
		return fragment;
	}

	AlimamaHelper mAlimamaHelper;

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		appsView = inflater.inflate(R.layout.include_recommend_applayout, null);
		appsLayout = (LinearLayout) appsView
				.findViewById(R.id.layout_recommend_app);

		initStarsLayout();
		listView = (PullToRefreshListView) view.findViewById(R.id.listview);
		advertLayout = (RelativeLayout) view.findViewById(R.id.rlayout);
		mAlimamaHelper = new AlimamaHelper(getActivity());
		mAlimamaHelper.initFeed();
		mAlimamaHelper.feedsManager
				.setIncubatedListener(new IncubatedListener() {
					@Override
					public void onComplete(int status, Feed feed, String arg2) {
						if (adapter != null && getActivity() != null
								&& !getActivity().isFinishing()
								&& adapter.feed == null && status == 1) {
							adapter.feed = feed;
							adapter.notifyDataSetChanged();
						}
					}
				});
		advert = view.findViewById(R.id.rlayout);
		listView.getRefreshableView().addHeaderView(headerView);
		String meta = AppUtil.getMetaData(mActivity, "UMENG_CHANNEL");
		if (!meta.equals("anZhi") && !meta.equals("huawei")) {
			listView.getRefreshableView().addFooterView(appsView);
		}
		listView.getRefreshableView().setSelector(
				R.drawable.list_transe_selector);
		listView.setOnRefreshListener(this);
		listView.setMode(Mode.PULL_FROM_START);
		listView.setScrollingWhileRefreshingEnabled(true);
		listView.setPullToRefreshOverScrollEnabled(false);
		appScrollListener(listView.getRefreshableView(),
				new FabOnScrollListener());
		cache = PreferencesUtils.getString(getActivity(), null,
				Constants.RECOMMEND_CACHE_CONTENT);
		if (!TextUtils.isEmpty(cache)) {
			rparser = new RecommendDetailParser();
			try {
				rparser.parse(new JSONObject(cache));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			getRecommendDetail(rparser.errCode, rparser.recommendData);
		}
		//导航栏广告
		boolean kugouAd = PreferencesUtils.getBoolean(getActivity(), null, "kugouAd",false);
		if (kugouAd) {
			if (getActivity() instanceof SlidingMainActivity) {
				((SlidingMainActivity) getActivity()).updateFestivalLogo(1);
			}
		}
	}

	RecommendDetailParser rparser;
	String cache;

	public void getRecommandDetail(boolean tip) {
		rparser = new RecommendDetailParser();
		if (needLoadData) {
			if (tip) {
				showLoadingLayout();
			}
			VolleyHelper.post(new RecommendDetailRequest().make(),
					new ParseListener(rparser) {

						@Override
						public void onParse(BaseJsonParser parser) {
							getRecommendDetail(rparser.errCode,
									rparser.recommendData);
							
							if (rparser.errCode == 0) {
								PreferencesUtils.putLong(getActivity(), null,
										Constants.RECOMMEND_CACHE_KEY,
										System.currentTimeMillis() / 1000);
								PreferencesUtils.putString(getActivity(), null,
										Constants.RECOMMEND_CACHE_CONTENT,
										rparser.recommendDataStr);
							}
						}
					}, this);
			// VolleyProgramRequest.recommendDetail(this, this);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		refreshTimes ++ ;
		needLoadData = true;
		mAlimamaHelper.initFeed();
		getRecommandDetail(false);
	}
	public int refreshTimes = 0;

	@Override
	public void getRecommendDetail(int errCode,
			RecommendPageNewData recommendData) {
		listView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK && recommendData != null) {
			hideLoadingLayout();
			needLoadData = false;
			rpd = recommendData;
			columnDatas = rpd.getColumn();
			initData(columnDatas);
			initUi();
		} else {
			if (!(adapter != null && adapter.getCount()>0)){
				showErrorLayout();
			}
		}
		((SlidingMainActivity) getActivity()).getWelcomeListTask();
	}

	private void initUi() {
		if (mAlimamaHelper != null) {
			feed = mAlimamaHelper.getFeed();
			if (feed == null) {
				mAlimamaHelper.initFeed();
				feed = mAlimamaHelper.getFeed();
			}
		}
		adapter = new RecommendListAdapter(getActivity(), this, recommendDatas,
				feed);
		listView.setAdapter(adapter);
		initAppsLayout();
		updateStarsLayout((ArrayList<RecommendData>) rpd.getRecommend());
	}

	private void initAppsLayout() {
		List<AppData> datas = rpd.getApps();
		if (datas == null || (datas != null && datas.size() == 0)) {
			listView.getRefreshableView().removeFooterView(appsView);
			return;
		} else {
			if (datas.size() > 4) {
				datas = datas.subList(0, 4);
			}
			appsView.setVisibility(View.VISIBLE);
		}

		View appColumnView = appsView
				.findViewById(R.id.rmcd_main_list_item_top_);
		appColumnView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
					startActivity(new Intent(mActivity,
							RecommandAppActivity.class));
			}
		});
		ImageView iv = (ImageView) appsView
				.findViewById(R.id.rmcd_main_list_item_top_pic);
		TextView tv = (TextView) appsView
				.findViewById(R.id.rmcd_main_list_item_top_name);
		iv.setImageResource(R.drawable.ic_rcmd_app);
		tv.setText(R.string.recommend_apps);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LayoutParams params = (LayoutParams) appsLayout.getLayoutParams();
		params.weight = 1;
		appsLayout.removeAllViews();
		for (int i = 0; i < datas.size(); i++) {
			AppData data = datas.get(i);
			View view = inflater.inflate(R.layout.item_rcmd_app, null);
			view.setLayoutParams(params);
			ImageView appImg = (ImageView) view.findViewById(R.id.icon);
			TextView appTv = (TextView) view.findViewById(R.id.name);
			loadImageCacheDisk(appImg, data.pic, R.drawable.ic_launcher);
			appTv.setText(data.name);
			appsLayout.addView(view);
			view.setTag(data);
			view.setOnClickListener(this);
		}

	}

	private void initData(List<ColumnData> column) {
		boolean liveModule = PreferencesUtils.getBoolean(getActivity(), null, "liveModule", true);
		List<List<RecommendCommonData>> commonDatas = new ArrayList<List<RecommendCommonData>>();
		for (int i = 0; i < column.size(); i++) {
			ColumnData columnData = column.get(i);
			List<RecommendCommonData> recommendCommonDatas = columnData
					.getDatas();
			if (columnData.type == ColumnData.TYPE_LIVE
					&& !liveModule) {
				continue;
			}
			if (columnData.type == ColumnData.TYPE_ADVERT) {
				recommendCommonDatas = new ArrayList<RecommendCommonData>();
				RecommendCommonData recommendColumn = new RecommendCommonData();
				recommendColumn.setColumnData(columnData);
				recommendCommonDatas.add(recommendColumn);
				commonDatas.add(recommendCommonDatas);
				continue;
			}
			if (recommendCommonDatas != null) {
				// 数据与所要显示的类型不符则抛弃数据
				boolean case1 = columnData.picType == ColumnData.PIC_TYPE_NORMAL
						&& recommendCommonDatas.size() < 2;
				boolean case2 = (columnData.picType == ColumnData.PIC_TYPE_THREE || columnData.picType == ColumnData.PIC_TYPE_HORIZONTAL)
						&& recommendCommonDatas.size() < 3;
				boolean case3 = recommendCommonDatas.size() <= 0;
				if (case1 || case2 || case3) {
					continue;
				}
				List<RecommendCommonData> recommendColumns = new ArrayList<RecommendCommonData>();
				RecommendCommonData recommendColumn = new RecommendCommonData();
				recommendColumn.setColumnData(columnData);
				recommendColumns.add(recommendColumn);
				commonDatas.add(recommendColumns);
				int type = columnData.picType;
				if (type == ColumnData.PIC_TYPE_VERTICAL) {
					if(columnData.type == ColumnData.TYPE_SHAKE){
						if(columnData.showPic == 1){
							List<RecommendCommonData> temp = new ArrayList<RecommendCommonData>();
							RecommendCommonData tempItem = new RecommendCommonData();
							tempItem.name = "shake_shake";
							temp.add(tempItem);
							commonDatas.add(temp);
						}
						for (int k = 0; k < recommendCommonDatas.size() / 2; k++) {
							commonDatas.add(recommendCommonDatas.subList(2 * k,
									2 * (k + 1)));
						}
					}else {
						commonDatas.add(recommendCommonDatas.subList(0, 1));
						for (int k = 0; k < (recommendCommonDatas.size() - 1) / 2; k++) {
							commonDatas.add(recommendCommonDatas.subList(2 * k + 1,
									2 * (k + 1) + 1));
						}
					}
				} else if (type == ColumnData.PIC_TYPE_HORIZONTAL) {
					if (recommendCommonDatas.size() < 3)
						return;
					commonDatas.add(recommendCommonDatas.subList(0, 3));
					for (int k = 0; k < (recommendCommonDatas.size() - 3) / 2; k++) {
						commonDatas.add(recommendCommonDatas.subList(2 * k + 3,
								2 * (k + 1) + 3));
					}
				} else if (type == ColumnData.PIC_TYPE_NORMAL) {
					for (int k = 0; k < recommendCommonDatas.size() / 2; k++) {
						commonDatas.add(recommendCommonDatas.subList(2 * k,
								2 * (k + 1)));
					}
				} else if (type == ColumnData.PIC_TYPE_THREE) {
					for (int k = 0; k < recommendCommonDatas.size() / 3; k++) {
						commonDatas.add(recommendCommonDatas.subList(3 * k,
								3 * (k + 1)));
					}
				}
			}

		}
		recommendDatas.clear();
		recommendDatas.addAll(commonDatas);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.app_linear: {
			AppData data = (AppData) v.getTag();
			actionOpenApp(data.packageName, data.url, data.name, (int) data.id);
			MobclickAgent.onEvent(getActivity(), "syapp", data.name);
			break;
		}
		case R.id.imageView:
		case R.id.imageView1:
		case R.id.imageView2: {
			MobclickAgent.onEvent(getActivity(), "sydjjm");
			RecommendCommonData commonData = (RecommendCommonData) v.getTag();
			int type = commonData.columnType;
			if (type == ColumnData.TYPE_LIVE) {
				if (commonData.netPlayDatas != null
						&& commonData.netPlayDatas.size() > 0
						&& commonData.type != 3) {
					liveThroughNet(commonData.netPlayDatas, 0, commonData.id,commonData.skipToWeb,"");
				} else if (commonData.type == 3) {
					if (getActivity() instanceof SlidingMainActivity) {
						AdStatisticsUtil.adCount(getActivity(), Constants.homeLive);
						FanxingManager.goMainUi(getActivity());
					}
				}
			} else if (type == ColumnData.TYPE_ACTIVITY) {
				openActivityDetailActivity(commonData.id);
			} else if (type == ColumnData.TYPE_DIRECTLY_PLAY) {
				//311 改为全屏 不区分类型播放推荐子节目
				openProgramDetailActivity((long) commonData.tempId,commonData.id,
						commonData.name, 0,0);
				/*// 非网页源情况
				String videoPath = commonData.videoPath;
				if (!TextUtils.isEmpty(commonData.videoPathHigh)) {
					videoPath = commonData.videoPathHigh;
				}

				if (!TextUtils.isEmpty(commonData.videoPathSuper)) {
					videoPath = commonData.videoPathSuper;
				}
				if (!TextUtils.isEmpty(videoPath)) {
					Intent intent = new Intent();
					intent.putExtra(PlayerActivity.TAG_INTENT_STANDURL,
							commonData.videoPath);
					intent.putExtra(PlayerActivity.TAG_INTENT_HIGHURL,
							commonData.videoPathHigh);
					intent.putExtra(PlayerActivity.TAG_INTENT_SUPERURL,
							commonData.videoPathSuper);
					openAvoidActivity(intent,commonData.tempId, commonData.id, commonData.webUrl,
							videoPath, commonData.name);
				} else {
					if (!TextUtils.isEmpty(commonData.webUrl)) {
						openAvoidActivity(null,commonData.tempId, commonData.id,
								commonData.webUrl, null, commonData.name);
					}
				}*/
			}else if (type == ColumnData.TYPE_SHAKE){
				Intent playerIntent = new Intent(getActivity(),PlayerActivity.class);
				playerIntent.putExtra("isHalf",true);
				if (commonData.tvfanProgId>0){
					playerIntent.putExtra("id",commonData.tvfanProgId);
					playerIntent.putExtra("subid",commonData.tvfanProgSubId);
					playerIntent.putExtra("playType",PlayerActivity.VOD_PLAY);
				} else {
					playerIntent.putExtra("id",commonData.id);
					playerIntent.putExtra("playType",PlayerActivity.SHAKE_PLAY);
				}
				startActivity(playerIntent);
			}else {
				if (commonData.subject) {
					openSpecialActivity(commonData.id, commonData.pic, false,
							commonData.title, 2);
				} else {
					openProgramDetailActivity((long) commonData.id,commonData.subId,
							commonData.name, 0,commonData.pType);
				}
			}
			break;
		}
			case R.id.rmcd_main_list_item_top_arrow_tips:
			case R.id.rmcd_main_list_item_top_arrow_tips1:
			case R.id.rmcd_main_list_item_top_arrow_tips2:
				if (v.getId() == R.id.rmcd_main_list_item_top_arrow_tips){
					tagPos = 0;
				}else if (v.getId() ==  R.id.rmcd_main_list_item_top_arrow_tips1){
					tagPos = 1;
				}else if (v.getId() ==  R.id.rmcd_main_list_item_top_arrow_tips2){
					tagPos = 2;
				}
		case R.id.rmcd_main_list_item_top_: {
			ColumnData data = (ColumnData) v
					.getTag(R.id.tag_stickyheader_content);
			if (null == data) {
				tagPos = -1;
				return;
			}
			MobclickAgent.onEvent(getActivity(), "sydjgd", data.name);
			if (data.type == ColumnData.TYPE_LIVE) {
				if (mActivity instanceof SlidingMainActivity) {
					((SlidingMainActivity) mActivity).changePage(1);
					//根据标签跳转直播频道
					if (tagPos>-1 && tagPos<3){
						EventMessage liveMessage = new EventMessage("TvLiveFragment");
						liveMessage.bundle.putSerializable("tag",data.recommendTags.get(tagPos));
						EventBus.getDefault().post(liveMessage);
					}
				}
			} else if (data.type == ColumnData.TYPE_ACTIVITY) {
				mActivity.startActivity(new Intent(mActivity,
						RecommendActivityActivity.class));
			} else if (data.type == ColumnData.TYPE_APPLICATION) {
				actionOpenApp(data.identifyName, data.downloadUrl,
						data.appName, (int) data.id);
			} else if (data.type == ColumnData.TYPE_SHAKE) {
				getActivity().startActivity(new Intent(getActivity(), ShakeActivity.class));
			} else if (data.getVault() != null && data.getVault().size() > 0) {
				ArrayList<HotLibType> list = data.getVault();
				if (data.type != HotLibType.TYPE_USHOW && data.type != 3) {
					Intent intent = new Intent(getActivity(),
							LibDetailActivity.class);
					intent.putParcelableArrayListExtra("libType", list);
                    if (data.parentId>0 && data.contentTypeId>0){
                        intent.putExtra("id", data.parentId);
                        intent.putExtra("tagId",data.contentTypeId);
                    } else if(data.parentId>0){
                        intent.putExtra("id", data.parentId);
                    }else {
                        intent.putExtra("id", (long) data.id);
                    }
					if (tagPos>-1 && tagPos<3){
						intent.putExtra("tagId",(int)data.recommendTags.get(tagPos).id);
					}
					startActivity(intent);
				}else {
					if (getActivity() instanceof SlidingMainActivity) {
//						startActivity(new Intent(getActivity(),FanxingActivity.class));
//						AdStatisticsUtil.adCount(getActivity(), 3);
//						FanxingManager.goMainUi(getActivity());
					}
				}
			} else {
				openSpecialRecommendColumnActivity(data.name, data.id);
			}
            tagPos = -1;
			break;
		}
		default:
			break;
		}

	}
	public int tagPos = -1;
	@Override
	public void reloadData() {
		needLoadData = true;
		getRecommandDetail(true);
	}

	private void openSpecialRecommendColumnActivity(String title, int id) {
		Intent intent = new Intent(getActivity(),
				SpecialRecommendColumnActivity.class);
		intent.putExtra("id", id);
		intent.putExtra("title", title);
		startActivity(intent);
	}

	protected void actionOpenApp(String identify, final String url,
			final String appName, final int appId) {
		PackageManager manager = getActivity().getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(identify, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (info != null) {
			Intent intent = manager.getLaunchIntentForPackage(identify);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
		} else {
			new AlertDialog.Builder(getActivity())
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (!url.endsWith(".apk")) {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.VIEW");
										Uri content_url = Uri.parse(url);
										intent.setData(content_url);
										startActivity(intent);
									} else {
										Intent intent = new Intent(
												getActivity(),
												AppDownloadService.class);
										intent.putExtra("url", url);
										intent.putExtra("name", appName);
										intent.putExtra("appId", appId);
										intent.putExtra("resId",
												R.drawable.icon_small);
										getActivity().startService(intent);
									}
								}

							}).setMessage("下载" + appName + "感受更多精彩？")
					.setTitle("更多").create().show();

		}
	}

	ArrayList<String> appList = new ArrayList<String>();

	class MyInstalledReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals("android.intent.action.PACKAGE_ADDED")) { // install
				String packageName = intent.getDataString();
				Log.i("homer", "安装了 :" + packageName);
				for (int i = 0; i < rpd.getApps().size(); i++) {
					AppData appData = rpd.getApps().get(i);
					if (packageName.equals("package:" + appData.packageName)
							&& !appList.contains(appData.packageName)) {
						appList.add(appData.packageName);
						VolleyRequest.DownloadRecommendApp(
								RecommendFragment.this, appData.id,
								RecommendFragment.this);
					}
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyRequest.cancelRequest(Constants.recommendDetail);
	}

	@Override
	public void onError(int code) {
		listView.onRefreshComplete();
	}

	@Override
	public void OnDownloadRecommendApp(int errCode, int totalPoint) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (totalPoint == UserNow.current().totalPoint) {
				Toast.makeText(mActivity, R.string.rcmdApp_installed,
						Toast.LENGTH_SHORT).show();
			} else {
				UserNow.current().setTotalPoint(totalPoint,UserNow.current().vipIncPoint);
			}
		}

	}

	private class FabOnScrollListener implements OnScrollListener {

		private boolean currStatus = false;

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (firstLoading && view.getFirstVisiblePosition() != 0) {
					mAlimamaHelper.addTaobaoWall(advert);
					firstLoading = false;
				}
				if (view.getFirstVisiblePosition() == 0) {
					setStatus(false);
				} else {
					setStatus(true);
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}

		private void setStatus(boolean status) {
			if (currStatus == status) {
				return;
			}
			if (status) {
				advertLayout.setVisibility(View.VISIBLE);
				currStatus = true;
			} else {
				advertLayout.setVisibility(View.GONE);
				currStatus = false;
			}
		}
	}


}
