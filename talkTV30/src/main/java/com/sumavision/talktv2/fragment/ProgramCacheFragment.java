package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ProgramDetailHalfActivity;
import com.sumavision.talktv2.adapter.CacheJujiAdapter;
import com.sumavision.talktv2.adapter.JujiAdapter;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.components.StaticGridView;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.CacheRequest;
import com.sumavision.talktv2.http.json.LoadSubListParser;
import com.sumavision.talktv2.http.json.LoadSubListRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramCacheFragment extends Fragment implements OnClickListener{
	
	private PullToRefreshListView episodeList;
	private StaticGridView episodeGrid;
	private PullToRefreshScrollView episodeScroll;
	private CacheJujiAdapter adapter;
	private TextView back;
	
	private LinearLayout episodeListLayout;
	private LinearLayout episodeGridLayout;
	
	private TextView listStorage;
	private TextView gridStorage;
	private TextView listStartCache;
	private TextView gridStartCache;
	private Spinner defSpinner;

	private int platformId;
	private boolean isNative;
	String[] defValues = {"superDef","hight","standar"};
	String defSelected = "superDef";
	
	private ProgramData programData;
	private final int LOADING_NUMBER = 50;
	View loading;
	private ArrayList<JiShuData> episodeData = new ArrayList<JiShuData>();
	
	public static ProgramCacheFragment newInstance(int platformid, boolean isNative) {
		ProgramCacheFragment fragment = new ProgramCacheFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("platformId", platformid);
		bundle.putBoolean("isNative",isNative);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getExtras();
		View view = inflater.inflate(R.layout.fragment_program_cache, null);
		episodeList = (PullToRefreshListView) view.findViewById(R.id.episode_list);
		episodeGrid = (StaticGridView) view.findViewById(R.id.episode_grid);
		episodeScroll = (PullToRefreshScrollView) view.findViewById(R.id.episode_scrollview);
		back = (TextView) view.findViewById(R.id.episode_fragment_back);
		loading = view.findViewById(R.id.loading);
		episodeListLayout = (LinearLayout) view.findViewById(R.id.episode_list_layout);
		episodeGridLayout = (LinearLayout) view.findViewById(R.id.episode_grid_layout);
		listStorage = (TextView) view.findViewById(R.id.episode_storage);
		gridStorage = (TextView) view.findViewById(R.id.episode_storage_grid);
		listStorage.setOnClickListener(this);
		gridStorage.setOnClickListener(this);
		listStartCache = (TextView) view.findViewById(R.id.episode_start_cache);
		gridStartCache = (TextView) view.findViewById(R.id.episode_start_cache_grid);
		defSpinner = (Spinner) view.findViewById(R.id.episode_fragment_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.video_def, R.layout.spinner_item);
		adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
		defSpinner.setAdapter(adapter);
		defSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				defSelected = defValues[position];
				PreferencesUtils.putString(getActivity(),null,"video_def",defSelected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		defSelected = PreferencesUtils.getString(getActivity(),null,"video_def","superDef");
		int pos;
		if (defSelected.equals(defValues[2])){
			pos = 2;
		}else if (defSelected.equals(defValues[1])){
			pos = 1;
		} else {
			pos = 0;
		}
		defSpinner.setSelection(pos,true);
		episodeScroll.setMode(Mode.PULL_FROM_END);
		episodeList.setMode(Mode.PULL_FROM_END);
		
		updateView();
		loading.setVisibility(View.VISIBLE);
		getPlatformData((int)programData.programId,platformId,0, LOADING_NUMBER);
		initEvents();
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	
	private void getExtras() {
		programData = ((ProgramDetailHalfActivity) getParentFragment()).programData;
		platformId = getArguments().getInt("platformId");
		isNative = getArguments().getBoolean("isNative");
	}
	
	private void updateView() {
		if (programData.pType == ProgramData.TYPE_TV 
				|| programData.pType == ProgramData.TYPE_DONGMAN) {
			adapter = new CacheJujiAdapter(getActivity(), ProgramData.TYPE_TV, episodeData);
			episodeGrid.setAdapter(adapter);
			episodeGridLayout.setVisibility(View.VISIBLE);
			gridStorage.setText(showStorageRoom());
		} else {
			adapter = new CacheJujiAdapter(getActivity(),  programData.subType == 2 ? JujiAdapter.TYPE_PIC : JujiAdapter.TYPE_MOVIE, episodeData);
			episodeList.setAdapter(adapter);
			episodeListLayout.setVisibility(View.VISIBLE);
			listStorage.setText(showStorageRoom());
		}
	}
	private void getPlatformData(int programId,int platformId,int first,int count){
		final LoadSubListParser loadSubListParser = new LoadSubListParser();
		VolleyHelper.post(
				new LoadSubListRequest(
						programId,platformId,first, count).make(),
				new ParseListener(loadSubListParser) {
					@Override
					public void onParse(BaseJsonParser parser) {
						loading.setVisibility(View.GONE);
						episodeList.onRefreshComplete();
						episodeScroll.onRefreshComplete();
						if (loadSubListParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							List<JiShuData> temp = loadSubListParser.subList;
							if (temp == null || temp.size() == 0){
								return;
							}
							((ProgramDetailHalfActivity)getParentFragment()).filterCacheInfo(temp, programData);
							episodeData.addAll(temp);
							updateEpisodeView();
							if(temp != null && temp.size()<LOADING_NUMBER){
								episodeList.setMode(Mode.DISABLED);
								episodeScroll.setMode(Mode.DISABLED);
							}
						}
					}
				}, null);
	}
//	private void getPlatformData(int first, int count) {
//		final PlatVideoParser platVideoParser = new PlatVideoParser();
//		VolleyHelper.post(
//				new PlatVideoRequest(
//						platformId,
//						programData.programId, 0, first, count).make(),
//				new ParseListener(platVideoParser) {
//					@Override
//					public void onParse(BaseJsonParser parser) {
//						loading.setVisibility(View.GONE);
//						episodeList.onRefreshComplete();
//						episodeScroll.onRefreshComplete();
//						if (platVideoParser.errCode == JSONMessageType.SERVER_CODE_OK) {
//							ArrayList<JiShuData> temp = platVideoParser.subList;
//							((ProgramDetailHalfActivity)getParentFragment()).filterCacheInfo(temp, programData);
//							episodeData.addAll(temp);
//							if (temp.size() < LOADING_NUMBER) {
//								episodeList.setMode(Mode.DISABLED);
//								episodeScroll.setMode(Mode.DISABLED);
//							}
//							updateEpisodeView();
//						}
//					}
//				}, null);
//	}
	
	private void updateEpisodeView() {
		adapter.notifyDataSetChanged();
	}
	
	private void initEvents() {
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		episodeList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
			}
			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				getPlatformData((int)programData.programId,platformId,episodeData.size(), LOADING_NUMBER);
			}
		});
		episodeScroll.setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
				getPlatformData((int)programData.programId,platformId,episodeData.size(), LOADING_NUMBER);
			}
		});
		episodeList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = (int) id;
				CacheInfo info = episodeData.get(pos).cacheInfo;
				if (info.state == 7) {
					ToastHelper.showToast(getActivity(), "由于视频原因本片暂时只提供在线观看");
					return;
				}
				adapter.notifyDataSetChanged();
				if (info.state == 5) {
					info.state = 6;
					adapter.notifyDataSetChanged();
				} else if (info.state == 6) {
					info.state = 5;
					adapter.notifyDataSetChanged();
				}
			}
		});
		episodeGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = (int) id;
				CacheInfo info = episodeData.get(pos).cacheInfo;
				if (info.state == 7) {
					ToastHelper.showToast(getActivity(), "由于视频原因本片暂时只提供在线观看");
					return;
				}
				adapter.notifyDataSetChanged();
				if (info.state == 5) {
					info.state = 6;
					adapter.notifyDataSetChanged();
				} else if (info.state == 6) {
					info.state = 5;
					adapter.notifyDataSetChanged();
				}
			}
		});
		listStartCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCache();
			}
		});
		gridStartCache.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startCache();
			}
		});
	}
	
	private String showStorageRoom() {
		String space = "";
		int type = 0;
		boolean isSdCard = false;
		File path = null;
		ArrayList<String> dirs = CommonUtils.getStoragePath(getActivity());
		if (dirs.size() == 0) {
			space = String.format("可用空间  剩余：%1$sG / 总量：%2$sG", "0", "0");
			return space;
		}
		type = getCachePathType();
		if (type == 0) {
			if (dirs.size() > 1) {
				path = new File(dirs.get(1));
				isSdCard = true;
			} else if (dirs.size() == 1
					&& Environment.isExternalStorageRemovable()) {
				path = new File(dirs.get(0));
				isSdCard = true;
			} else {
				setCachePathType(1);
				path = new File(dirs.get(0));
			}
		} else {
			if (dirs.size() > 1) {
				path = new File(dirs.get(0));
				isSdCard = false;
			} else if (dirs.size() == 1
					&& !Environment.isExternalStorageRemovable()) {
				path = new File(dirs.get(0));
				isSdCard = false;
			} else {
				path = new File(dirs.get(0));
			}
		}
		StatFs localStatFs = new StatFs(path.getPath());
		long l1 = localStatFs.getBlockCount();
		long l2 = localStatFs.getAvailableBlocks();
		long l3 = localStatFs.getBlockSize();
		long nSDTotalSize = (l1 * l3);
		long nSDFreeSize = (l2 * l3);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String allSize = decimalFormat.format(nSDTotalSize
				/ (1024 * 1024 * 1024.0));
		String freeSize = decimalFormat.format(nSDFreeSize
				/ (1024 * 1024 * 1024.0));

		if (isSdCard) {
			space = String.format("SD卡  剩余：%1$sG / 总量：%2$sG", freeSize,
					allSize);
		} else {
			space = String.format("手机内存  剩余：%1$sG / 总量：%2$sG", freeSize,
					allSize);
		}
		return space;
	}
	
	private int getCachePathType() {
		return PreferencesUtils.getInt(getActivity(), null, "cache_path_type");
	}
	
	private void setCachePathType(int type) {
		PreferencesUtils.putInt(getActivity(), null, "cache_path_type", type);
	}
	
	
	private void startCache() {
		try {
			ArrayList<JiShuData> datas = getCacheInfoData();
			if (datas != null && datas.size() > 0) {
//				Collections.reverse(datas);
				AccessDownload accessDownload = AccessDownload
						.getInstance(getActivity());
				for (JiShuData jishuData : datas) {
					DownloadInfo downloadInfo = new DownloadInfo();
					downloadInfo.programId = (int) programData.programId;
					downloadInfo.subProgramId = jishuData.id;
					downloadInfo.programPic = programData.pic;
					downloadInfo.definition = defSelected;

					InternalExternalPathInfo internalExternalPathInfo = CommonUtils
							.getInternalExternalPath(getActivity());
					String path = null;

					// 获取用户设置的存储路径，0外部存储，1内部存储
					int type = PreferencesUtils.getInt(getActivity(), null,
							"cache_path_type");
					if (type == 0) {
						if (internalExternalPathInfo.removableSDcard != null) {
							path = internalExternalPathInfo.removableSDcard;
						} else {
							// 如果没有外置，则判断有没有内置的，如果有则设置默认路径为内置
							if (internalExternalPathInfo.emulatedSDcard != null) {
								PreferencesUtils.putInt(getActivity(), null,
										"cache_path_type", 1);
								path = internalExternalPathInfo.emulatedSDcard;
							} else {
								Toast.makeText(getActivity(),
										"无外置sd卡，无法缓存，请修改存储路径为手机存储",
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					} else {
						if (internalExternalPathInfo.emulatedSDcard != null) {
							path = internalExternalPathInfo.emulatedSDcard;
						} else {
							if (internalExternalPathInfo.removableSDcard != null) {
								PreferencesUtils.putInt(getActivity(), null,
										"cache_path_type", 0);
								path = internalExternalPathInfo.removableSDcard;
							} else {
								Toast.makeText(getActivity(),
										"无内置存储空间，无法缓存，请修改存储路径为SD卡",
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}

					downloadInfo.sdcardDir = path;
					if (programData.name != null
							&& !programData.name.equals(jishuData.name)) {
						downloadInfo.programName = programData.name + "\n"
								+ jishuData.name;
					} else {
						downloadInfo.programName = programData.name == null ? "电视粉节目"
								: programData.name;
					}
					downloadInfo.initUrl = getInitUrl(jishuData);
					downloadInfo.state = DownloadInfoState.WAITTING;
					accessDownload.save(downloadInfo);
				}
				ArrayList<DownloadInfo> downloadingInfos = accessDownload
						.queryDownloadInfo(DownloadInfoState.DOWNLOADING);
				if (downloadingInfos == null || downloadingInfos.size() == 0) {
					ArrayList<DownloadInfo> waittings = accessDownload
							.queryDownloadInfo(DownloadInfoState.WAITTING);
					if (waittings != null && waittings.size() > 0) {
						DownloadInfo downloadInfo = waittings.get(0);
						downloadInfo.state = DownloadInfoState.DOWNLOADING;
						accessDownload.updateDownloadState(downloadInfo);
					}
					startCacheService();
				}
				Toast.makeText(getActivity(), "请到缓存中心查看进度",
						Toast.LENGTH_SHORT).show();
				cacheRequest();
			}
			if (episodeData != null) {
				((ProgramDetailHalfActivity)getParentFragment()).filterCacheInfo(episodeData, programData);
				adapter.notifyDataSetChanged();
			}
			getFragmentManager().popBackStack();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	ResultParser cacheParser = new ResultParser();

	private void cacheRequest() {
		VolleyHelper.post(new CacheRequest().make(), new ParseListener(
				cacheParser) {
			@Override
			public void onParse(BaseJsonParser parser) {

			}
		}, null);
	}
	
	public ArrayList<JiShuData> getCacheInfoData() {
		ArrayList<JiShuData> jishuDatas = episodeData;
		if (jishuDatas != null) {
			return filterPendingData(jishuDatas);
		}
		return null;
	}

	private ArrayList<JiShuData> filterPendingData(
			ArrayList<JiShuData> jishuDatas) {
		ArrayList<JiShuData> list = new ArrayList<JiShuData>();
		for (JiShuData temp : jishuDatas) {
			if (temp.cacheInfo != null && temp.cacheInfo.state == 6) {
				list.add(temp);
			}
		}
		return list;
	}
	
	public String getInitUrl(JiShuData jishuData) {
		if (TextUtils.isEmpty(jishuData.videoPath)) {
			return jishuData.url + "-webparse";
		}
		return jishuData.videoPath;
	}
	
	private void startCacheService() {
		Intent intent = new Intent(getActivity(), DownloadService.class);
		intent.putExtra(DownloadService.APPNAME_KEY,
				getString(R.string.app_name));
		intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
		getActivity().startService(intent);
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.loadSubList);
	}
}
