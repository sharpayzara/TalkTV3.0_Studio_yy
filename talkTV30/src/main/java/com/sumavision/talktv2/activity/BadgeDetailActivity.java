package com.sumavision.talktv2.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.BadgeDetailData;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PlayNewData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnBadgeDetailListener;
import com.sumavision.talktv2.http.request.VolleyUserRequest;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hpb
 * @version 3.0
 * @description 徽章详情解析
 * @changeLog
 */
public class BadgeDetailActivity extends BaseActivity implements
		OnBadgeDetailListener {
	private int badgeId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_badge_detail);
		setContentView(R.layout.activity_badge_detail);
		badgeId = getIntent().getIntExtra("badgeId", 0);
		initViews();
		getData();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("BadgeDetailActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("BadgeDetailActivity");
		super.onPause();
	}

	/** 徽章图片 */
	private ImageView picView;
	/** 徽章获取人数 */
	private TextView personCountView;
	/** 徽章详细描述 */
	private TextView descriptionView;
	/** 内容布局 */
	private RelativeLayout contentView;

	private void initViews() {
		initLoadingLayout();
		picView = (ImageView) findViewById(R.id.pic);
		personCountView = (TextView) findViewById(R.id.person_count);
		descriptionView = (TextView) findViewById(R.id.description);
		contentView = (RelativeLayout) findViewById(R.id.content_Layout);
	}

	private void updateUI(BadgeDetailData badgeDetailData) {
		contentView.setVisibility(View.VISIBLE);
		String count = String.valueOf(badgeDetailData.getCount);
		getSupportActionBar().setTitle(badgeDetailData.name);
		String countStr = "总共" + count + "人领取";
		int firstIndex = 2;
		int lastIndex = countStr.indexOf("人");
		SpannableString spannableString = CommonUtils.getSpannableString(
				countStr, firstIndex, lastIndex, new ForegroundColorSpan(
						Color.RED));
		personCountView.setText(spannableString);

		String intro = badgeDetailData.intro;
		if (intro != null) {
			descriptionView.setText(intro);
		}

		String url = PlayNewData.current.pic;
		if (url != null) {
			loadImage(picView, url, R.drawable.list_star_default);
		}
	}

	private void getData() {
		showLoadingLayout();
		executeGetData(badgeId);
	}

	/**
	 * 
	 * @param badgeId
	 *            徽章ID
	 * @param tempData
	 *            临时数据存贮
	 */
	private void executeGetData(int badgeId) {
		VolleyUserRequest.getBadgeDetail(badgeId, this, this);
	}

	@Override
	public void getBadgeDetail(int errCode, BadgeDetailData badgeDetail) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			hideLoadingLayout();
			updateUI(badgeDetail);
		} else {
			showErrorLayout();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.badgeDetail);
	}
}
