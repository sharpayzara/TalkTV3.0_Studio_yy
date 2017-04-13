package com.sumavision.talktv2.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.ShowImageAdapter;
import com.sumavision.talktv2.bean.EmergencyDetailData;
import com.sumavision.talktv2.bean.EmergencyDetailPic;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.components.StepGallery;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnEmergencyDetailListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * @author hpb
 * @version 3.0
 * @description 图片浏览
 * @changeLog
 */
public class ShowImageActivity extends BaseActivity implements OnClickListener,
		OnEmergencyDetailListener, OnItemSelectedListener {

	private WindowManager mWindowManager;
	private TextView picIntro;
	private RelativeLayout commentFrame;
	private LinearLayout bottomFrame;
	private TextView commentTxt;
	private EmergencyDetailData emergencyDetailData;

	String[] pics;
	int bigPicPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.navigator_show_pic);
		setContentView(R.layout.activity_show_image);
		getExtra();
		initViews();
		errTextView.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ShowImageActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ShowImageActivity");
		super.onResume();
	}

	private TextView errTextView;
	private StepGallery gallery;
	private String[] picArr;

	private void initViews() {
		errTextView = (TextView) findViewById(R.id.err_text);
		gallery = (StepGallery) findViewById(R.id.imageView_gallery);
		picIntro = (TextView) findViewById(R.id.intro);
		bottomFrame = (LinearLayout) findViewById(R.id.bottom_frame);
		commentFrame = (RelativeLayout) findViewById(R.id.comment_frame);
		commentTxt = (TextView) findViewById(R.id.comment_txt);
		commentFrame.setOnClickListener(this);
		if (objectId > 0) {// 急诊室专区
			bottomFrame.setVisibility(View.VISIBLE);
			VolleyProgramRequest.emergencyDetail(this, this, objectId, zoneId,
					type, way);

		} else {
			bottomFrame.setVisibility(View.GONE);
			int l = pics.length;
			picArr = new String[l];
			for (int i = 0; i < l; i++) {
				picArr[i] = pics[i].replace("s.", "b.");
			}
			initGallery(bigPicPosition);
		}
		errTextView.setText("点击可返回");
	}

	private int type;
	private int way;
	private long objectId;
	private long zoneId;

	private void getExtra() {
		Intent intent = getIntent();
		pics = intent.getStringArrayExtra("pics");
		bigPicPosition = intent.getIntExtra("pos", 0);
		if (intent.hasExtra("objectId"))
			objectId = intent.getLongExtra("objectId", 0L);
		if (intent.hasExtra("zoneId"))
			zoneId = intent.getLongExtra("zoneId", 0L);
		if (intent.hasExtra("type"))
			type = intent.getIntExtra("type", 0);
		if (intent.hasExtra("way"))
			way = intent.getIntExtra("way", 0);
	}

	private void initGallery(int pos) {
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display defaultDisplay = mWindowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		defaultDisplay.getMetrics(metrics);
		Point point = new Point();
		point.x = metrics.widthPixels;
		point.y = metrics.heightPixels;
		ShowImageAdapter ga = new ShowImageAdapter(this, point, picArr);
//		defaultDisplay.getSize(point);
		gallery.setAdapter(ga);
		gallery.setSelection(pos);
		if (objectId > 0) {
			gallery.setOnItemSelectedListener(this);
		}
	}

	private int mPosition;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.err_text:
		case R.id.imageView:
			finish();
		case R.id.comment_frame:
			Intent intent = new Intent(this, CommentActivity.class);
			intent.putExtra("topicId", emergencyDetailData.emDetailTopicId);
			intent.putExtra("programName", "急诊室故事");
			startActivity(intent);
			break;
		}
	}

	@Override
	public void getEmergencyDetail(int errCode, EmergencyDetailData eDetaildata) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			emergencyDetailData = eDetaildata;
			commentTxt.setText("评论(" + emergencyDetailData.emDetailTalkCount
					+ ")");
			List<EmergencyDetailPic> picList = emergencyDetailData.emDetailPicList;
			picArr = new String[picList.size()];
			for (int i = 0; i < picList.size(); i++) {
				picArr[i] = Constants.picUrlFor + picList.get(i).emDetailPicPic
						+ "m.jpg";
			}
			initGallery(0);

		}

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		mPosition = position;
		picIntro.setText("("
				+ (position + 1)
				+ " / "
				+ emergencyDetailData.emDetailPicList.size()
				+ ")"
				+ emergencyDetailData.emDetailPicList.get(mPosition).emDetailPicContent);

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.newsDetail);
	}

}
