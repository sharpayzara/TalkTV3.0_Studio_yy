package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.AwesomeAdapter;
import com.sumavision.talktv2.adapter.EmergencyAdapter;
import com.sumavision.talktv2.bean.EmergencyFocusData;
import com.sumavision.talktv2.bean.EmergencyObjectData;
import com.sumavision.talktv2.bean.EmergencyZoneData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnEmergencyZoneListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 专区首页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class EmergencyStoryActivity extends BaseActivity implements
		OnEmergencyZoneListener {
	private ListView wangyi_list;
	private EmergencyAdapter wAdapter;

	private ViewPager picViewPager;
	private LinearLayout picStarsLayout;
	private RelativeLayout picLayout;
	private TextView picTitleTextView;
	private final int AUTO_CHANGE = 10;
	public final int AUTO_CHANGE_DURATION = 6000;
	private int bigPicSize = 0;
	// 焦点图当前位置
	public int currentSelection;
	private int currentSelectionLocal;
	// 焦点图数量
	public int recommandPicSize;

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case AUTO_CHANGE:
				try {
					performAutoChange();
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				removeMessages(AUTO_CHANGE);
				sendEmptyMessageDelayed(AUTO_CHANGE, AUTO_CHANGE_DURATION);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (TextUtils.isEmpty(title)) {
			getSupportActionBar().setTitle("专区");
		} else {
			getSupportActionBar().setTitle(title);
		}
		setContentView(R.layout.activity_emergency_story);
		gerExtra();
		initView();
		showLoadingLayout();
		VolleyProgramRequest.emergencyZone(this, this, 0);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("EmergencyStoryActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("EmergencyStoryActivity");
		super.onResume();
	}

	private String title;

	private void gerExtra() {
		Intent intent = getIntent();
		if (intent.hasExtra("zoneId"))
			UserNow.current().zoneId = intent.getLongExtra("zoneId", 0);
		title = intent.getStringExtra("title");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		picViewPager.getParent().requestDisallowInterceptTouchEvent(true);
		return super.dispatchTouchEvent(ev);
	}

	private void initView() {

		wangyi_list = (ListView) findViewById(R.id.wangyi_list);
		LayoutInflater inflate = LayoutInflater.from(this);
		View headerView;
		if (AppUtil.isPad(this)) {
			headerView = inflate.inflate(R.layout.header_recommend_pad, null);
		} else {
			headerView = inflate.inflate(R.layout.header_recommend_v11, null);
		}
		wangyi_list.setOnTouchListener(touched);
		wangyi_list.addHeaderView(headerView);

		picViewPager = (ViewPager) headerView.findViewById(R.id.pic_view);
		picStarsLayout = (LinearLayout) headerView.findViewById(R.id.pic_star);
		picStarsLayout.setClickable(true);
		picLayout = (RelativeLayout) headerView
				.findViewById(R.id.pic_view_layout);
		picLayout.setClickable(true);
		picTitleTextView = (TextView) headerView.findViewById(R.id.pic_title);
		picTitleTextView.setClickable(true);
		initLoadingLayout();

		PreferencesUtils.putBoolean(this, null,
				"isUseBaiduPlayerNowNeedReopenBaseFile", false);

	}

	private EmergencyZoneData emergencyZoneData;

	private OnTouchListener touched = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.pic_view) {
				picViewPager.dispatchTouchEvent(event);
				return true;
			} else {
				return false;
			}
		}
	};
	OnPageChangeListener focusPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			currentSelectionLocal = arg0;
			currentSelection = arg0;
			try {
				onPicSelected(arg0);
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				try {
					onPicSelected(arg0);
				} catch (NullPointerException e1) {
					e1.printStackTrace();
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			// 滑动中
			case 1:
				handler.removeMessages(AUTO_CHANGE);
				break;
			// 停止
			case 2:
				handler.removeMessages(AUTO_CHANGE);
				handler.sendEmptyMessageDelayed(AUTO_CHANGE,
						AUTO_CHANGE_DURATION);
				break;
			// 空状态
			default:
				break;
			}

		}
	};

	private void onPicSelected(int position) {
		int size = list.size();
		recommandPicSize = size;
		for (int i = 0; i < size; i++) {
			ImageView imageView = (ImageView) picStarsLayout.findViewWithTag(i)
					.findViewById(R.id.imageView);
			if (i == position) {
				imageView.setImageResource(R.drawable.rcmd_pic_star_focus);
			} else {
				imageView.setImageDrawable(null);
			}
		}
		picTitleTextView.setText(list.get(position).focusName);
	}

	List<EmergencyFocusData> list;
	List<EmergencyObjectData> listobject = new ArrayList<EmergencyObjectData>();

	private void updateEmergencyList() {
		list = emergencyZoneData.emergencyFocus;
		listobject = emergencyZoneData.emergencyObject;
		wAdapter = new EmergencyAdapter(this, listobject);
		picViewPager.setOnPageChangeListener(focusPageChangeListener);
		wangyi_list.setVisibility(View.VISIBLE);
		wangyi_list.setDividerHeight(0);
		wangyi_list.setAdapter(wAdapter);
		if (list != null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			ArrayList<View> listViews = new ArrayList<View>();
			for (int i = 0; i < list.size(); i++) {
				View convertView = inflater.inflate(R.layout.rcmd_pic_item,
						null);
				final ImageView imageView = (ImageView) convertView
						.findViewById(R.id.imageView);
				imageView.setTag(i);
				String picUrl = Constants.picUrlFor + list.get(i).focuspic
						+ "b.jpg";
				loadImage(imageView, picUrl, R.drawable.recommend_pic_default);
				imageView.setOnClickListener(onClickListener);
				listViews.add(convertView);
			}
			AwesomeAdapter adapter = new AwesomeAdapter(listViews);
			picViewPager.setAdapter(adapter);
			bigPicSize = list.size();
			if (bigPicSize > 0) {
				picStarsLayout.removeAllViews();
			}
			for (int i = 0; i < bigPicSize; i++) {
				FrameLayout frame = (FrameLayout) inflater.inflate(
						R.layout.rcmd_pic_star, null);
				frame.setTag(i);
				if (i == 0) {
					((ImageView) frame.findViewById(R.id.imageView))
							.setImageResource(R.drawable.rcmd_pic_star_focus);
				}
				picStarsLayout.addView(frame);
			}
			picTitleTextView.setText(list.get(0).focusName);
		}
		handler.removeMessages(AUTO_CHANGE);
		handler.sendEmptyMessageDelayed(AUTO_CHANGE, AUTO_CHANGE_DURATION);
	}

	private void performAutoChange() {
		if (currentSelectionLocal == list.size() - 1) {
			currentSelectionLocal = 0;
		} else {
			currentSelectionLocal += 1;
		}
		picViewPager.setCurrentItem(currentSelectionLocal);
	}

	public int firstVisibleItemPosition = 0;
	// 焦点图点击事件
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			int type = 0;
			try {
				type = list.get(position).focustype;
			} catch (NullPointerException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
				return;
			}
			Intent intent;
			switch (type) {
			case 1: // 新闻 跳转
			case 2: // 视频跳转
				intent = new Intent(EmergencyStoryActivity.this,
						EmergencyDetailActivity.class);
				intent.putExtra("objectId", list.get(position).focusObjectId);
				intent.putExtra("type", list.get(position).focustype);
				intent.putExtra("zoneId", UserNow.current().zoneId);
				intent.putExtra("way", 0);
				startActivity(intent);
				break;
			case 3:// 演员跳转
				intent = new Intent(EmergencyStoryActivity.this,
						StarDetailActivity.class);
				intent.putExtra("starId", list.get(position).focusObjectId);
				startActivity(intent);
				break;
			case 4:// 花絮跳转
				intent = new Intent(EmergencyStoryActivity.this,
						ShowImageActivity.class);
				intent.putExtra("objectId", list.get(position).focusObjectId);
				intent.putExtra("type", list.get(position).focustype);
				intent.putExtra("zoneId", UserNow.current().zoneId);
				intent.putExtra("way", 0);
				startActivity(intent);
				break;
			case 5:// 活动跳转
				intent = new Intent(EmergencyStoryActivity.this,
						ActivityActivity.class);
				intent.putExtra("activityId", list.get(position).focusObjectId);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	};

	protected void reloadData() {
		showLoadingLayout();
		VolleyProgramRequest.emergencyZone(this, this, 0);
	};

	@Override
	public void getEmergencyZone(int errCode, EmergencyZoneData emergencyZone) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			emergencyZoneData = emergencyZone;
			hideLoadingLayout();
			updateEmergencyList();
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.zoneDetail);
	}

}
