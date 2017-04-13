package com.sumavision.talktv2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv.videoplayer.activity.MainWebPlayActivity;
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
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.ImageLoaderHelper.OnImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * fragment基类：子类采用单例模式
 * 
 * @author suma-hpb
 * 
 */
public abstract class BaseFragment extends SherlockFragment implements
		OnHttpErrorListener {
	protected Activity mActivity;
	private ImageLoaderHelper imageLoaderHelper;
	protected LayoutInflater inflater;
	protected String lastUpdate;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}

	public void onDetach() {
		super.onDetach();
		mActivity = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resId = getArguments() != null ? getArguments().getInt("resId") : 0;
	}

	/**
	 * init view
	 * 
	 * @param view
	 */
	protected abstract void initViews(View view);

	protected View rootView;
	int resId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (resId == 0) {
			String message = " must set the layout resouce before use";
			throw new IllegalStateException(message);
		}
		if (rootView == null) {
			rootView = inflater.inflate(resId, null);
			imageLoaderHelper = new ImageLoaderHelper();
			this.inflater = inflater;
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			lastUpdate = getString(R.string.last_update, sdf.format(new Date()));
			initViews(rootView);
		} else {
			if(rootView.getParent() != null){
				((ViewGroup) rootView.getParent()).removeView(rootView);
			}
		}
		return rootView;
	}

	@Override
	public void onError(int code) {
		showErrorLayout();
		if (mActivity != null) {
			Toast.makeText(mActivity, "网络异常", Toast.LENGTH_SHORT).show();
		}
	}

	private RelativeLayout mErrLayout;
	private TextView mErrTv;
	private ProgressBar mProgressBar;

	/**
	 * 加载初始化--需包含loading_layout
	 */
	protected void initLoadingLayout() {
		mErrLayout = (RelativeLayout) rootView.findViewById(R.id.errLayout);
		mErrTv = (TextView) rootView.findViewById(R.id.err_text);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
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

	protected void liveThroughNet(ArrayList<NetPlayData> netPlayDatas,
			int programId, int channelId,boolean toWeb,String programName) {
		if (netPlayDatas.size() == 0) {
			Toast.makeText(getActivity(), "暂无可用视频源", Toast.LENGTH_SHORT).show();
			return;
		}
		ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
		NetPlayData temp = null;
		for (NetPlayData play : netPlayDatas) {
			if (!TextUtils.isEmpty(play.channelIdStr) || !TextUtils.isEmpty(play.videoPath) || !TextUtils.isEmpty(play.url)) {
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
		Log.i("mylog", "temp.channelName=" + channelName);
		if (!TextUtils.isEmpty(temp.channelIdStr) || !TextUtils.isEmpty(videoPath) || !TextUtils.isEmpty(url)) {
			Intent intent = new Intent();
			intent.putExtra(PlayerActivity.INTENT_NEEDAVOID,
					Constants.NEEDAVOID_LIVE);
			Log.i("mylog", "avoid basefragment:" + Constants.NEEDAVOID_LIVE);
			if (!TextUtils.isEmpty(temp.webPage)){
				intent.setClass(getActivity(), WebAvoidPicActivity.class);
			} else if (!TextUtils.isEmpty(temp.showUrl) || !TextUtils.isEmpty(url)) {
				intent.setClass(getActivity(), WebAvoidActivity.class);
			} else {
				intent.setClass(getActivity(), WebAvoidPicActivity.class);
			}
			intent.putExtra("id", programId);
			intent.putExtra("channelId", channelId);
			intent.putExtra("toWeb",toWeb);
			intent.putExtra("path", videoPath);
			if (TextUtils.isEmpty(url)) {
				url = videoPath;
			}
			intent.putExtra("url", url);
			intent.putExtra("p2pChannel",TextUtils.isEmpty(temp.channelIdStr)?"":temp.channelIdStr);
			intent.putExtra("playType", 1);
			intent.putExtra("title", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			intent.putExtra("channelName", TextUtils.isEmpty(channelName) ? "电视直播"
					: channelName);
			intent.putExtra("share_program",programName);
			Bundle bundle = new Bundle();
			bundle.putSerializable("NetPlayData", playList);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			openNetLiveActivity(temp, programId, channelId, toWeb);
		}
	}
	
	private void openNetLiveActivity(NetPlayData data, int programId,
			int channelId,boolean toWeb) {
		Intent intent2 = new Intent(getActivity(), MainWebPlayActivity.class);
		intent2.putExtra("url", data.url);
		intent2.putExtra("videoPath", data.videoPath);
		intent2.putExtra("playType", 1);
		intent2.putExtra("title", data.channelName);
		intent2.putExtra("channelId", channelId);
		intent2.putExtra("id", programId);
		intent2.putExtra("p2pChannel",TextUtils.isEmpty(data.channelIdStr)?"":data.channelIdStr);
		startActivity(intent2);
	}

	/**
	 * 加载失败时重新加载需重载改方法
	 */
	public abstract void reloadData();

	/**
	 * 加载等待
	 */
	protected void showLoadingLayout() {
		if (mErrLayout != null) {
			mErrLayout.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			mErrTv.setVisibility(View.GONE);
		}
	}

	protected void hideLoadingLayout() {
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

	protected void loadImageCacheDisk(final ImageView imageView, String url,
			int defaultPic) {
		imageLoaderHelper.loadImageCacheDisk(imageView, url, defaultPic);
	}

	/**
	 * 图片加载(进度)
	 * 
	 * @param imageView
	 * @param url
	 * @param defaultPic
	 * @param progressBar
	 */
	protected void loadImage(final ImageView imageView, String url,
			int defaultPic, RoundProgressBar progressBar,
			OnImageLoadingListener listener) {
		imageLoaderHelper.loadImageWithProgress(imageView, url, defaultPic,
				progressBar, listener);
	}

	/**
	 * 快速滑动时停止加载
	 * 
	 * @param listView
	 */
	protected void appScrollListener(ListView listView, OnScrollListener listener) {
		imageLoaderHelper.applyScrollListener(listView, listener);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void filterCacheInfo(List<JiShuData> jiShuDatas, ProgramData programData) {
		AccessDownload accessDownload = AccessDownload.getInstance(getActivity());
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
				.getInstance(getActivity());
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
