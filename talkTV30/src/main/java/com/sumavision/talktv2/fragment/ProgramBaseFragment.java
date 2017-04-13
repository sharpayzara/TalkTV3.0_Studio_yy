package com.sumavision.talktv2.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv.videoplayer.activity.DLNAControllActivity;
import com.sumavision.talktv.videoplayer.activity.MainWebPlayActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidActivity;
import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramActivity;
import com.sumavision.talktv2.activity.ProgramCacheActivity;
import com.sumavision.talktv2.activity.ProgramDetailActivity;
import com.sumavision.talktv2.adapter.SourceAdapter;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.SimpleSourcePlatform;
import com.sumavision.talktv2.bean.SourcePlatform;
import com.sumavision.talktv2.fragment.SourceDialogFragment.OnSourceItemClickListener;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.RankingUpdateEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.PlatVideoParser;
import com.sumavision.talktv2.http.json.PlatVideoRequest;
import com.sumavision.talktv2.http.json.UshowOnOffParser;
import com.sumavision.talktv2.http.json.UshowOnOffRequest;
import com.sumavision.talktv2.http.listener.OnDianzanListener;
import com.sumavision.talktv2.http.listener.OnProgramMicroVideoListener;
import com.sumavision.talktv2.http.listener.OnSignProgramListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.StringUtils;

import de.greenrobot.event.EventBus;

public abstract class ProgramBaseFragment extends BaseFragment implements
		OnClickListener, OnSignProgramListener, OnProgramMicroVideoListener,
		OnDianzanListener {
	public int countList = 20;
	public int countGrid = 120;
	protected int skipWeb;
	public ProgramData programData = new ProgramData();
	private int selectedPlayItemPos;

	public int getSelectedPlayItemPos() {
		return selectedPlayItemPos;
	}

	public void setSelectedPlayItemPos(int selectedPlayItemPos) {
		this.selectedPlayItemPos = selectedPlayItemPos;
	}

	private boolean subIsList = false;// 子节目显示为list

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		skipWeb = getArguments().getInt("skipWeb");
	}

	/**
	 * 子节目展示效果为list
	 * 
	 * @param subIsList
	 */
	public void setSubIsList(boolean subIsList) {
		this.subIsList = subIsList;
	}

	protected boolean IS_BACK = false;

	Button zanBtn;
	ImageView ushowView;

	protected void initHeaderView() {
		baseLayout = (LinearLayout) rootView.findViewById(R.id.base_layout);
		signLayout = (RelativeLayout) rootView.findViewById(R.id.sign_layout);
		zanBtn = (Button) rootView.findViewById(R.id.btn_zan);
		zanBtn.setOnClickListener(this);
		ushowView = (ImageView) rootView.findViewById(R.id.ushow_anchor);
		ushowView.setOnClickListener(this);
		nameView = (TextView) rootView.findViewById(R.id.programName);
		directorView = (TextView) rootView.findViewById(R.id.director);
		actorView = (TextView) rootView.findViewById(R.id.actors);
		regionView = (TextView) rootView.findViewById(R.id.region);
		scoreView = (TextView) rootView.findViewById(R.id.score);
		programImageView = (ImageView) rootView.findViewById(R.id.pic);

		dlnaResume = (ImageButton) rootView.findViewById(R.id.dlna_resume);
		dlnaResume.setOnClickListener(this);
		rootView.findViewById(R.id.goDetail).setOnClickListener(this);
		initSignViews();
		initLoadingLayout();
	}

	@Override
	public void reloadData() {
		((ProgramActivity) getActivity()).getProgramInfo();
		// getProgramHeaderInfo(programData.programId, programData.cpId);
	}

	SourceAdapter sourceAdapter;

	private Button cacheBtn;

	protected ImageView sourceImg, sourceDownView;
	protected LinearLayout singleSourceLayout;
	ArrayList<SimpleSourcePlatform> plats;

	protected void initSignViews() {
		singleSourceLayout = (LinearLayout) rootView
				.findViewById(R.id.llayout_source);
		singleSourceLayout.setOnClickListener(this);
		sourceImg = (ImageView) rootView.findViewById(R.id.imgv_source);
		sourceDownView = (ImageView) rootView.findViewById(R.id.iv_down);

		cacheBtn = (Button) rootView.findViewById(R.id.btn_cache);
		cacheBtn.setOnClickListener(this);
		plats = new ArrayList<SimpleSourcePlatform>();
		if (programData != null) {
			for (SourcePlatform p : programData.platformList) {
				SimpleSourcePlatform sp = new SimpleSourcePlatform();
				sp.id = p.id;
				sp.name = p.name;
				sp.pic = p.pic;
				plats.add(sp);
			}
		}
	}

	protected void showSourceDialog() {
		sourceFragment = (SourceDialogFragment) getFragmentManager()
				.findFragmentByTag("source");
		if (sourceFragment == null) {
			sourceFragment = SourceDialogFragment.newInstance(plats);
			sourceFragment.setListener(new OnSourceItemClickListener() {

				@Override
				public void OnSourceItemClick(int position) {
					if (position != currentPlatPos) {
						currentPlatPos = position;
						SourcePlatform plat = programData.platformList
								.get(position);
						loadImage(sourceImg, plat.pic,
								R.drawable.play_source_default);
						changeSource = true;
						if (plat.jishuList != null && plat.jishuList.size() > 0) {
							updateJishu(plat.jishuList, 0);
						} else {
							getPlatVideoList(position, 0, countList);
						}
					}

				}
			});
		}
		if (!sourceFragment.isAdded()) {
			sourceFragment.show(getFragmentManager(), "source");
		}
	}

	SourceDialogFragment sourceFragment;
	public int currentPlatPos;

	UshowOnOffParser ushowParser = new UshowOnOffParser();
	protected boolean changeSource;

	protected void ushowSwitchRequest() {
		VolleyHelper.post(new UshowOnOffRequest().make(), new ParseListener(
				ushowParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				if (mActivity != null) {
					ushowView.setVisibility(ushowParser.isOpen ? View.VISIBLE
							: View.GONE);
				}

			}
		}, null);
	}

	PlatVideoParser platVideoParser = new PlatVideoParser();
	protected boolean loadMore;

	public void getPlatVideoList(final int platIndex, int first, int count) {
		if (first == 0) {
			showLoadingLayout();
		} else {
			loadMore = true;
		}
		VolleyHelper.post(
				new PlatVideoRequest(
						programData.platformList.get(platIndex).id,
						programData.programId, 0, first, count).make(),
				new ParseListener(platVideoParser) {

					@Override
					public void onParse(BaseJsonParser parser) {
						hideLoadingLayout();
						if (platVideoParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							programData.platformList.get(platIndex).jishuList
									.addAll(platVideoParser.subList);
						} else {
							Log.e("getPlatVideoList", "加载失败");
						}
						updateJishu(platVideoParser.subList,
								platVideoParser.errCode);

					}
				}, this);
	}

	public abstract void updateJishu(ArrayList<JiShuData> jishuList, int errcode);

	protected void getProgramMicroInfo(String titleName) {
		VolleyProgramRequest.ProgramMicroVideo(this, titleName, this);
	}

	protected void openProgramDetail() {
		Intent intent = new Intent(getActivity(), ProgramDetailActivity.class);
		intent.putExtra("programId", programData.programId);
		intent.putExtra("isChased", programData.isChased);
		intent.putExtra("topicId", programData.topicId);
		startActivity(intent);
	}

	public static final int CAHCE = 21;

	private void openCachePageActivity() {

		Intent intent = new Intent(getActivity(), ProgramCacheActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong("programId", programData.programId);
		bundle.putInt("pType", programData.pType);
		bundle.putString("programPic", programData.pic);
		SourcePlatform plat = new SourcePlatform();
		if (programData.platformList.size() > 0) {
			plat = programData.platformList.get(currentPlatPos);
		}
		bundle.putSerializable("plat", plat);
		bundle.putString("programName", programData.name);
		intent.putExtra("bundle", bundle);
		getActivity().startActivityForResult(intent, CAHCE);
	}

	private void playHandler(int pos, ArrayList<JiShuData> jiShuDatas) {
		JiShuData jishu = jiShuDatas.get(pos);
		String url = jishu.url;
		String videoPath = jishu.videoPath;
		String title = jishu.name;
		int playType = 2;
		// 本地源，直接本地播放,地址
		if (!TextUtils.isEmpty(videoPath)) {
			openPlayerActivity(jiShuDatas, selectedPlayItemPos);
		}
		// 网页源，破解播放
		else if (!TextUtils.isEmpty(url)) {
//			boolean baiduParser = url.contains("pan.baidu.com")
//					|| url.contains("d.pcs.baidu.com");
//			if (baiduParser || url.contains("sohu") || url.contains("youku")
//					|| url.contains("iqiyi") || url.contains("tudou")
//					|| url.contains("letv") || url.contains("qq")
//					|| url.contains("pptv") || url.contains("hunantv")) {
//				openParserActivity(baiduParser, jiShuDatas);
//			} else {
//				openWebPlayActivity(url, videoPath, playType, title);
//			}
			openParserActivity(jiShuDatas);
		} else {
			Toast.makeText(getActivity(), "暂时无法播放！", Toast.LENGTH_SHORT).show();
		}
	}

	public void openParserActivity(ArrayList<JiShuData> jiShuDatas) {
		Intent intent = new Intent();
		intent.putExtra(PlayerActivity.INTENT_NEEDAVOID, skipWeb == 1);
//		if (baiduParser) {
//			intent.setClass(getActivity(), BaiduPathParserActivtiy.class);
//		} else {
			intent.setClass(getActivity(), WebAvoidActivity.class);
//		}
		setIntentExtra(intent, jiShuDatas, selectedPlayItemPos);
		getActivity().startActivityForResult(intent, PLAY);
	}

	private void openWebPlayActivity(String url, String videoPath, int isLive,
			String title) {
		Intent intent2 = new Intent(getActivity(), MainWebPlayActivity.class);
		intent2.putExtra("url", url);
		intent2.putExtra("videoPath", videoPath);
		intent2.putExtra("playType", isLive);
		intent2.putExtra("title", title);
		startActivity(intent2);
	}

	ArrayList<NetPlayData> netPlayDataList = new ArrayList<NetPlayData>();// 有直播播放地址的list集合
	ArrayList<NetPlayData> netPlayDataList1 = new ArrayList<NetPlayData>();// 没有直播播放地址的list集合

	/**
	 * 
	 * @param netPlayDatas
	 * @descraption 传入的list值
	 */
	@SuppressLint("UseSparseArrays")
	public void liveThroughNet(ArrayList<JiShuData> jiShuDatas, int arg2) {
		this.selectedPlayItemPos = arg2;
		playHandler(arg2, jiShuDatas);
	}

	/**
	 * 播放参数传递
	 * 
	 * @param intent
	 * @param playData
	 * @param jiShuDatas
	 * @param arg2
	 */
	private void setIntentExtra(Intent intent, ArrayList<JiShuData> jiShuDatas,
			int arg2) {
		JiShuData jishu = jiShuDatas.get(arg2);
		intent.putExtra("programPic", programData.pic);
		intent.putExtra("url", jishu.url);
		intent.putExtra("fav", programData.isChased);
		intent.putExtra("path", jishu.videoPath);
		intent.putExtra("subid", jishu.id);
		if (jiShuDatas != null) {// 传上下集数据
			Bundle mBundle = new Bundle();
			mBundle.putInt("mPosition", arg2);
			mBundle.putInt("subOrderType", programData.subOrderType);
			mBundle.putSerializable("subList", jiShuDatas);
			intent.putExtras(mBundle);
		}
		if (jiShuDatas.get(arg2).videoPath.contains("sdcard"))
			intent.putExtra("playType", 3);
		else
			intent.putExtra("playType", 2);

		if (programData.pType == ProgramData.TYPE_TV
				|| programData.pType == ProgramData.TYPE_DONGMAN) {
			intent.putExtra("programName", programData.name);
			intent.putExtra("title", "第" + jishu.name + "集");
		} else {
			intent.putExtra("title", jishu.name);
		}
		String id = String.valueOf(programData.programId);
		intent.putExtra("id", Integer.parseInt(id));
		intent.putExtra("topicId", programData.topicId);

	}

	public static final int PLAY = 30;

	private void openPlayerActivity(ArrayList<JiShuData> jiShuDatas, int arg2) {
		Intent intent = new Intent();
		Log.i("PlayerActivity", "programdetail");
		intent.putExtra(PlayerActivity.INTENT_NEEDAVOID, skipWeb == 1);
		intent.setClass(getActivity(), WebAvoidPicActivity.class);
		setIntentExtra(intent, jiShuDatas, arg2);
		getActivity().startActivityForResult(intent, PLAY);
	}

	protected int columnId = 0;

	public void play(ArrayList<JiShuData> jiShuDatas, int arg2) {
		setSelectedPlayItemPos(arg2);

		boolean playCache = playCacheVideo(jiShuDatas.get(arg2).id);
		if (!playCache) {
			if (columnId != 0 && columnId == 56) {
				JiShuData jishu = jiShuDatas.get(arg2);
				String title = jishu.name;
				int title1 = title.indexOf("：");
				String titleMicro = title.substring(title1 + 1);
				String titleMicro1 = "";
				try {
					titleMicro1 = URLEncoder.encode(titleMicro, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				getProgramMicroInfo(titleMicro1);
			} else {
				liveThroughNet(jiShuDatas, arg2);
			}
		}
	}

	/**
	 * 更新节目信息补充
	 */
	public abstract void onProgramHeader();

	static final String directorPre = "导演：", actorPre = "演员：",
			regionPre = "地区：", doubanPre = "评分：";
	protected LinearLayout baseLayout;
	protected RelativeLayout signLayout;
	protected TextView nameView, directorView, actorView, regionView,
			scoreView;
	protected ImageView programImageView, goDetailImageView;
	protected ImageButton dlnaResume;

	public void updateProgramHeader() {
		if (programData == null) {
			return;
		}
		baseLayout.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(programData.name)) {
			nameView.setText(programData.name);
		}
		String director = programData.director;
		if (StringUtils.isNotEmpty(director)) {
			directorView.setText(directorPre + director);
		} else {
			directorView.setVisibility(View.INVISIBLE);
		}
		String actors = programData.actors;
		if (StringUtils.isNotEmpty(actors)) {
			actorView.setText(actorPre + actors);
		} else {
			actorView.setVisibility(View.INVISIBLE);
		}
		String region = programData.region;
		if (StringUtils.isNotEmpty(region)) {
			regionView.setText(regionPre + region);
		} else {
			regionView.setVisibility(View.INVISIBLE);
		}
		double score = programData.doubanPoint;
		if (score > 1.0) {
			String scoreString = String.valueOf(score);
			if (scoreString != null) {
				if (scoreString.length() > 3)
					scoreString.substring(0, 2);
				scoreView.setText(doubanPre + scoreString);
			}
		}
		if (programData.isZan) {
			zanBtn.setSelected(true);
		}
		zanBtn.setText(getResources().getString(R.string.dianzan_count,
				programData.evaluateCount));
		String url = programData.pic;
		loadImage(programImageView, url, R.drawable.pd_program_pic);

	}

	protected void updateSignLayout() {
		signLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void signResult(int errCode) {
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			DialogUtil.alertToast(getActivity(), "签到失败!");
			break;
		case JSONMessageType.SERVER_CODE_OK:
			programData.isSigned = true;
			// signBtn.setImageResource(R.drawable.pd_sign_new_pressed);
			DialogUtil.alertToast(getActivity(), "签到成功!");
			updateSignLayout();
			break;
		default:
			break;
		}

	}

	private WebView webView;

	@Override
	public void getMicroVideoUrl(String url) {
		if (!subIsList) {
			IS_BACK = false;
			if (StringUtils.isNotEmpty(url)) {
				if (url.contains("http:")) {
					if (webView == null) {
						webView = (WebView) rootView
								.findViewById(R.id.programActivity_webView);
						webView.setVisibility(View.VISIBLE);
					}
					webView.loadUrl(url);
					webView.setWebViewClient(new WebViewClient() {
						@Override
						public void onPageFinished(WebView view, String url) {
							super.onPageFinished(view, url);
							if (!IS_BACK) {
								IS_BACK = true;
								webView.setVisibility(View.GONE);
							}
						}

					});

				} else {
				}

			}
		}
	}

	/**
	 * 缓存视频播放
	 * 
	 * @param subId
	 * @return
	 */
	protected boolean playCacheVideo(int subId) {
		AccessDownload accessDownload = AccessDownload
				.getInstance(getActivity());
		DownloadInfo downloadInfo = new DownloadInfo();
		downloadInfo.programId = (int) programData.programId;
		downloadInfo.subProgramId = subId;
		downloadInfo = accessDownload.queryCacheProgram(downloadInfo);
		if (downloadInfo.state == DownloadInfoState.DOWNLOADED) {
			openPlayerActivity(downloadInfo);
			return true;
		}
		return false;

	}

	private void openPlayerActivity(DownloadInfo downloadInfo) {
		Intent intent;
		intent = new Intent(getActivity(), PlayerActivity.class);
		intent.putExtra("path", downloadInfo.fileLocation);
		intent.putExtra("historyurl", downloadInfo.initUrl);
		intent.putExtra("url", downloadInfo.initUrl);
		intent.putExtra("playType", 3);
		intent.putExtra("title", downloadInfo.programName);
		intent.putExtra("id", downloadInfo.programId);
		intent.putExtra("detailid", downloadInfo.subProgramId);
		intent.putExtra("subid", downloadInfo.subProgramId);
		intent.putExtra("nameHolder", downloadInfo.programName);
		startActivity(intent);
	}
	
	protected DownloadInfo isCacheVideo(int subId) {
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llayout_source:
			showSourceDialog();
			break;
		case R.id.dlna_resume:
			Intent intent = new Intent(getActivity(),
					DLNAControllActivity.class);
			intent.putExtra("isResume", true);
			startActivity(intent);
			break;
		case R.id.goDetail:
			openProgramDetail();
			break;
		case R.id.btn_cache:
//			String state = Environment.getExternalStorageState();
//			if (Environment.MEDIA_MOUNTED.equals(state)) {
				openCachePageActivity();
//			} else {
//				DialogUtil.alertToast(getActivity(), "SD卡不存在 无法为您缓存");
//			}
			break;
		case R.id.btn_zan:
			if (zanBtn.isSelected()) {
				Toast.makeText(mActivity, "您已点赞", Toast.LENGTH_SHORT).show();
			} else {
				VolleyProgramRequest.dianzan(getActivity(), this,
						(int) programData.programId, 1, this);
			}
			break;
		case R.id.ushow_anchor:
//			new UshowManager(mActivity).launchHall();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDianzan(int errCode) {
		String msg = "";
		switch (errCode) {
		case 0:
		case 2:
			msg = "点赞成功";
			zanBtn.setText(getResources().getString(R.string.dianzan_count,
					programData.evaluateCount + 1));
			zanBtn.setSelected(true);
			EventBus.getDefault().post(new RankingUpdateEvent());
			break;
		case 1:
			msg = "点赞失败";
			break;
		case 3:
			msg = "稍后再来吧";
			break;
		default:
			break;
		}

		Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (DLNAControllActivity.needShowResumeBtn) {
			dlnaResume.setVisibility(View.VISIBLE);
		} else {
			dlnaResume.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void filterCacheInfo(ArrayList<JiShuData> jiShuDatas) {
		AccessDownload accessDownload = AccessDownload.getInstance(mActivity);
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

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.searchShowOnOff);
		VolleyHelper.cancelRequest(Constants.platFormProgramSubList);
		VolleyHelper.cancelRequest(Constants.programEvaluateAdd);
	}

}
