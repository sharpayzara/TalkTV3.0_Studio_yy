package com.sumavision.talktv2.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.AddProgramActivity;
import com.sumavision.talktv2.adapter.ShakeUploadAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DeleteUploadRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.http.json.UploadListParser;
import com.sumavision.talktv2.http.json.UploadListRequest;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 摇一摇添加影片
 * 
 * @author cx
 * 
 */
public class UploadFragment extends BaseFragment implements OnClickListener, OnRefreshListener2<ListView>, OnItemClickListener {

	private Button add;
	private PullToRefreshListView listView;
	private ShakeUploadAdapter adapter;
	private ArrayList<VodProgramData> vodprogram = new ArrayList<VodProgramData>();
	private UploadListParser uploadParser = new UploadListParser();
	private final int COUNT = 10;
	
	private LinearLayout eidtLayout;
	private TextView selectAll;
	private TextView delete;
	private boolean editMode;
	private boolean isSelectedAll;

	public static UploadFragment newInstance() {
		UploadFragment fragment = new UploadFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_upload);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(getActivity());
	}

	@Override
	protected void initViews(View view) {
		initLoadingLayout();
		hideLoadingLayout();
		
		eidtLayout = (LinearLayout) view.findViewById(R.id.edit_layout);
		selectAll = (TextView) view.findViewById(R.id.select_all);
		delete = (TextView) view.findViewById(R.id.delete);
		add = (Button) view.findViewById(R.id.add);
		listView = (PullToRefreshListView) view.findViewById(R.id.upload_list);
		View emptyView = view.findViewById(R.id.upload_empty);
		listView.setEmptyView(emptyView);
		
		selectAll.setOnClickListener(this);
		delete.setOnClickListener(this);
		add.setOnClickListener(this);
		
		listView.setMode(Mode.BOTH);
		listView.setOnItemClickListener(this);
		listView.setOnRefreshListener(this);
		listView.setPullToRefreshOverScrollEnabled(false);
		adapter = new ShakeUploadAdapter(getActivity(), vodprogram);
		listView.setAdapter(adapter);
		
		showLoadingLayout();
		getUploadProgram(0, COUNT);
	}
	
	public void getUploadProgram(final int first, int count) {
		VolleyHelper.post(new UploadListRequest(first, count).make(), new ParseListener(uploadParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (uploadParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					updateView(first);
				} else {
//					updateViewTest();
				}
				hideLoadingLayout();
			}
		}, null);
	}
	
	private void updateView(int first) {
		listView.onRefreshComplete();
		if (uploadParser.listProgram.size() < COUNT) {
			listView.setMode(Mode.PULL_FROM_START);
		}else {
			listView.setMode(Mode.BOTH);
		}
		if(first == 0){
			vodprogram.clear();
		}
		vodprogram.addAll(uploadParser.listProgram);
		adapter.notifyDataSetChanged();
	}
	
	private void updateViewTest() {
		for (int i = 0; i < 10; i++) {
			VodProgramData v = new VodProgramData();
			v.id = i + "";
			v.name = "name"+i;
			v.pic = "http://tvfan.cn/photo/hotActivity/2015/0115/bm7gcqlf9mln.jpg";
			v.playUrl = "asdf";
			v.monthGoodCount = 100 * i;
			vodprogram.add(v);
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_all:
			setAllSelectedStatus(!isSelectedAll);
			adapter.notifyDataSetChanged();
			break;
		case R.id.delete:
			String ids = getIds();
			if (!TextUtils.isEmpty(ids)) {
				deleteUploaded(ids);
			}else {
				ToastHelper.showToast(getActivity(),"请选择视频");
			}
			break;
		case R.id.add:
			MobclickAgent.onEvent(getActivity(), "yjptianjiayingpian");
			getActivity().startActivity(new Intent(getActivity(), AddProgramActivity.class));
			break;
		default:break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(getActivity());
	}

	@Override
	public void reloadData() {
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getUploadProgram(0, COUNT);
	}
	
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getUploadProgram(vodprogram.size(), COUNT);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int pos = (int) id;
		if (editMode) {
			setSingleSelectedStatus(!vodprogram.get(pos).selected, pos);
			changeSelectText();
			adapter.notifyDataSetChanged();
		} else {
			MobclickAgent.onEvent(getActivity(), "yjpwodeshangchuan");
			Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
			playerIntent.putExtra("isHalf",false);
			playerIntent.putExtra("id",Integer.parseInt(vodprogram.get(pos).id));
			playerIntent.putExtra("url",vodprogram.get(pos).playUrl);
			playerIntent.putExtra("subid",vodprogram.get(pos).subId);
			playerIntent.putExtra("title",vodprogram.get(pos).name);
			playerIntent.putExtra("playType",PlayerActivity.SHAKE_PLAY);
			startActivity(playerIntent);
		}
	}
	
	public void setEditMode(boolean editMode) {
		if (adapter != null) {
			this.editMode = editMode;
			adapter.setEditMode(editMode);
			if (!editMode) {
				eidtLayout.setVisibility(View.GONE);
				setAllSelectedStatus(false);
				add.setEnabled(true);
				add.setClickable(true);
			} else {
				eidtLayout.setVisibility(View.VISIBLE);
				add.setEnabled(false);
				add.setClickable(false);
			}
			adapter.notifyDataSetChanged();
		}
	}
	
	public void setAllSelectedStatus(boolean select) {
		for (int i = 0; i < vodprogram.size(); i++) {
			setSingleSelectedStatus(select, i);
		}
		isSelectedAll = select;
		selectAll.setText(isSelectedAll ? "取消全选":"全选");
	}
	
	public void setSingleSelectedStatus(boolean select, int index) {
		VodProgramData data = vodprogram.get(index);
		data.selected = select;
		vodprogram.set(index, data);
		
	}

	ArrayList<VodProgramData> listDelete = new ArrayList<VodProgramData>();
	private String getIds() {
		listDelete.clear();
		StringBuilder ids = new StringBuilder();
		for (int i = 0; i < vodprogram.size(); i++) {	
			VodProgramData v = vodprogram.get(i);
			if (v.selected) {
				listDelete.add(v);
				ids.append(v.id);
				ids.append(",");
			}
		}
		if (!ids.toString().endsWith(",")){
			return "";
		}
		return ids.substring(0, ids.length() - 1);
	}
	public void changeSelectText(){
		boolean temp = true;
		for (int i = 0; i < vodprogram.size(); i++) {
			VodProgramData v = vodprogram.get(i);
			if (!v.selected) {
				temp = false;
				break;
			}
		}
		if (temp){
			isSelectedAll = true;
			selectAll.setText("取消全选");
		}else{
			isSelectedAll = false;
			selectAll.setText("全选");
		}
	}
	ResultParser rparser = new ResultParser();
	private void deleteUploaded(String ids) {
		VolleyHelper.post(new DeleteUploadRequest(ids).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					vodprogram.removeAll(listDelete);
					adapter.notifyDataSetChanged();
					ToastHelper.showToast(getActivity(), "删除成功");
				} else {
					ToastHelper.showToast(getActivity(), "删除失败");
				}
			}
		}, null);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getUploadProgram(0, COUNT);
	}

	
}
