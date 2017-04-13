package com.sumavision.talktv2.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.activity.WebBrowserActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.EmergencyStoryActivity;
import com.sumavision.talktv2.activity.ExchangeLimitActivity;
import com.sumavision.talktv2.activity.InteractiveZoneActivity;
import com.sumavision.talktv2.activity.ShoppingHomeActivity;
import com.sumavision.talktv2.activity.SlidingMainActivity;
import com.sumavision.talktv2.activity.SpecialActivity;
import com.sumavision.talktv2.activity.SpecialProgramActivity;
import com.sumavision.talktv2.adapter.RecommendImageAdapter;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.RecommendData;
import com.sumavision.talktv2.bean.interactive.Interactive;
import com.sumavision.talktv2.service.AppDownloadService;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class FocusLayoutFragment extends BaseFragment {

	@Override
	protected void initViews(View view) {
	}

	/**
	 * 上方焦点图
	 * */
	public View headerView;
	private Gallery picView;
	private TextView picTitle;
	private LinearLayout picStarsLayout;
	private static final int MSG_REFRESH_GALLERY = 1;
	private static final int DELAY_DURATION = 6000;
	private RecommendImageAdapter imageAdapter;
	private int currentPagePosition = 0;
	protected ArrayList<RecommendData> listRecommend;
	private View starCacheView;
	private Handler handler;

	/**
	 * 初始化
	 * */
	protected void initStarsLayout() {
		headerView = inflater.inflate(R.layout.include_focus_layout, null);
		picView = (Gallery) headerView.findViewById(R.id.pic_view);
		picTitle = (TextView) headerView.findViewById(R.id.pic_title);
		picStarsLayout = (LinearLayout) headerView.findViewById(R.id.pic_star);
		picView.requestDisallowInterceptTouchEvent(true);
		picView.setAnimationDuration(0);
		picView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				position = (int) arg0.getAdapter().getItemId(position);
				changeLayoutPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		picView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				position = (int) arg0.getAdapter().getItemId(position);
				onRecommendPageClick(position, listRecommend);
			}
		});
		picView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.removeMessages(MSG_REFRESH_GALLERY);
					break;
				case MotionEvent.ACTION_UP:
					sendMsg(MSG_REFRESH_GALLERY, 3000);
					break;

				default:
					break;
				}
				return false;
			}
		});
	}

	public View getPicView(){
		return picView;
	}
	protected void updateStarsLayout(ArrayList<RecommendData> listRecommend) {
		this.listRecommend = listRecommend;
		List<String> urls = new ArrayList<String>();
		if (listRecommend != null && listRecommend.size() > 0) {
			for (int i = 0; i < listRecommend.size(); i++) {
				urls.add(listRecommend.get(i).pic);
			}
			imageAdapter = new RecommendImageAdapter(urls, getActivity());
			picView.setAdapter(imageAdapter);

			int bigPicSize = listRecommend.size();
			picStarsLayout.removeAllViews();
			for (int i = 0; i < bigPicSize; i++) {
				FrameLayout frame = (FrameLayout) inflater.inflate(
						R.layout.rcmd_pic_star, null);
				frame.setTag(i);
				picStarsLayout.addView(frame);
			}
			refreshStarsLayout(0);

			if (handler == null) {
				handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case MSG_REFRESH_GALLERY: {
							if (picView != null && imageAdapter.getCount() > 0) {
								picView.setSelection((currentPagePosition + 1)
										% imageAdapter.getCount());
							}
							sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
							break;
						}
						default:
							break;
						}
					}
				};
			}
			handler.removeMessages(MSG_REFRESH_GALLERY);
			sendMsg(MSG_REFRESH_GALLERY, DELAY_DURATION);
		}
	}

	private void sendMsg(int what, int delay) {
		Message msg = handler.obtainMessage(what);
		handler.sendMessageDelayed(msg, delay);
	}

	/**
	 * 刷新圆点
	 * */
	private void refreshStarsLayout(int position) {
		if (picStarsLayout.getChildCount() <= position) {
			return;
		}
		if (starCacheView != null) {
			starCacheView.setSelected(false);
		}
		starCacheView = picStarsLayout.getChildAt(position);
		starCacheView.setSelected(true);
		picTitle.setText(listRecommend.get(position).name);
	}

	/**
	 * 改变当前显示的位置
	 * */
	protected void changeLayoutPosition(int position) {
		currentPagePosition = position;
		refreshStarsLayout(position);
	}

	/**
	 * 显示焦点图布局
	 * */
	protected void showStarsLayout() {
		headerView.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏焦点图布局
	 * */
	protected void hideStarsLayout() {
		if (headerView != null) {
			headerView.setVisibility(View.GONE);
		}
	}

	public void onRecommendPageClick(int position, ArrayList<RecommendData> list) {
		MobclickAgent.onEvent(getActivity(), "banner");
		if (list == null)
			return;
		RecommendData rcd = list.get(position);
		int type = rcd.type;
		switch (type) {
		case RecommendData.TYPE_PROGRAM:
			MobclickAgent.onEvent(getActivity(), "jiaodiantu", "节目");
			openProgramDetailActivity(rcd.id,0, rcd.name, 0,0);
			break;
		case RecommendData.TYPE_ACTIVITY: // activity
			MobclickAgent.onEvent(getActivity(), "jiaodiantu", "活动");
			int acitivtyId = (int) rcd.id;
			openActivityDetailActivity(acitivtyId);
			break;
		case RecommendData.TYPE_USER:// user
			MobclickAgent.onEvent(getActivity(), "jiaodiantu", "用户");
			break;
		case RecommendData.TYPE_STAR:// star
			MobclickAgent.onEvent(getActivity(), "jiaodiantu", "明星");
			break;
		case RecommendData.TYPE_ADVERTISE:// 广告
			MobclickAgent.onEvent(getActivity(), "jiaodiantu", "广告");
			Intent iAd = new Intent(getActivity(), WebBrowserActivity.class);

			iAd.putExtra("url", rcd.url);
			iAd.putExtra("title", rcd.name);
			iAd.putExtra("where",5);
			startActivity(iAd);
			break;
		case RecommendData.TYPE_APP_RECOMMEND:
			final String url = rcd.url;
			final int appId = (int) rcd.id;
			final String name = rcd.appName;
			final String identify = rcd.identifyName;
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
				break;
			}
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
										intent.putExtra("name", name);
										intent.putExtra("appId", appId);
										intent.putExtra("resId",
												R.drawable.icon_small);
										getActivity().startService(intent);
									}
								}
							}).setMessage("下载" + rcd.name + "感受更多精彩？")
					.setTitle("更多").create().show();
			break;
		case RecommendData.TYPE_NEWS_ZONE:
			Intent intent1 = new Intent(getActivity(),
					EmergencyStoryActivity.class);
			intent1.putExtra("zoneId", rcd.id);
			startActivity(intent1);
			break;
		case RecommendData.TYPE_INTERACTIVE_ZONE:
			startActivity(new Intent(getActivity(),
					InteractiveZoneActivity.class));
			if (list.get(position).otherId != 0) {
				Interactive.getInstance().zoneId = (int) rcd.otherId;
			}
			break;
		case RecommendData.TYPE_USHOW:
			if (getActivity() instanceof SlidingMainActivity) {
//				((SlidingMainActivity) getActivity()).uShowLaunchHall();
//				AdStatisticsUtil.adCount(getActivity(), 3);
			}
			break;
		case RecommendData.TYPE_SHOPPING_HOME:
			startActivity(new Intent(getActivity(),
					ShoppingHomeActivity.class));
			break;
		case RecommendData.TYPE_CHILD_PROGRAM:
			openAvoidActivity(null,(int)rcd.otherId, (int) rcd.id, rcd.url, rcd.videoPath,
					rcd.name);
			break;
		case RecommendData.TYPE_CHANNEL:
			ArrayList<NetPlayData> datas = new ArrayList<NetPlayData>();
			NetPlayData data = new NetPlayData();
			data.name = rcd.name;
			data.id = (int) rcd.id;
			data.url = rcd.url;
			data.videoPath = rcd.videoPath;
			if (rcd.liveUrls != null && rcd.liveUrls.size()>0){
				datas = rcd.liveUrls;
			} else {
				datas.add(data);
			}
			liveThroughNet(datas, data.id, data.id,false,"");
			break;
		case RecommendData.TYPE_SPECIAL:
			openSpecialActivity((int) rcd.id, rcd.pic, false, rcd.name, 1);
			break;
		case RecommendData.TYPE_SPECIAL_PROGRAM:
			openSpecialActivity((int) rcd.id, rcd.pic, true, rcd.name, 2);
			break;
		case RecommendData.TYPE_KUGOU_FANXING:
			AdStatisticsUtil.adCount(getActivity(), Constants.bannerAD);
			FanxingManager.goMainUi(getActivity());
			break;
		case RecommendData.TYPE_GOODS_DETAIL:
			Intent goodsIntent = new Intent(getActivity(), ExchangeLimitActivity.class);
			goodsIntent.putExtra("hotGoodsId",rcd.id);
			goodsIntent.putExtra("goodsType",rcd.type);
			startActivity(goodsIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param intent
	 *            有高清、标清、超清时用
	 * @param id
	 *            节目id
	 * @param url
	 *            网页地址
	 * @param videoPath
	 *            直接播放源
	 * @param name
	 *            节目名次
	 * @description 如果videoPath不为空则直接播放videoPath
	 */
	protected void openAvoidActivity(Intent intent,int programId, int id, String url,
			String videoPath, String name) {
		if (intent == null) {
			intent = new Intent();
			intent.putExtra(PlayerActivity.TAG_INTENT_PLAYTYPE, 2);
		}
		intent.putExtra(PlayerActivity.TAG_INTENT_PROGRAMID, programId);
		intent.putExtra(PlayerActivity.TAG_INTENT_SUBID, id);
		intent.putExtra(PlayerActivity.TAG_INTENT_TITLE, name);
		if (!TextUtils.isEmpty(videoPath)) {
			intent.putExtra(PlayerActivity.TAG_INTENT_PATH, videoPath);
			intent.putExtra(PlayerActivity.TAG_INTENT_NEEDPARSE, false);
		} else {
			if (!TextUtils.isEmpty(url)) {
				intent.putExtra(PlayerActivity.TAG_INTENT_NEEDPARSE, true);
			}
		}

		if (!TextUtils.isEmpty(url)) {
			intent.putExtra(PlayerActivity.TAG_INTENT_URL, url);
			intent.setClass(getActivity(), WebAvoidActivity.class);
			intent.putExtra("where",1);
		} else {
			intent.putExtra("where",1);
			intent.setClass(getActivity(), WebAvoidPicActivity.class);
		}
		startActivity(intent);
	}

	protected void openSpecialActivity(int id, String pic, boolean isSub,
			String title, int to) {
		Intent intent = new Intent();
		if (to == 1) {
			intent.setClass(getActivity(), SpecialActivity.class);
			intent.putExtra("columnId", id);
		} else {
			intent.setClass(getActivity(), SpecialProgramActivity.class);
			intent.putExtra("programId", id);
		}
		intent.putExtra("pic", pic);
		intent.putExtra("title", title);
		intent.putExtra("isSub", isSub);
		startActivity(intent);
	}

	protected void openActivityDetailActivity(long id) {
		MobclickAgent.onEvent(getActivity(), "tjhuodong");
		Intent intent = new Intent(getActivity(), ActivityActivity.class);
		intent.putExtra("activityId", id);
		startActivity(intent);
	}

	protected void openProgramDetailActivity(long id, int subId, String updateName,
			long cpId,int pType) {
		Intent intent = new Intent(getActivity(), PlayerActivity.class);
//		Intent intent = new Intent(getActivity(), ProgramActivity.class);
		intent.putExtra("id", (int)id);
		intent.putExtra("subid", subId);
		intent.putExtra("playType", 2);
		intent.putExtra("isHalf", true);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
		intent.putExtra("ptype", pType);
		intent.putExtra("where",1);
		startActivity(intent);
	}

	@Override
	public void reloadData() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeMessages(MSG_REFRESH_GALLERY);
			handler = null;
		}
	}


}
