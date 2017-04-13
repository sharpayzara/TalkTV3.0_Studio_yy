package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.UserEventAdapter;
import com.sumavision.talktv2.bean.BadgeData;
import com.sumavision.talktv2.bean.EventData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.User;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.eventbus.GuanZhuUpdateEvent;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.UserEventParser;
import com.sumavision.talktv2.http.json.UserEventRequest;
import com.sumavision.talktv2.http.listener.OnAddGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnDeleteGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnOtherSpaceListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.DialogUtil;
import com.sumavision.talktv2.widget.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author jianghao 用户中心
 * 
 */
public class UserCenterActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener, OnOtherSpaceListener,
		OnAddGuanzhuListener, OnDeleteGuanzhuListener,
		OnSharedPreferenceChangeListener, OnRefreshListener2<ListView> {
	private int userId;
	private User userInfo;
	private ImageView headPic;
	private TextView userNameTextView;
	private TextView userLevelTextView;
	private TextView signatureTextView;
	private ImageView gender;
	private PullToRefreshListView eventListView;
	private TextView commentTextView, fansTextView, followTextView;

	SharedPreferences pushSp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usercenter);
		userInfo = new User();
		userId = getIntent().getIntExtra("id", 0);
		initView();
		if (userId == UserNow.current().userID) {
			getSupportActionBar().setTitle(R.string.my_space);
		} else {
			getSupportActionBar().setTitle(R.string.other_space);
		}
		EventBus.getDefault().register(this);
		pushSp = getSharedPreferences(Constants.pushMessage, 0);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UserCenterActivity");
		super.onResume();
		getUserInfo();
		pushSp.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("UserCenterActivity");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.user_center, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private CircleImageView fansMsgTip, commentMsgTip;
	private RelativeLayout otherLayout;
	private LinearLayout diamondLayout;
	private Button sendMsgBtn, followBtn;
	private TextView gold, diamond;
	private ImageView vipMark;

	private void initView() {
		initLoadingLayout();
		eventListView = (PullToRefreshListView) findViewById(R.id.listView);
		eventListView.setVisibility(View.GONE);
		View headerView = getLayoutInflater().inflate(
				R.layout.header_user_center, null);
		eventListView.getRefreshableView().addHeaderView(headerView);
		eventListView.setMode(Mode.PULL_FROM_END);
		eventAdapter = new UserEventAdapter(this, eventList);
		eventListView.setAdapter(eventAdapter);
		eventListView.setOnRefreshListener(this);
		eventListView.setPullToRefreshOverScrollEnabled(false);
		diamondLayout = (LinearLayout) findViewById(R.id.rlayout_diamond);
		otherLayout = (RelativeLayout) findViewById(R.id.rlayout_other);
		sendMsgBtn = (Button) findViewById(R.id.btn_send_msg);
		followBtn = (Button) findViewById(R.id.btn_follow);
		gold = (TextView) findViewById(R.id.user_gold);
		diamond = (TextView) findViewById(R.id.user_diamond);
		headPic = (ImageView) findViewById(R.id.user_header);
		vipMark = (ImageView) findViewById(R.id.user_vip_mark);
		userNameTextView = (TextView) findViewById(R.id.name);
		userLevelTextView = (TextView) findViewById(R.id.level);
		gender = (ImageView) findViewById(R.id.gender);
		signatureTextView = (TextView) findViewById(R.id.ou_signnature);

		fansTextView = (TextView) findViewById(R.id.tv_fans);
		followTextView = (TextView) findViewById(R.id.tv_follow);
		// searchTextView = (TextView) findViewById(R.id.tv_search);
		commentTextView = (TextView) findViewById(R.id.tv_comment);

		fansMsgTip = (CircleImageView) findViewById(R.id.imgv_fans_tip);
		commentMsgTip = (CircleImageView) findViewById(R.id.imgv_comment_tip);
		sendMsgBtn.setOnClickListener(this);
		followBtn.setOnClickListener(this);
		eventListView.setOnItemClickListener(this);
	}

	private boolean hasData;

	private void getUserInfo() {
		if (!hasData) {
			showLoadingLayout();
			VolleyUserRequest.getOtherSpace(userId, this, this);
		}
	}

	public void onEvent(GuanZhuUpdateEvent e) {
		VolleyUserRequest.getOtherSpace(userId, this, this);
	}

	ArrayList<EventData> eventList = new ArrayList<EventData>();
	UserEventAdapter eventAdapter;

	private void updateUI() {
		if (userId == UserNow.current().userID) {
			otherLayout.setVisibility(View.GONE);
			diamondLayout.setVisibility(View.VISIBLE);
			gold.setText("" + UserNow.current().totalPoint);
			diamond.setText("" + UserNow.current().diamond);
		} else {
			diamondLayout.setVisibility(View.GONE);
			otherLayout.setVisibility(View.VISIBLE);
		}
		String name = userInfo.name;
		if (name != null) {
			userNameTextView.setText(name);
		}
		String lvl = userInfo.level;
		if (lvl != null) {
			userLevelTextView.setText("Lv " + lvl);
		}
		String signature = userInfo.signature;
		if (signature != null && !signature.equals("")) {
			signatureTextView.setText(signature);
		} else {
			signatureTextView.setText("这个家伙什么也木有留下");
		}

		if (userInfo.gender == 2) {
			gender.setImageResource(R.drawable.uc_female_selected);
		}

		String fanCount = String.valueOf(userInfo.fansCount);
		fansTextView.setText(getString(R.string.my_function_fans) + "\n"
				+ fanCount);
		String followCount = String.valueOf(userInfo.friendCount);
		followTextView.setText(getString(R.string.my_function_fellow) + "\n"
				+ followCount);
		String comments = String.valueOf(userInfo.talkCount);
		commentTextView.setText(getString(R.string.my_function_comment) + "\n"
				+ comments);
		if (userId != UserNow.current().userID) {
			if (userInfo.isGuanzhu == 1) {
				followBtn.setText("取消关注");
			}
		}
		loadImage(headPic, userInfo.iconURL, R.drawable.list_star_default);
		if (userInfo.event != null) {
			eventList.clear();
			eventList.addAll(userInfo.event);
			eventAdapter.notifyDataSetChanged();
		}
		if (userInfo.eventCount == eventList.size() || eventList.size() == 0) {
			eventListView.setMode(Mode.DISABLED);
		}

		if (pushSp.getBoolean(Constants.key_user_comment, false)) {
			commentMsgTip.setVisibility(View.VISIBLE);
		}
		if (pushSp.getBoolean(Constants.key_fans, false)) {
			fansMsgTip.setVisibility(View.VISIBLE);
		}
		if (userInfo.isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
	}

	private void addGuanzhu() {
		showLoadingLayout();
		VolleyUserRequest.addGuanzhu(userId, this, this);
	}

	private void deleteGuanzhu() {
		showLoadingLayout();
		VolleyUserRequest.deleteGuanzhu(userId, this, this);
	}

	UserEventParser userEventParser = new UserEventParser();

	private void getUserEvents(int first, int count) {

		VolleyHelper.post(new UserEventRequest(userId, first, count).make(),
				new ParseListener(userEventParser) {

					@Override
					public void onParse(BaseJsonParser parser) {
						eventListView.onRefreshComplete();
						if (userEventParser.errCode == JSONMessageType.SERVER_CODE_OK) {
							eventList.addAll(userEventParser.list);
							userEventParser.list.clear();
							eventAdapter.notifyDataSetChanged();
							if (userInfo.eventCount == eventList.size()) {
								eventListView.setMode(Mode.DISABLED);
							}
						}

					}
				}, this);
	}

	@Override
	protected void reloadData() {
		getUserInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_follow:
			Intent i = new Intent(this, MyFellowingActivity.class);
			i.putExtra("id", userId);
			startActivity(i);
			break;
		case R.id.btn_send_msg:
			openPrivateMsgActivity(userInfo.userId, userInfo.name);
			break;
		case R.id.btn_follow:
			if (userInfo.isGuanzhu == 1) {
				deleteGuanzhu();
			} else {
				addGuanzhu();
			}
			break;
		case R.id.layout_fans:
			Intent intent = new Intent(this, FansActivity.class);
			intent.putExtra("id", userId);
			startActivity(intent);
			break;
		case R.id.layout_search:
			startActivity(new Intent(this, FriendSearchActivity.class));
			break;
		case R.id.layout_comment:
			openCommentActivity();
			break;
		default:
			break;
		}
	}

	private void openPrivateMsgActivity(int id, String name) {
		Intent intent = new Intent(this, UserMailActivity.class);
		intent.putExtra("otherUserId", id);
		intent.putExtra("otherUserName", name);
		startActivity(intent);
	}

	private void openCommentActivity() {
		Intent intent = new Intent(this, UserCommentActivity.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pushSp.unregisterOnSharedPreferenceChangeListener(this);
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (id >= 0) {
			EventData temp = eventList.get((int) id);
			switch (temp.toObjectType) {
			case 1: // program
				openProgramDetailActivity(String.valueOf(temp.toObjectId), "");
				break;
			case 9:
				openOtherUserCenterActivity(temp.toObjectId);
				break;
			default:
				break;
			}
		}
	}

	private void openProgramDetailActivity(String id, String topicId) {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(id);
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		startActivity(intent);
	}

	private void openOtherUserCenterActivity(int id) {
		Intent intent = new Intent(this, UserCenterActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	@Override
	public void getUserInfo(int errCode, User userInfo) {
		eventListView.onRefreshComplete();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
			this.userInfo = userInfo;
			eventListView.setVisibility(View.VISIBLE);
			updateUI();
			hasData = true;
		} else {
			hasData = false;
			eventListView.setVisibility(View.GONE);
			showErrorLayout();
		}

	}

	@Override
	public void deleteGuanzhuResult(int errCode) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(getApplication(), "取消关注成功");
			userInfo.isGuanzhu = 0;
			followBtn.setText("加关注");
			EventBus.getDefault().post(new GuanZhuUpdateEvent());
		} else {
			DialogUtil.alertToast(getApplication(), "取消关注失败");
		}

	}

	@Override
	public void addGuanzhuResult(int errCode, ArrayList<BadgeData> badgeList) {
		hideLoadingLayout();
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			DialogUtil.alertToast(getApplication(), "添加关注成功!");
			userInfo.isGuanzhu = 1;
			followBtn.setText("取消关注");
			EventBus.getDefault().post(new GuanZhuUpdateEvent());
		} else {
			DialogUtil.alertToast(getApplication(), "添加关注失败!");
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (userId == UserNow.current().userID) {
			if (Constants.key_user_comment.equals(key)) {
				changeMsgTipView(Constants.key_user_comment, commentMsgTip);
			} else if (Constants.key_fans.equals(key)) {
				changeMsgTipView(Constants.key_fans, fansMsgTip);
			}
		}
	}

	private void changeMsgTipView(String key, View view) {
		if (pushSp.getBoolean(key, false)) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getUserEvents(eventList.size(), 10);
	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.userSpace);
		VolleyHelper.cancelRequest(Constants.guanZhuAdd);
		VolleyHelper.cancelRequest(Constants.guanZhuCancel);
		VolleyHelper.cancelRequest(Constants.userEventList);
	}
}
