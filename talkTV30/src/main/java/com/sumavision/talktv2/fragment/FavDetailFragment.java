package com.sumavision.talktv2.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.LiveDetailActivity;
import com.sumavision.talktv2.activity.MyFavActivity;
import com.sumavision.talktv2.adapter.FavAdapter;
import com.sumavision.talktv2.bean.ChaseData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.fragment.DeleteDialogFragment.OnClickDeleteListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnChaseDeleteListener;
import com.sumavision.talktv2.http.listener.OnChaseListListener;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.listener.OnRemindListListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * 点播、直播收藏
 * 
 * @author suma-hpb
 * 
 */
public class FavDetailFragment extends BaseFragment implements OnItemLongClickListener, OnChaseListListener, OnRemindListListener, OnChaseDeleteListener,
		OnRefreshListener2<ListView>, OnDeleteRemindListener, OnItemClickListener, OnSharedPreferenceChangeListener {
	public static FavDetailFragment newInstance(boolean isLive) {
		FavDetailFragment fragment = new FavDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isLive", isLive);
		bundle.putInt("resId", R.layout.fragment_fav_detail);
		fragment.setArguments(bundle);
		return fragment;
	}

	boolean isLive;
	ArrayList<VodProgramData> programList = new ArrayList<VodProgramData>();

	SharedPreferences favSp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isLive = getArguments().getBoolean("isLive");
		favSp = getActivity().getSharedPreferences(Constants.pushMessage, Context.MODE_PRIVATE);
	}

	boolean firstLoad = true;
	boolean deleteAll;

	public void clear() {
		deleteAll = true;
		StringBuffer pids = new StringBuffer();
		for (int i = 0, len = programList.size(); i < len; i++) {
			pids.append(isLive ? programList.get(i).remindId : programList.get(i).id).append(",");
		}
		deleteProg(pids.substring(0, pids.length() - 1));
		DataSupport.deleteAll(Remind.class);
	}

	public void loadData() {
		if (firstLoad) {
			showLoadingLayout();
			programList.clear();
			if (isLive) {
				getFavLiveData(0, 10);
			} else {
				getFavData(0, 10);
			}

		}
		firstLoad = false;
	}

	private void getFavData(int first, int count) {
		VolleyProgramRequest.getFavDetail(UserNow.current().userID, first, count, mActivity, this, this);
	}

	private void getFavLiveData(int first, int count) {
		VolleyUserRequest.getRemind(mActivity, UserNow.current().userID, first, count, this, this);
	}

	PullToRefreshListView ptrListView;
	FavAdapter mFavAdapter;

	@Override
	public void onResume() {
		super.onResume();
		favSp.registerOnSharedPreferenceChangeListener(this);
		if (refreshLiveReminds) {
			refreshLiveReminds = false;
			reloadData();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		favSp.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		ptrListView = (PullToRefreshListView) view.findViewById(R.id.list);
		ptrListView.setMode(Mode.PULL_FROM_END);
		ptrListView.getRefreshableView().setOnItemLongClickListener(this);
		ptrListView.setOnRefreshListener(this);
		ptrListView.setPullToRefreshOverScrollEnabled(false);
		ptrListView.setEmptyView(inflater.inflate(R.layout.fav_empty_layout, null));
		ptrListView.setVisibility(View.GONE);
		mFavAdapter = new FavAdapter(getActivity(), isLive, programList);
		ptrListView.setAdapter(mFavAdapter);
		ptrListView.setOnItemClickListener(this);
		loadData();
	}

	@Override
	public void reloadData() {
		firstLoad = true;
		loadData();
	}

	int selectPos = -1;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
		selectPos = (int) id;
		DeleteDialogFragment fragment = DeleteDialogFragment.newInstance(getString(R.string.delete_this_fav));
		fragment.setOnClickDeleteListener(new OnClickDeleteListener() {

			@Override
			public void onDeleteClick() {
				String pid;
				deleteAll = false;
				if (isLive) {
					pid = String.valueOf(mFavAdapter.getItem((int) id).remindId);
					DataSupport.deleteAll(Remind.class, "cpid = ?",
							mFavAdapter.getItem((int) id).cpId + "");
				} else {
					pid = mFavAdapter.getItem((int) id).id;
				}
				deleteProg(pid);
			}
		});
		fragment.show(getFragmentManager(), "delete");
		return true;
	}

	private void deleteProg(String programIds) {
		showLoadingLayout();
		if (isLive) {
			VolleyUserRequest.deleteRemind(UserNow.current().userID, programIds, this, this);
		} else {
			VolleyUserRequest.chaseDelete(programIds, this, this);
		}
	}

	@Override
	public void getChaseList(int errCode, int chaseCount, ArrayList<ChaseData> chaseList) {
		if (getActivity() == null || getActivity().isFinishing()) {
			return;
		}
		hideLoadingLayout();
		ptrListView.onRefreshComplete();
		if (JSONMessageType.SERVER_CODE_OK == errCode) {
			ptrListView.setVisibility(View.VISIBLE);
			firstLoad = false;
			int num = chaseList.size();
			for (int i = 0; i < num; i++) {
				VodProgramData p = new VodProgramData();
				ChaseData chase = chaseList.get(i);
				p.id = "" + chase.programId;
				p.name = chase.programName;
				p.pic = chase.programPic;
				p.subId = String.valueOf(chase.latestSubId);
				p.updateName = chase.latestSubName;
				p.ptype = chase.pType;
				programList.add(p);
			}
			if (programList.size() > 0) {
				((MyFavActivity) getActivity()).showAction();
			}
			mFavAdapter.notifyDataSetChanged();
			if (programList.size() == chaseCount) {
				ptrListView.setMode(Mode.DISABLED);
			}
		} else {
			showErrorLayout();
		}
	}

	@Override
	public void chaseDeleteResult(int errCode) {
		onFavDelete(errCode);
	}

	private void onFavDelete(int errCode) {
		if (getActivity().isFinishing()) {
			return;
		}
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (deleteAll) {
				programList.clear();
				mFavAdapter.notifyDataSetChanged();
			} else {
				programList.remove(selectPos);
				mFavAdapter.notifyDataSetChanged();
			}
			if (programList.size() == 0) {
				((MyFavActivity) getActivity()).hideAction();
			}
		} else {
			Toast.makeText(getActivity(), "收藏删除失败", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void deleteRemindResult(int errCode) {
		onFavDelete(errCode);
	}

	public void showOrHideAction() {
		if (programList.size() > 0) {
			((MyFavActivity) getActivity()).showAction();
		} else {
			((MyFavActivity) getActivity()).hideAction();
		}
	}

	@Override
	public void getRemindList(int errCode, int remindCount, ArrayList<VodProgramData> remindList) {
		if (getActivity() == null || getActivity().isFinishing()) {
			return;
		}
		ptrListView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			firstLoad = false;
			ptrListView.setVisibility(View.VISIBLE);
			hideLoadingLayout();
			programList.addAll(remindList);
			mFavAdapter.notifyDataSetChanged();
			if (programList.size() == remindCount) {
				ptrListView.setMode(Mode.DISABLED);
			}
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		if (isLive) {
			getFavLiveData(programList.size(), 10);
		} else {
			getFavData(programList.size(), 10);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		VodProgramData program = programList.get((int) id);
		if (!isLive) {
			if (!TextUtils.isEmpty(program.subId)) {
				StringBuffer itemKey = new StringBuffer(UserNow.current().userID);
				itemKey.append("_").append(program.id);
				PreferencesUtils.putInt(getActivity(), Constants.SP_FAV, itemKey.toString(), Integer.parseInt(program.subId));
				view.findViewById(R.id.imgv_new).setVisibility(View.GONE);
				PreferencesUtils.putBoolean(getActivity(), Constants.pushMessage, Constants.key_favourite, false);
				// mFavAdapter.notifyDataSetChanged();
			}
			Intent intent = new Intent(getActivity(), PlayerActivity.class);
//			intent.putExtra("id", Long.parseLong(program.id));
			intent.putExtra("id", Integer.parseInt(program.id));
			intent.putExtra("isHalf", true);
			intent.putExtra("ptype",program.ptype);
			intent.putExtra("where",2);
			startActivity(intent);
		} else {
			if (!TextUtils.isEmpty(program.channelId)) {
				Intent intent = new Intent(getActivity(), LiveDetailActivity.class);
				intent.putExtra("channelName", program.channelName);
				intent.putExtra("channelId", Integer.parseInt(program.channelId));
				intent.putExtra("pic", program.channelLogo);
				startActivity(intent);
				refreshLiveReminds = true;
			}
		}
	}

	private boolean refreshLiveReminds;

	@Override
	public void onDestroy() {
		super.onDestroy();
		VolleyHelper.cancelRequest(Constants.chaseList);
		VolleyHelper.cancelRequest(Constants.remindList);
		VolleyHelper.cancelRequest(Constants.remindDelete);
		VolleyHelper.cancelRequest(Constants.chaseDelete);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(Constants.key_favourite)) {
			if (favSp.getBoolean(key, false)) {
				firstLoad = true;
				loadData();
			}
		}
	}
}
