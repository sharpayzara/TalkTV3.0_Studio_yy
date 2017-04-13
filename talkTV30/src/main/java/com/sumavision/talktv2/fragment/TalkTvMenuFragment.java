package com.sumavision.talktv2.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.FeedbackMailActivity;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.MessageListActivity;
import com.sumavision.talktv2.activity.MyAccountActivity;
import com.sumavision.talktv2.activity.MyCacheActivity;
import com.sumavision.talktv2.activity.MyFavActivity;
import com.sumavision.talktv2.activity.MyVipPrivilegeActivity;
import com.sumavision.talktv2.activity.PlayHistoryActivity;
import com.sumavision.talktv2.activity.SettingActivity;
import com.sumavision.talktv2.activity.StatementActivity;
import com.sumavision.talktv2.activity.UserCenterActivity;
import com.sumavision.talktv2.activity.UserFeedbackActivity;
import com.sumavision.talktv2.activity.UserInfoEditActivity;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.eventbus.UserInfoEvent;
import com.sumavision.talktv2.service.TvBaiduPushMessageReceiver;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.widget.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 侧滑菜单
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class TalkTvMenuFragment extends BaseFragment implements OnClickListener {
	public static TalkTvMenuFragment newInstance() {
		TalkTvMenuFragment fragment = new TalkTvMenuFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("resId", R.layout.fragment_menu);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	ImageView vipMark;
	private ImageView userHeaderView;
	private ImageView userGenderView;
	private TextView userPointTxt;
	private TextView userNameTxt;
	private TextView userLevelTxt;
	private TextView userDiamondTxt;
	private TextView unloginTxt;
	private LinearLayout userOtherInfoView;
	private CircleImageView mailMsgTip, favMsgTip, cacheMsgTip, centerMsgTip;
	RelativeLayout vipLayout;
	ImageView vipLine;

	@Override
	protected void initViews(View view) {
		userHeaderView = (ImageView) view.findViewById(R.id.user_header);
		userHeaderView.setImageResource(R.drawable.my_headpic);
		userGenderView = (ImageView) view.findViewById(R.id.user_gender);
		userPointTxt = (TextView) view.findViewById(R.id.user_gold);
		userDiamondTxt = (TextView) view.findViewById(R.id.user_diamond);
		userLevelTxt = (TextView) view.findViewById(R.id.user_level);
		userNameTxt = (TextView) view.findViewById(R.id.user_name);
		unloginTxt = (TextView) view.findViewById(R.id.unlogin);
		vipLayout = (RelativeLayout) view.findViewById(R.id.rlayout_vip);
		vipLine = (ImageView) view.findViewById(R.id.temp_vip_line);
//		if (UserNow.current().isVip){
//			vipLayout.setVisibility(View.VISIBLE);
//			vipLine.setVisibility(View.VISIBLE);
//		} else {
//			vipLayout.setVisibility(View.GONE);
//			vipLine.setVisibility(View.GONE);
//		}
		view.findViewById(R.id.tv_history).setOnClickListener(this);
		view.findViewById(R.id.rlayout_cache).setOnClickListener(this);
		view.findViewById(R.id.rlayout_msg).setOnClickListener(this);
		view.findViewById(R.id.tv_feedback).setOnClickListener(this);
		view.findViewById(R.id.rlayout_fav).setOnClickListener(this);
		view.findViewById(R.id.tv_settings).setOnClickListener(this);
		view.findViewById(R.id.rlayout_space).setOnClickListener(this);
		view.findViewById(R.id.layout_statement).setOnClickListener(this);
		view.findViewById(R.id.rlayout_vip).setOnClickListener(this);
		view.findViewById(R.id.user_level).setOnClickListener(this);
		userOtherInfoView = (LinearLayout) view.findViewById(R.id.user_other);
		userOtherInfoView.setOnClickListener(this);
		view.findViewById(R.id.user_header).setOnClickListener(this);
		((TextView) (view.findViewById(R.id.tv_version))).setText("v"
				+ AppUtil.getAppVersionId(mActivity));
		mailMsgTip = (CircleImageView) view.findViewById(R.id.imgv_msg_tip);
		favMsgTip = (CircleImageView) view.findViewById(R.id.imgv_fb_tip);
		cacheMsgTip = (CircleImageView) view.findViewById(R.id.imgv_cache_tip);
		centerMsgTip = (CircleImageView) view.findViewById(R.id.imgv_space_tip);
		vipMark = (ImageView) view.findViewById(R.id.user_vip_mark);

		if (UserNow.current().userID > 0) {
			onEvent(null);
			changeMsgTipView(Constants.key_privateMsg, mailMsgTip);
			changeMsgTipView(Constants.key_favourite, favMsgTip);
			changeMsgTipView(Constants.KEY_USER_CENTER, centerMsgTip);
		}
		changeMsgTipView(Constants.KEY_CACHE, cacheMsgTip);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_header:
			if (UserNow.current().userID > 0) {
				MobclickAgent.onEvent(getActivity(), "zhanghaoshezhi");
				Intent intent = new Intent(mActivity,
						UserInfoEditActivity.class);
				startActivity(intent);
			} else {
				MobclickAgent.onEvent(getActivity(), "wodedenglu");
				startActivity(new Intent(mActivity, LoginActivity.class));
			}
			break;
		case R.id.user_other:
			startActivity(new Intent(mActivity, MyAccountActivity.class));
			break;
		case R.id.rlayout_msg:
			mailMsgTip.setVisibility(View.GONE);
			MobclickAgent.onEvent(getActivity(), "Chwodexiaoxi");
			if (UserNow.current().userID > 0) {
				startActivity(new Intent(mActivity, MessageListActivity.class));
			} else {
				startActivity(new Intent(mActivity, LoginActivity.class));
			}
			break;
		case R.id.rlayout_cache:
			cacheMsgTip.setVisibility(View.GONE);
			MobclickAgent.onEvent(getActivity(), "Chlixianhuancun");
			startActivity(new Intent(mActivity, MyCacheActivity.class));
			break;
		case R.id.rlayout_fav:
			favMsgTip.setVisibility(View.GONE);
			MobclickAgent.onEvent(getActivity(), "Chwodeshoucang");
			if (UserNow.current().userID > 0) {
				startActivity(new Intent(mActivity, MyFavActivity.class));
			} else {
				startActivity(new Intent(mActivity, LoginActivity.class));
			}
			break;
		case R.id.rlayout_space:
			centerMsgTip.setVisibility(View.GONE);
			MobclickAgent.onEvent(getActivity(), "Chwodekongjian");
			if (UserNow.current().userID > 0) {
				PreferencesUtils
						.putBoolean(getActivity(), Constants.pushMessage,
								Constants.KEY_USER_CENTER, false);
				Intent spaceIntent = new Intent(mActivity,
						UserCenterActivity.class);
				spaceIntent.putExtra("id", UserNow.current().userID);
				startActivity(spaceIntent);
			} else {
				startActivity(new Intent(mActivity, LoginActivity.class));
			}
			break;
		case R.id.tv_history:
			MobclickAgent.onEvent(getActivity(), "Chbofanglishi");
			startActivity(new Intent(mActivity, PlayHistoryActivity.class));
			break;
		case R.id.tv_feedback:
			MobclickAgent.onEvent(getActivity(), "Chyoujiangfankui");
			startActivity(new Intent(mActivity, FeedbackMailActivity.class));
//			startActivity(new Intent(mActivity, UserFeedbackActivity.class));
			break;
		case R.id.tv_settings:
			MobclickAgent.onEvent(getActivity(), "chshezhi");
			startActivity(new Intent(mActivity, SettingActivity.class));
			break;
		case R.id.layout_statement:
			Intent intent = new Intent(mActivity, StatementActivity.class);
			startActivity(intent);
			break;
		case R.id.rlayout_vip:
			if (UserNow.current().userID>0){
				MobclickAgent.onEvent(getActivity(),"chmyvip");
				startActivity(new Intent(mActivity, MyVipPrivilegeActivity.class));
			} else {
				startActivity(new Intent(mActivity, LoginActivity.class));
			}
			break;
		case R.id.user_level:
//			String temp = "{\"push\":{\"discovery\":{\"objectId\":78,\"objectType\":2},\"text\":\"商城ts\"}}";
//			temp = "{\"push\":{\"title\":\"直播\",\"text\":\"CCTV5\",\"channel\":{\"id\":14,\"name\":\"CCTV-5\",\"play\":[{\"url\":\"pa://cctv_p2p_hdcctv5\",\"videoPath\":\"\",\"channelIdStr\":\"tvfan_cctv1\"},{\"url\":\"Letv://cctv5_1300\",\"videoPath\":\"\",\"showUrl\":\"\"},{\"url\":\"\",\"videoPath\":\"\",\"showUrl\":\"http://cctv5.cntv.cn/\",\"channelIdStr\":\"cctv5\"},{\"url\":\"\",\"videoPath\":\"http://60.12.207.11:8000/live/flv/channel212\"},{\"url\":\"\",\"videoPath\":\"http://60.12.207.136:8000/live/flv/channel214\",\"channelIdStr\":\"cctv5\"},{\"url\":\"http://m.wasu.cn/content,freewap,RDUCHS-NB3G-IPS-04142898201890759,195432,38,6609317.page?profile=wasuClientH5_cj\",\"videoPath\":\"\"},{\"url\":\"\",\"videoPath\":\"http://tvie03.ucatv.com.cn/channels/xjyx/CCTV5-Suma/m3u8:300k_flv\"},{\"videoPath\":\"http://60.12.207.11:8000/live/flv/channel212\"},{\"videoPath\":\"http://58.135.196.138:8090/live/6b9e3889ec6e2ab1a8c7bd0845e5368a/index.m3u8 \n" +
//					"\n" +
//					"\"}]}}}\n";
//			new TvBaiduPushMessageReceiver().parseMessage(getActivity(),temp);
			break;
		default:
			break;
		}
	}

	public void onEvent(UserInfoEvent event) {
		if (UserNow.current().userID > 0) {
			handler.sendEmptyMessage(1);
		} else {
			handler.sendEmptyMessage(2);
		}
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					String url = UserNow.current().iconURL;
					loadImage(userHeaderView, url, R.drawable.list_headpic_default);
					unloginTxt.setVisibility(View.GONE);
					userOtherInfoView.setVisibility(View.VISIBLE);
					userNameTxt.setText(UserNow.current().name);
					userPointTxt.setText(String.valueOf(UserNow.current().totalPoint));
					userDiamondTxt.setText(String.valueOf(UserNow.current().diamond));
					if (UserNow.current().gender == 1) {
						userGenderView.setImageResource(R.drawable.uc_male_selected);
					} else {
						userGenderView.setImageResource(R.drawable.uc_female_selected);
					}
					userLevelTxt.setText("LV " + UserNow.current().level);
					if (UserNow.current().isVip){
						vipMark.setVisibility(View.VISIBLE);
//						vipLayout.setVisibility(View.VISIBLE);
//						vipLine.setVisibility(View.VISIBLE);
					} else {
						vipMark.setVisibility(View.GONE);
//						vipLayout.setVisibility(View.GONE);
//						vipLine.setVisibility(View.GONE);
					}
					break;
				case 2:
					userGenderView.setImageResource(R.drawable.transparent_background);
					userHeaderView.setImageResource(R.drawable.my_headpic);
					userNameTxt.setText(R.string.user_info_my_account);
					userLevelTxt.setText("");
					unloginTxt.setVisibility(View.VISIBLE);
					userOtherInfoView.setVisibility(View.GONE);
					vipMark.setVisibility(View.GONE);
//					vipLayout.setVisibility(View.GONE);
					vipLine.setVisibility(View.GONE);
					break;
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void reloadData() {

	}

	public void changeTip(String key) {
		if (Constants.key_privateMsg.equals(key)) {
			if (UserNow.current().userID > 0) {
				changeMsgTipView(Constants.key_privateMsg, mailMsgTip);
			}
		} else if (Constants.key_favourite.equals(key)) {
			if (UserNow.current().userID > 0) {
				changeMsgTipView(Constants.key_favourite, favMsgTip);
			}
		} else if (Constants.KEY_CACHE.equals(key)) {
			changeMsgTipView(Constants.KEY_CACHE, cacheMsgTip);
		} else if (Constants.KEY_USER_CENTER.equals(key)) {
			if (UserNow.current().userID > 0) {
				changeMsgTipView(Constants.KEY_USER_CENTER, centerMsgTip);
			}
		}
	}

	private void changeMsgTipView(String key, View view) {
		if (view != null) {
			if (PreferencesUtils.getBoolean(getActivity(),
					Constants.pushMessage, key)) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
