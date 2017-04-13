package com.sumavision.talktv2.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.StarProgramAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.StarData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.components.ListViewHeight;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.listener.OnStarDetailListener;
import com.sumavision.talktv2.http.request.VolleyProgramRequest;
import com.sumavision.talktv2.utils.CommonUtils;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * @author jianghao
 * @version 2.0
 * @description明星详情
 * @changeLog
 */
public class StarDetailActivity extends BaseActivity implements
		OnClickListener, OnStarDetailListener, OnItemClickListener {
	private int starId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getSupportActionBar().setTitle(R.string.navigator_stardetail);
		starId = getIntent().getIntExtra("starId", 0);
		setContentView(R.layout.activity_star_detail);
		initViews();
		setListeners();
		getStarDetail();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("StarDetailActivity");
		super.onPause();
	}

	private TextView errText;
	private ProgressBar progressBar;
	private ScrollView scrollView;
	private TextView nameText;
	private TextView englishNameText;
	private TextView starTypeText;
	private TextView hobbyText;
	private TextView introText;

	private ImageView headPicImageView;

	private LinearLayout gallery;
	// 演员相关剧集里列表
	private ListView programList;
	// 演员简介是否展开
	private boolean isOpened = false;

	private void initViews() {
		errText = (TextView) findViewById(R.id.err_text);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		scrollView = (ScrollView) findViewById(R.id.scollView);

		nameText = (TextView) findViewById(R.id.star_name);
		englishNameText = (TextView) findViewById(R.id.star_name_eng);
		starTypeText = (TextView) findViewById(R.id.star_startype);
		hobbyText = (TextView) findViewById(R.id.star_hobby);

		introText = (TextView) findViewById(R.id.star_intro);

		headPicImageView = (ImageView) findViewById(R.id.star_img);
		gallery = (LinearLayout) findViewById(R.id.sd_pic_gallery_small);
		programList = (ListView) findViewById(R.id.star_programs);
		programList.setSelector(R.color.transparent);

	}

	private void setListeners() {
		errText.setOnClickListener(this);
		introText.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.star_intro:
			if (!isOpened) {
				introText.setMaxLines(400);
			} else {
				introText.setMaxLines(4);
			}
			isOpened = !isOpened;
			break;
		case R.id.sd_gallery_img_small:
			int position = (Integer) arg0.getTag();
			String[] temp = starData.photo;
			String url = temp[position];
			url = url.replace("s.", "b.");
			openShowImageActivity(url, temp, position);
			break;
		case R.id.err_text:
			getStarDetail();
			break;
		default:
			break;
		}
	}

	private boolean hasData;

	private void getStarDetail() {
		VolleyProgramRequest.getStarDetail(this, starId, this);
		if (!hasData) {
			progressBar.setVisibility(View.VISIBLE);
			errText.setVisibility(View.GONE);
		}
	}

	private void updateUI() {
		progressBar.setVisibility(View.GONE);
		errText.setVisibility(View.GONE);
		scrollView.setVisibility(View.VISIBLE);
		initStarInfo("中文名: ", starData.name, nameText);
		initStarInfo("英文名: ", starData.nameEng, englishNameText);
		initStarInfo("星座: ", starData.starType, starTypeText);
		initStarInfo("爱好: ", starData.hobby, hobbyText);
		if (!TextUtils.isEmpty(starData.intro)) {
			introText.setText(starData.intro);
		}
		loadImage(headPicImageView, starData.photoBig_V,
				R.drawable.list_star_default);
		String[] temp = starData.photo;
		if (temp != null) {
			LayoutInflater inflater = LayoutInflater.from(this);
			for (int i = 0; i < temp.length; i++) {
				View rowView = inflater.inflate(R.layout.star_gallery_item,
						null);
				ImageView imageView = (ImageView) rowView
						.findViewById(R.id.sd_gallery_img_small);
				loadImage(imageView, temp[i], R.drawable.list_star_default);
				imageView.setTag(i);
				imageView.setOnClickListener(this);
				LinearLayout.LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = 8;
				gallery.addView(rowView, params);
			}
		}

		ArrayList<VodProgramData> listp = starData.program;
		if (listp != null && listp.size() != 0) {
			programList.setAdapter(new StarProgramAdapter(this, listp));
			programList.setOnItemClickListener(this);
			ListViewHeight.setListViewHeightBasedOnChildren(programList);
		}
	}

	private void initStarInfo(String pref, String name, TextView txt) {
		if (!TextUtils.isEmpty(name)) {
			StringBuffer strbuf = new StringBuffer(pref);
			strbuf.append(name);
			int firstIndex = 0;
			int lastIndex = pref.length() - 1;
			SpannableString spannableString = CommonUtils.getSpannableString(
					strbuf.toString(),
					firstIndex,
					lastIndex,
					new ForegroundColorSpan(getResources().getColor(
							R.color.light_black)));
			txt.setText(spannableString);
		}
	}

	private void openProgramDetailActivity(String id, String topicId) {
		Intent intent = new Intent(this, PlayerActivity.class);
		long programId = Long.valueOf(id);
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ArrayList<VodProgramData> listp = starData.program;
		String tvId = listp.get(position).id;
		String tvTopicId = listp.get(position).topicId;
		openProgramDetailActivity(tvId, tvTopicId);
	}

	private void openShowImageActivity(String url, String[] pics, int pos) {
		Intent intent = new Intent(this, ShowImageActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("pics", pics);
		intent.putExtra("pos", pos);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("StarDetailActivity");
		super.onResume();
		scrollView.smoothScrollTo(-2000, -2000);
	}

	StarData starData = new StarData();

	@Override
	public void getStatDetail(int errCode, StarData starData) {
		if (errCode == JSONMessageType.SERVER_CODE_OK) {
			this.starData = starData;
			updateUI();
			hasData = true;
		} else {
			progressBar.setVisibility(View.GONE);
			errText.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void finish() {
		super.finish();
		VolleyHelper.cancelRequest(Constants.starDetail);
	}
}
