package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.Calendar;

import org.litepal.crud.DataSupport;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ChannelDetailAdapter;
import com.sumavision.talktv2.adapter.FragmentAdapter;
import com.sumavision.talktv2.bean.ChannelData;
import com.sumavision.talktv2.bean.CpData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ColumnHorizontalScrollView;
import com.sumavision.talktv2.dao.Remind;
import com.sumavision.talktv2.fragment.ChannelDetailFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnAddRemindListener;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.service.LiveAlertService;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 频道详情页：<br>
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ChannelDetailActivity extends LiveBaseActivity implements
		OnPageChangeListener, OnClickListener, OnAddRemindListener,
		OnDeleteRemindListener {

	private RelativeLayout infoLayout;
	private ImageView pic, liveBtn;
	private TextView tvName;
	private ViewPager mViewPager;
	FragmentAdapter mFragmentAdapter;

	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	RelativeLayout tabLayout;
	private ImageView shadeLeft, shadeRight;

	private int channelId;
	private String channelName;
	private String picUrl;
	String programId = "";
	private int mScreenWidth, mItemWidth;
	/** 当前选中的栏目 */
	private int columnSelectIndex = 0;

	private int requestCode = 15;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_channel_detail);
		getExtras(getIntent());
		mScreenWidth = AppUtil.getScreenWidth(this);
		mItemWidth = mScreenWidth / 8;// 一个Item宽度为屏幕的1/7
		if (StringUtils.isNotEmpty(channelName)) {
			getSupportActionBar().setTitle(channelName);
		} else {
			getSupportActionBar().setTitle(R.string.navigator_channel_detail);
		}
		initViews();
		liveBtn.setOnClickListener(this);
		pic.setOnClickListener(this);
		infoLayout.setOnClickListener(this);
		channelData = new ChannelData();
		channelData.channelName = channelName;
		today = setWeekText();
		initTabColumn();
		initViewPager();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		getExtras(intent);
		today = setWeekText();
		if (today != mViewPager.getCurrentItem()) {
			mViewPager.setCurrentItem(today);
		} else {
			ChannelDetailFragment fragment = (ChannelDetailFragment) mFragmentAdapter
					.getItem(today);
			fragment.needLoad = true;
			fragment.reloadData();
		}
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

	private void getExtras(Intent intent) {
		channelId = intent.getIntExtra("channelId", 0);
		if (intent.hasExtra("tvName")) {
			channelName = intent.getStringExtra("tvName");
		}
		picUrl = intent.getStringExtra("pic");
	}

	private int setWeekText() {
		int returnValue = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		return returnValue;
	}

	private void initViews() {
		initLoadingLayout();
		hideLoadingLayout();
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		tabLayout = (RelativeLayout) findViewById(R.id.tab_layout);
		shadeLeft = (ImageView) findViewById(R.id.shade_left);
		shadeRight = (ImageView) findViewById(R.id.shade_right);

		infoLayout = (RelativeLayout) findViewById(R.id.info_layout);
		pic = (ImageView) findViewById(R.id.pic);
		liveBtn = (ImageView) findViewById(R.id.liveBtn);
		tvName = (TextView) findViewById(R.id.tvName);
		if (channelName != null)
			tvName.setText(channelName);
		if (picUrl != null) {
			loadImage(pic, picUrl, R.drawable.channel_tv_logo_default);
		}
	}

	String[] week;

	/**
	 * 初始化Column栏目项
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		week = getResources().getStringArray(R.array.week_day);
		int count = week.length;
		mColumnHorizontalScrollView.setParam(this, mScreenWidth,
				mRadioGroup_content, shadeLeft, shadeRight, null, tabLayout);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.MATCH_PARENT);
			params.leftMargin = 5;
			params.rightMargin = 5;
			TextView columnTextView = new TextView(this);
			columnTextView.setTextAppearance(this,
					R.style.channel_detail_tag_text_style);
			columnTextView
					.setBackgroundResource(R.drawable.channel_detail_tab_bg);
			columnTextView.setGravity(Gravity.CENTER);
			columnTextView.setId(i);
			columnTextView.setText(week[i]);
			columnTextView.setTextColor(getResources().getColorStateList(
					R.color.channel_detail_tab));
			if (columnSelectIndex == i) {
				columnTextView.setSelected(true);
			}
			columnTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							localView.setSelected(true);
							mViewPager.setCurrentItem(i);
						}
					}
				}
			});
			mRadioGroup_content.addView(columnTextView, i, params);
		}
	}

	ArrayList<Fragment> fragments;

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOnPageChangeListener(this);
		fragments = new ArrayList<Fragment>();
		for (int i = 0; i < 7; i++) {
			fragments.add(ChannelDetailFragment
					.newInstance(channelId, i, today));
		}
		mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),
				fragments);
		mViewPager.setAdapter(mFragmentAdapter);
		new Handler() {
			public void handleMessage(android.os.Message msg) {
				mViewPager.setCurrentItem(today);
			};
		}.sendEmptyMessageDelayed(0, 200);

	}

	/**
	 * 选择的Column里面的Tab
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		// 判断是否选中
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		String mobDate = week[arg0];
		MobclickAgent.onEvent(this, "riqi", mobDate);
		selectTab(arg0);
	}

	private int today = -1;
	// 表示发出预约请求的listView和项目所在位置
	private int remindPosition;

	private void addRemind(int userId, int programId) {
		showLoadingLayout();
		VolleyUserRequest.addRemind(userId, programId, this, this);
	}

	private void onRemindAdded(int remindPosition, int remindId) {
		CpData cpData = channelDetailAdapter.getItem(remindPosition);
		cpData.order = 1;
		cpData.remindId = remindId;
		channelDetailAdapter.notifyDataSetChanged();
	}

	private void deleteRemind(int userId, int cpId) {
		showLoadingLayout();
		VolleyUserRequest
				.deleteRemind(userId, String.valueOf(cpId), this, this);
	}

	private void onRemindDeleted(int remindPosition) throws Exception {
		CpData cpData = channelDetailAdapter.getItem(remindPosition);
		cpData.order = 0;
		channelDetailAdapter.notifyDataSetChanged();
	}

	private ChannelDetailAdapter channelDetailAdapter;

	public void setChannelDetailAdapter(
			ChannelDetailAdapter channelDetailAdapter) {
		this.channelDetailAdapter = channelDetailAdapter;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pic:
		case R.id.info_layout:
		case R.id.liveBtn:
			liveThroughNet(
					channelData.netPlayDatas,
					TextUtils.isEmpty(programId) ? 0 : Integer
							.parseInt(programId), channelId, channelData);
			break;
		case R.id.item_liveBtn:
			onLiveBtnClick((Integer) v.getTag(R.id.item_pos),
					(Boolean) v.getTag(R.id.item_bool));
			break;
		default:
			break;
		}
	}

	public ChannelData channelData;

	public void onLiveBtnClick(int position, boolean liveFlag) {
		CpData cpData = channelDetailAdapter.getItem(position);
		programId = cpData.programId;
		if (liveFlag) {
			liveThroughNet(channelData.netPlayDatas,
					Integer.parseInt(programId), channelId, channelData);
		} else {
			// 预约或者取消预约
			if (UserNow.current().userID == 0) {
				startActivityForResult(new Intent(this, LoginActivity.class),
						requestCode);
			} else {
				if (cpData.order == 1) {
					remindPosition = position;
					deleteRemind(UserNow.current().userID,
							(int) cpData.remindId);
				} else {
					remindPosition = position;
					addRemind(UserNow.current().userID, cpData.id);
				}
			}
		}
	}

	@Override
	public void deleteRemindResult(int errCode) {
		hideLoadingLayout();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_ERROR:
			DialogUtil.alertToast(getApplicationContext(), "取消预约失败!");
			break;
		case JSONMessageType.SERVER_CODE_OK:
			try {
				DialogUtil.alertToast(getApplicationContext(), "取消预约成功!");
				onRemindDeleted(remindPosition);
				DataSupport.delete(Remind.class,
						channelDetailAdapter.getItem(remindPosition).id);
				remindPosition = -1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void addRemindResult(int errCode, String msg, int remindId) {
		hideLoadingLayout();
		switch (errCode) {
		case JSONMessageType.SERVER_CODE_OK:
			DialogUtil.alertToast(getApplicationContext(), "预约成功!");
			onRemindAdded(remindPosition, remindId);
			VodProgramData program = cpToProgram();
			Remind.get(program).save();
			Intent alertServcice = new Intent(this, LiveAlertService.class);
			startService(alertServcice);
			remindPosition = -1;
			break;
		default:
			DialogUtil.alertToast(getApplicationContext(),
					TextUtils.isEmpty(msg) ? "预约失败" : msg);
			break;
		}
	}

	private VodProgramData cpToProgram() {
		CpData cp = channelDetailAdapter.getItem(remindPosition);
		VodProgramData program = new VodProgramData();
		program.cpId = cp.id;
		program.channelId = String.valueOf(channelId);
		program.cpName = cp.name;
		program.channelName = channelName;
		program.channelLogo = picUrl;
		int item = mViewPager.getCurrentItem();
		program.cpDate = ((ChannelDetailFragment) mFragmentAdapter
				.getItem(item)).getDate(item);
		program.startTime = cp.startTime;
		return program;
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.remindAdd);
		VolleyHelper.cancelRequest(Constants.remindDelete);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent arg2) {
		if (result == RESULT_OK) {
			if (request == requestCode) {
				for (int i = 0; i < fragments.size(); i++) {
					ChannelDetailFragment fragment = (ChannelDetailFragment) fragments
							.get(i);
					if (i == mViewPager.getCurrentItem()) {
						fragment.reloadData();
					} else if (!fragment.needLoad) {
						fragment.needLoad = true;
					}
				}
			}
		}
		super.onActivityResult(request, result, arg2);
	}
}
