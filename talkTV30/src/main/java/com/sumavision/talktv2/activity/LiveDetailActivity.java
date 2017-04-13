package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv.videoplayer.activity.WebAvoidPicActivity;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ChannelDetailAdapter;
import com.sumavision.talktv2.adapter.ChannelTypeAdapter;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ToastHelper;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.AddRemindEvent;
import com.sumavision.talktv2.http.json.AddRemindParser;
import com.sumavision.talktv2.http.json.AddRemindRequest;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.DeleteRemindRequest;
import com.sumavision.talktv2.http.json.LiveDetailParser;
import com.sumavision.talktv2.http.json.LiveDetailRequest;
import com.sumavision.talktv2.http.json.ResultParser;
import com.sumavision.talktv2.service.LiveAlertService;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;

public class LiveDetailActivity extends LiveBaseActivity implements OnClickListener, OnScrollListener,
					OnItemClickListener{

	private ImageView pic;
	private TextView title;
	private TextView time;
	private TextView channelNameTxt;
	private Button living;
	private ListView listWeek;
	private ListView listProgram;
	private RelativeLayout titleLayout;
	private int channelId;
	private String channelName;
	private String picUrl;
	
	String programId = "";
	private int requestCode = 15;
	
	private ArrayList<String> listWeekDate;
	private ChannelTypeAdapter typeAdapter;
	private ChannelDetailAdapter channelDetailAdapter;
	
	private ArrayList<CpData> listCpData;
	private LiveDetailParser liveParser;
	public ChannelData channelData;
	
	private int remindPosition;
	
	public static int entryPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_live_detail);
		getExtras(getIntent());
		initViews();
		doRequest();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ChannelDetailActivity");
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ChannelDetailActivity");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private void getExtras(Intent intent) {
		channelId = intent.getIntExtra("channelId", 0);
		channelName = intent.getStringExtra("channelName");
		picUrl = intent.getStringExtra("pic");
	}

	private void initViews() {
		initLoadingLayout();
		hideLoadingLayout();
		pic = (ImageView) findViewById(R.id.pic);
		channelNameTxt = (TextView) findViewById(R.id.channelName);
		title = (TextView) findViewById(R.id.title);
		time = (TextView) findViewById(R.id.time);
		living = (Button) findViewById(R.id.living);
		listWeek = (ListView) findViewById(R.id.list_week);
		listProgram = (ListView) findViewById(R.id.list_program);
		titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
		titleLayout.setVisibility(View.GONE);
		titleLayout.setOnClickListener(this);
		if (channelName != null) {
			channelNameTxt.setText(channelName);
			getSupportActionBar().setTitle(channelName);
		}
		if (picUrl != null) {
			loadImage(pic, picUrl, R.drawable.channel_tv_logo_default);
		}
		listWeekDate = new ArrayList<String>();
		typeAdapter = new ChannelTypeAdapter(this, listWeekDate);
		listWeek.setAdapter(typeAdapter);
		listCpData = new ArrayList<CpData>();
		channelDetailAdapter = new ChannelDetailAdapter(this, listCpData);
		listProgram.setAdapter(channelDetailAdapter);
		listProgram.setOnScrollListener(this);
		listWeek.setOnItemClickListener(this);
		listProgram.setOnItemClickListener(this);
		living.setOnClickListener(this);
		Set<String> favs = PreferencesUtils.getStringSet(this,null,Constants.SP_CHANNEL_RECORD,new HashSet<String>());
		if (favs.contains(""+channelId)){
			living.setText("已收藏");
		} else {
			living.setText("收藏频道");
		}
	}
	
	private void doRequest() {
		showLoadingLayout();
		liveParser = new LiveDetailParser(channelName);
		VolleyHelper.post(new LiveDetailRequest(channelId).make(), new ParseListener(liveParser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (liveParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					titleLayout.setVisibility(View.VISIBLE);
					channelData = liveParser.channelData;
					listWeekDate.clear();
					listCpData.clear();
					listWeekDate.addAll(liveParser.weekDate);
					listCpData.addAll(channelData.cpList);
					if (listCpData.size() == 0) {
						showEmptyLayout("该频道暂无节目单");
						listWeek.setVisibility(View.GONE);
						listProgram.setVisibility(View.GONE);
					} else {
						listWeek.setVisibility(View.VISIBLE);
						listProgram.setVisibility(View.VISIBLE);
						updateView();
					}
				} else {
					hideLoadingLayout();
					showErrorLayout();
				}
			}
		}, null);
	}
	private void updateView() {
		channelDetailAdapter.notifyDataSetChanged();
		typeAdapter.notifyDataSetChanged();
		CpData c = null;
		for (int i = 0; i < listCpData.size(); i++) {
			c = listCpData.get(i);
			if (c.isPlaying == 0) {
				title.setText(c.name);
				channelData.shareProgramName = c.name;
				time.setText(c.startTime + "-" + c.endTime);
				listProgram.setSelection(i - 1);
				for (int j = 0; j < listWeekDate.size(); j++) {
					if (listWeekDate.get(j).equals(c.week)) {
						typeAdapter.setSelectedPos(j);
						typeAdapter.notifyDataSetInvalidated();
						break;
					}
				}
				break;
			}
		}
		hideLoadingLayout();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_layout:
		case R.id.living:
		case R.id.pic:
				Set<String> favs = PreferencesUtils.getStringSet(this,null,Constants.SP_CHANNEL_RECORD,new HashSet<String>());
			String tempId = channelId +"";
			if (favs.contains(tempId)){
				favs.remove(tempId);
				living.setText("收藏频道");
				ToastHelper.showToast(this, "删除频道成功");
			}else {
				favs.add(tempId);
				living.setText("已收藏");
				ToastHelper.showToast(this,"收藏频道成功");
			}
			PreferencesUtils.putStringSet(this,null,Constants.SP_CHANNEL_RECORD,favs);
			break;

		case R.id.liveBtn:
			MobclickAgent.onEvent(LiveDetailActivity.this, "zbjmdzhengzaibochu");
			liveThroughNet(
					channelData.netPlayDatas,
					TextUtils.isEmpty(programId) ? 0 : Integer
							.parseInt(programId), channelId, channelData);
			break;
		case R.id.item_review:
		case R.id.item_liveBtn:
			onLiveBtnClick((Integer) v.getTag(R.id.item_pos),
					(Integer) v.getTag(R.id.item_bool), (String) v.getTag(R.id.item_back));
			break;
		default:
			break;
		}
	}
	
	public void onLiveBtnClick(int position, int flag, String backUrl) {
		CpData cpData = channelDetailAdapter.getItem(position);
		programId = cpData.programId;
		if (!TextUtils.isEmpty(backUrl) && flag == CpData.TYPE_REVIEW) {
			entryPosition = position;
			Intent intent = new Intent();
			intent.setClass(this, WebAvoidPicActivity.class);
			intent.putExtra("path", backUrl);
			intent.putExtra("url", backUrl);
			intent.putExtra("hideFav", true);
			intent.putExtra("isBackLook", true);
			intent.putExtra("playType", PlayerActivity.LIVE_PLAY);
			intent.putExtra("title", cpData.name);
			intent.putExtra("channelName", channelName);
			intent.putExtra("channelId", channelId);
			intent.putExtra("toWeb",getIntent().getBooleanExtra("toWeb",false));
			intent.putExtra(PlayerActivity.INTENT_NEEDAVOID, Constants.NEEDAVOID_LIVE);
			startActivity(intent);
			return;
		}
		if (TextUtils.isEmpty(backUrl) && flag == CpData.TYPE_REVIEW) {
			return;
		}
		if (flag == CpData.TYPE_LIVE) {
			MobclickAgent.onEvent(LiveDetailActivity.this, "zbjmdbofang");
			liveThroughNet(channelData.netPlayDatas,
					TextUtils.isEmpty(programId) ? 0 : Integer
							.parseInt(programId), channelId, channelData);
		} else {
			MobclickAgent.onEvent(LiveDetailActivity.this, "zbjmdyuyue");
			// 预约或者取消预约
			if (UserNow.current().userID == 0) {
				startActivityForResult(new Intent(this, LoginActivity.class),
						requestCode);
			} else {
				if (cpData.order == 1) {
					deleteRemind(UserNow.current().userID,
							(int) cpData.remindId);
					remindPosition = position;
				} else {
					addRemind(UserNow.current().userID, cpData.id);
					remindPosition = position;
				}
			}
		}
	}
	
	private void addRemind(int userId, int cpId) {
		showLoadingLayout();
		final AddRemindParser addparser = new AddRemindParser();
		VolleyHelper.post(new AddRemindRequest(userId, cpId).make(), new ParseListener(addparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (addparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					DialogUtil.alertToast(getApplicationContext(), "预约成功!");
					CpData cpData = channelDetailAdapter.getItem(remindPosition);
					cpData.order = 1;
					cpData.remindId = addparser.remindId;
					channelDetailAdapter.notifyDataSetChanged();
					VodProgramData program = cpToProgram();
					Remind.get(program).save();
					Intent alertServcice = new Intent(LiveDetailActivity.this, LiveAlertService.class);
					startService(alertServcice);
					remindPosition = -1;
				} else {
					DialogUtil.alertToast(getApplicationContext(), addparser.errMsg+"");
				}
			}
		}, null);
	}
	
	private void deleteRemind(int userId, int remindId) {
		showLoadingLayout();
		final ResultParser rparser = new ResultParser();
		VolleyHelper.post(new DeleteRemindRequest(userId, String.valueOf(remindId)).make(), new ParseListener(rparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (rparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					DialogUtil.alertToast(getApplicationContext(), "取消预约成功!");
					CpData cpData = channelDetailAdapter.getItem(remindPosition);
					cpData.order = 0;
					channelDetailAdapter.notifyDataSetChanged();
					DataSupport.deleteAll(Remind.class,"cpid = ?",
							channelDetailAdapter.getItem(remindPosition).id+"");
					remindPosition = -1;
					Intent alertServcice = new Intent(LiveDetailActivity.this, LiveAlertService.class);
					startService(alertServcice);
				} else {
					DialogUtil.alertToast(getApplicationContext(), "取消预约失败!");
				}
			}
		}, null);
	}
	
	private VodProgramData cpToProgram() {
		CpData cp = channelDetailAdapter.getItem(remindPosition);
		VodProgramData program = new VodProgramData();
		program.cpId = cp.id;
		program.channelId = String.valueOf(channelId);
		program.cpName = cp.name;
		program.channelName = channelName;
		program.channelLogo = picUrl;
		program.cpDate = cp.date;
		program.startTime = cp.startTime;
		return program;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			int firstVisibleItem = view.getFirstVisiblePosition();
			int lastVisibleItem = view.getLastVisiblePosition();
			int size = 0;
			if (listCpData != null) {
				size = listCpData.size();
			}
			if (size > 0) {
				if (lastVisibleItem == view.getCount() - 1) {
					typeAdapter.setSelectedPos(listWeekDate.size() - 1);
					typeAdapter.notifyDataSetInvalidated();
					listWeek.setSelection(listWeekDate.size() - 1);
					Toast.makeText(this, "无更多内容", Toast.LENGTH_SHORT).show();
					return;
				}
				if (firstVisibleItem < size && firstVisibleItem != 0) {
					CpData data = listCpData.get(firstVisibleItem);
					String name = data.week;
					for (int i = 0; i < listWeekDate.size(); i++) {
						if (name.equals(listWeekDate.get(i))) {
							typeAdapter.setSelectedPos(i);
							typeAdapter.notifyDataSetInvalidated();
							listWeek.setSelection(i);
						}
					}
				} else if (firstVisibleItem < size && firstVisibleItem == 0) {
					typeAdapter.setSelectedPos(0);
					typeAdapter.notifyDataSetInvalidated();
					listWeek.setSelection(0);
					Toast.makeText(this, "已到达顶部", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == listWeek.getId()) {
			MobclickAgent.onEvent(LiveDetailActivity.this, "zbjmriqi");
			String name = listWeekDate.get(position);
			for (int i = 0; i < listCpData.size(); i++) {
				CpData data = listCpData.get(i);
				if (name.equals(data.week)) {
					listProgram.setSelection(i);
					break;
				}
			}
			typeAdapter.setSelectedPos(position);
			typeAdapter.notifyDataSetInvalidated();
		} else {
			CpData data = channelDetailAdapter.getItem(position);
			onLiveBtnClick(position, data.isPlaying, data.backUrl);
		}
	}

	public void onEvent(AddRemindEvent event) {
		doRequest();
	}
}
