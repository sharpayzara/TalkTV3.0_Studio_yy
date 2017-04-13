package com.sumavision.talktv2.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.components.RoundProgressBar;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.service.NetManagerReceiver.OnNetworkChangeListener;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.ImageLoaderHelper.OnImageLoadingListener;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.SmartBarUtils;
import com.sumavision.talktv2.utils.StatusBarUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * activity基类
 * 
 * @author suma-hpb
 * 
 */
public abstract class BaseActivity extends SherlockFragmentActivity implements
		OnHttpErrorListener, OnNetworkChangeListener {
	private ImageLoaderHelper imageLoaderHelper;
	protected String lastUpdate;// 用于下拉刷新控件更新label说明

	private RelativeLayout mErrLayout;
	private TextView mErrTv;
	private ProgressBar mProgressBar;

	/**
	 * 加载初始化--需包含loading_layout
	 */
	protected void initLoadingLayout() {
		mErrLayout = (RelativeLayout) findViewById(R.id.errLayout);
		mErrTv = (TextView) findViewById(R.id.err_text);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mErrLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mErrTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				reloadData();

			}
		});
	}

	/**
	 * 加载失败时重新加载需重载改方法
	 */
	protected void reloadData() {

	};

	/**
	 * 加载等待
	 */
	public void showLoadingLayout() {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		mErrTv.setVisibility(View.GONE);
	}

	public void hideLoadingLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.GONE);
		}
	}

	/**
	 * 加载失败
	 */
	protected void showErrorLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
			mErrTv.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 加载数据为空
	 * 
	 * @param emptyTip
	 */
	protected void showEmptyLayout(String emptyTip) {
		mErrLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mErrTv.setVisibility(View.VISIBLE);
		mErrTv.setClickable(false);
		mErrTv.setCompoundDrawables(null, null, null, null);
		mErrTv.setText(emptyTip);
	}

	protected int theme = 0;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("theme", theme);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			theme = R.style.AppTheme_Light;
		} else {
			theme = savedInstanceState.getInt("theme");
		}
		setTheme(theme);
		if (SmartBarUtils.hasSmartBar()) {
			getWindow().setUiOptions(
					ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
			SmartBarUtils.setBackIcon(getActionBar(), getResources()
					.getDrawable(R.drawable.ic_action_back));
			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), false);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		} else {
			getSupportActionBar().setDisplayUseLogoEnabled(true);
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setLogo(R.drawable.ic_action_back);
		}
		StatusBarUtils.setImmerseTheme(this, R.color.navigator_bg_color);
		super.onCreate(savedInstanceState);
		imageLoaderHelper = new ImageLoaderHelper();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		lastUpdate = getString(R.string.last_update, sdf.format(new Date()));
		// ViewServer.get(this).addWindow(this);
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		// ViewServer.get(this).setFocusedWindow(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 图片加载
	 * 
	 * @param imageView
	 * @param url
	 * @param defaultPic
	 */
	protected void loadImage(final ImageView imageView, String url,
			int defaultPic) {
		imageLoaderHelper.loadImage(imageView, url, defaultPic);
	}

	protected void loadImage(final ImageView imageView, String url,
			int defaultPic, OnImageLoadingListener listener) {
		imageLoaderHelper.loadImage(imageView, url, defaultPic, listener);
	}

	public void loadImageWithProgress(ImageView imageView, String url,
			int defalutPic, final RoundProgressBar progressBar) {
		imageLoaderHelper.loadImageWithProgress(imageView, url, defalutPic,
				progressBar, null);
	}

	public void loadImageWithProgress(ImageView imageView, String url,
			int defalutPic, final RoundProgressBar progressBar,
			OnImageLoadingListener listenerF) {
		imageLoaderHelper.loadImageWithProgress(imageView, url, defalutPic,
				progressBar, listenerF);
	}

	@Override
	public void onError(int code) {
		hideLoadingLayout();
		// showErrorLayout();
		Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void isNetworkConnected(boolean connect) {
		if (!connect) {
			Toast.makeText(this, "网络已断开", Toast.LENGTH_SHORT).show();
		}

	}
	
	protected void liveThroughNet(ArrayList<NetPlayData> netPlayDatas) {
		if (netPlayDatas.size() == 0) {
			Toast.makeText(this, "暂无可用视频源", Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
		NetPlayData temp = null;
		for (NetPlayData play : netPlayDatas) {
			if (!TextUtils.isEmpty(play.videoPath)  || !TextUtils.isEmpty(play.url)) {
				playList.add(play);
				if (null == temp) {
					temp = play;
				}
			}
		}
		if (null == temp) {
			temp = netPlayDatas.get(0);
		}
		String url = temp.url;
		String videoPath = temp.videoPath;
		String channelName = temp.channelName;// 分享内容

		if (!TextUtils.isEmpty(videoPath) || !TextUtils.isEmpty(videoPath)) {
			Intent intent = new Intent();
			intent.putExtra(PlayerActivity.INTENT_NEEDAVOID,
					Constants.NEEDAVOID_LIVE);
			Log.i("mylog", "avoid basefragment:"+Constants.NEEDAVOID_LIVE);
			if (!TextUtils.isEmpty(temp.webPage)){
				intent.setClass(this, WebAvoidPicActivity.class);
			}else if (!TextUtils.isEmpty(url)) {
				intent.setClass(this, WebAvoidActivity.class);
			} else {
				intent.setClass(this, WebAvoidPicActivity.class);
			}
//			intent.putExtra("id", programId);
//			intent.putExtra("channelId", channelId);
			intent.putExtra("path", videoPath);
			if (TextUtils.isEmpty(url)) {
				url = videoPath;
			}
			intent.putExtra("url", url);
			intent.putExtra("playType", 1);
			intent.putExtra("title", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			intent.putExtra("channelName", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			Bundle bundle = new Bundle();
			bundle.putSerializable("NetPlayData", playList);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	
	public void filterCacheInfo(List<JiShuData> jiShuDatas, ProgramData programData) {
		AccessDownload accessDownload = AccessDownload.getInstance(this);
		ArrayList<DownloadInfo> downloadedInfos = accessDownload
				.queryDownloadInfo(DownloadInfoState.DOWNLOADED);
		ArrayList<DownloadInfo> downloadIngInfos = accessDownload
				.queryDownloadInfo(DownloadInfoState.WAITTING);
		ArrayList<DownloadInfo> downloadIngInfos2 = accessDownload
				.queryDownloadInfo(DownloadInfoState.DOWNLOADING);
		ArrayList<DownloadInfo> downloadIngInfos3 = accessDownload
				.queryDownloadInfo(DownloadInfoState.PAUSE);
		downloadIngInfos.addAll(downloadIngInfos2);
		downloadIngInfos.addAll(downloadIngInfos3);
		for (JiShuData jishuData : jiShuDatas) {
			boolean isSet = false;
			for (DownloadInfo downloadedInfo : downloadedInfos) {
				if (programData.programId == downloadedInfo.programId
						&& jishuData.id == downloadedInfo.subProgramId) {
					CacheInfo info = new CacheInfo();
					info.state = DownloadInfoState.DOWNLOADED;
					jishuData.cacheInfo = info;
					jishuData.videoPath = downloadedInfo.fileLocation;
					// jishuData.netPlayDatas.get(0).videoPath =
					// downloadedInfo.fileLocation;
					isSet = true;
					break;
				}
			}
			for (DownloadInfo downloadingInfo : downloadIngInfos) {
				if (isSet) {
					break;
				}
				if (programData.programId == downloadingInfo.programId
						&& jishuData.id == downloadingInfo.subProgramId) {
					CacheInfo info = new CacheInfo();
					info.state = DownloadInfoState.WAITTING;
					jishuData.cacheInfo = info;
					isSet = true;
					break;
				}
			}
			if (!isSet) {
				CacheInfo info = new CacheInfo();
				info.state = 5;
				jishuData.cacheInfo = info;
			}

		}
	}
	
	public DownloadInfo isCacheVideo(int subId, ProgramData programData) {
		AccessDownload accessDownload = AccessDownload
				.getInstance(this);
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.programId = (int) programData.programId;
		downloadInfo.subProgramId = subId;
		downloadInfo = accessDownload.queryCacheProgram(downloadInfo);
		if (downloadInfo.state == DownloadInfoState.DOWNLOADED) {
			return downloadInfo;
		}
		return null;
	}

}
