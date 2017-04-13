package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.RecommandAppAdapter;
import com.sumavision.talktv2.bean.AppData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnDownloadRecommendAppListener;
import com.sumavision.talktv2.http.listener.OnRecommendAppListener;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 精品软件推荐
 * 
 * @author suma-hpb
 * 
 */
public class RecommandAppActivity extends BaseActivity implements OnRecommendAppListener, OnDownloadRecommendAppListener, OnRefreshListener2<ListView> {

	private PullToRefreshListView myAppListView;

	private ArrayList<AppData> list = new ArrayList<AppData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recommand_app);
		getSupportActionBar().setTitle(getString(R.string.navigator_recommandapp));
		myAppListView = (PullToRefreshListView) findViewById(R.id.listView);
		myAppListView.setOnRefreshListener(this);
		myAppListView.setPullToRefreshOverScrollEnabled(false);
		adapter = new RecommandAppAdapter(this, list);
		myAppListView.setAdapter(adapter);
		initLoadingLayout();
		getRecommandAppData(0, 20);
	}

	MyInstalledReceiver installedReceiver;

	@Override
	public void onStart() {
		super.onStart();

		installedReceiver = new MyInstalledReceiver();
		IntentFilter filter = new IntentFilter();

		filter.addAction("android.intent.action.PACKAGE_ADDED");
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");

		this.registerReceiver(installedReceiver, filter);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("RecommandAppActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("RecommandAppActivity");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		if (installedReceiver != null) {
			this.unregisterReceiver(installedReceiver);
			installedReceiver = null;
		}

		super.onDestroy();
	}

	private void getRecommandAppData(int first, int count) {
		if (first == 0) {
			showLoadingLayout();
			myAppListView.setMode(Mode.PULL_FROM_END);
		}
		VolleyUserRequest.getRecommendApp(first, count, this, this);

	}

	private RecommandAppAdapter adapter;

	private void updateUI(ArrayList<AppData> appList) {
		list.clear();
		if (appList != null) {
			list.addAll(appList);
			if (list.size() == 0) {
				showEmptyLayout("暂时没有推荐软件");
			} else {
				adapter.notifyDataSetChanged();
				if (appList.size() < 20) {
					myAppListView.setMode(Mode.DISABLED);
				}

			}
		}
	}

	@Override
	protected void reloadData() {
		getRecommandAppData(0, 20);
	}

	private void DownloadRecommendApp(AppData app) {
		VolleyRequest.DownloadRecommendApp(this, app.id, this);
	}

	ArrayList<String> appList = new ArrayList<String>();

	class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) { // install
				String packageName = intent.getDataString();

				Log.i("homer", "安装了 :" + packageName);
				for (int i = 0; i < list.size(); i++) {
					if (packageName.equals("package:" + list.get(i).packageName) && !appList.contains(list.get(i).packageName)) {
						appList.add(list.get(i).packageName);
						DownloadRecommendApp(list.get(i));
					}
				}
			}

			if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { // uninstall
				String packageName = intent.getDataString();

				Log.i("homer", "卸载了 :" + packageName);
			}
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void getRecommendAppList(int errCode, ArrayList<AppData> appList) {
		hideLoadingLayout();
		myAppListView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			updateUI(appList);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void OnDownloadRecommendApp(int errCode, int totalPoint) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			if (totalPoint == UserNow.current().totalPoint) {
				Toast.makeText(getApplicationContext(), R.string.rcmdApp_installed, Toast.LENGTH_SHORT).show();
			} else {
				UserNow.current().setTotalPoint(totalPoint,UserNow.current().vipIncPoint);
			}
		}

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getRecommandAppData(list.size(), 20);

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.recommendAppList);
	}

}
