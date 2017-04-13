package com.sumavision.talktv.videoplayer.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.talktv.videoplayer.R;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv.videoplayer.utils.ImageLoaderHelper;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.NetPlayData;

import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * @author李伟
 * @version
 * @createTime
 * @description web浏览器版权规避界面：无真实url即无需破解
 * @changeLog
 */
public class WebAvoidPicActivity extends CompatBaseActivity {
	public final static String[] images_pad = {
			"http://tvfan.cn/resource/channelUrl/pad_1.png",
			"http://tvfan.cn/resource/channelUrl/pad_2.png",
			"http://tvfan.cn/resource/channelUrl/pad_3.png",
			"http://tvfan.cn/resource/channelUrl/pad_4.png" };
	public final static String[] images = {
			"http://tvfan.cn/resource/channelUrl/1.png",
			"http://tvfan.cn/resource/channelUrl/2.png",
			"http://tvfan.cn/resource/channelUrl/3.png",
			"http://tvfan.cn/resource/channelUrl/4.png" };
	private String url = "";
	private String title = "节目播放";
	private TextView urlold;
	private ImageView urlbtn;
	private String videoPath = "";
	private ImageView avoidImage;
	int playType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webavoidpic);

		EventBus.getDefault().removeAllStickyEvents();
		int subid = getIntent().getIntExtra("subid", 0);
		int programId = getIntent().getIntExtra("id", 0);
		String path = getIntent().getStringExtra("path");
		playType = getIntent().getIntExtra("playType", 0);
		boolean isBackLook = getIntent().getBooleanExtra("isBackLook", false);
		Log.i("PlayerActivity", "webavoidpic playtype:" + playType);
		boolean isHalf = getIntent().getBooleanExtra("isHalf", false);
		if (!isHalf && (playType == PlayerActivity.VOD_PLAY || isBackLook)) {
			startPreLoading(path, programId, subid, false);
		}

		if (!getIntent().getBooleanExtra(PlayerActivity.INTENT_NEEDAVOID, true)) {
            if (playType ==PlayerActivity.LIVE_PLAY && getIntent().getBooleanExtra("toWeb",false)){

            }else {
                loadPlayerStatusForLocalPlay();
                return;
            }
		}
		getExtra();
		initView();
		showOrHideActionBar();
		if (titleLayout.isShown()) {
			refreshBtn.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(title)) {
			String subTitle = title;
			if (title.indexOf(":") > 0) {
				subTitle = title.substring(title.indexOf(":") + 1);
			} else if (title.indexOf("：") > 0) {
				subTitle = title.substring(title.indexOf("：") + 1);
			}
			if (getSupportActionBar() != null
					&& getSupportActionBar().isShowing()) {
				getSupportActionBar().setTitle(subTitle);
			} else {
				titleTextView.setText(subTitle);
			}
		}
		countDown = new PlayCountDown(3000, 1000);
		countDown.start();
		if (playType == PlayerActivity.LIVE_PLAY){
			countDown.cancel();
			urlbtn.setImageResource(R.drawable.webplaybtn1);
		} else {
			urlbtn.setImageResource(R.drawable.webplaybtn);
		}
	}

	private void getExtra() {
		if (getIntent().hasExtra("path"))
			videoPath = getIntent().getStringExtra("path");
		String name = getIntent().getStringExtra("programName");
		if (TextUtils.isEmpty(name)) {
			title = getIntent().getStringExtra("title");
		} else {
			title = name + " " + getIntent().getStringExtra("title");
		}
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			stopPreLoading();
			if (getIntent().getBooleanExtra("isHalf", false)){
				loadPlayerStatusForLocalPlay();
			}
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	PlayCountDown countDown;
	TextView tipText;
	private void initView() {
		avoidImage = (ImageView) findViewById(R.id.imageView);
		avoidImage.setOnClickListener(this);
		tipText = (TextView) findViewById(R.id.web_tip_text);
		ImageLoaderHelper loadHelper = new ImageLoaderHelper();
		Random random = new Random();
		int index = random.nextInt(4);
		if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			url = images_pad[index];
		} else {
			url = images[index];
		}

		urlold = (TextView) findViewById(R.id.urlold);
		urlbtn = (ImageView) findViewById(R.id.urlbtn);
		urlbtn.setOnClickListener(this);
		if (getIntent().getIntExtra("playType", 0) == PlayerActivity.LIVE_PLAY
				&& getIntent().hasExtra("NetPlayData")){
			ArrayList<NetPlayData> temp = (ArrayList<NetPlayData>) getIntent().getSerializableExtra("NetPlayData");
			if (temp != null && temp.size()>0){
				int pos = getIntent().getIntExtra("livepos",0);
				if (!TextUtils.isEmpty(temp.get(pos<temp.size()?pos:0).webPage)){
					url = temp.get(pos<temp.size()?pos:0).webPage;
				}
				if (!TextUtils.isEmpty(temp.get(pos<temp.size()?pos:0).showUrl)){
					urlold.setText(temp.get(pos<temp.size()?pos:0).showUrl);
				} else if(!TextUtils.isEmpty(temp.get(pos<temp.size()?pos:0).url)) {
					urlold.setText(temp.get(pos<temp.size()?pos:0).url);
				}else {
					urlold.setText(temp.get(pos<temp.size()?pos:0).videoPath);
				}
			}
		}else {
			urlold.setText(videoPath);
		}
		loadHelper.loadImage(avoidImage, url, R.drawable.aadefault,new ImageLoaderHelper.AnimateFirstDisplayListener(){
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				if (playType == PlayerActivity.LIVE_PLAY){
					avoidImage.setVisibility(View.VISIBLE);
					tipText.setVisibility(View.GONE);
				}
			}

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				super.onLoadingStarted(imageUri, view);
				if (playType == PlayerActivity.LIVE_PLAY){
					avoidImage.setVisibility(View.GONE);
					tipText.setVisibility(View.VISIBLE);
				}
			}
		});


	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.urlbtn || v.getId() == R.id.imageView) {
			countDown.cancel();
			countDown.onFinish();
		}
	}

	public void loadPlayerStatusForLocalPlay() {
		Intent intent = getIntent();
		int playType = intent.getIntExtra("playType", 0);
		boolean isHalf = intent.getBooleanExtra("isHalf", false);
		if (playType == PlayerActivity.LIVE_PLAY || (!isHalf&&!getIntent().getBooleanExtra("fromSource",false))) {
			intent.setClass(this, PlayerActivity.class);
				startActivity(intent);

		}
		if (playType ==2 && isHalf){
			setResult(RESULT_OK);
			EventBus.getDefault().post(new JiShuData());
		}
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		if (countDown != null) {
			countDown.cancel();
		}
	}

	class PlayCountDown extends CountDownTimer {

		public PlayCountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}

		@Override
		public void onFinish() {
			loadPlayerStatusForLocalPlay();
		}

	}

	private void startPreLoading(String url, int programId, int subId,
			boolean needParse) {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_START);
		intent.putExtra("url", url);
		intent.putExtra("programId", programId);
		intent.putExtra("subId", subId);
		intent.putExtra("needParse", needParse);
		startService(intent);
	}

	private void stopPreLoading() {
		Intent intent = new Intent(this, CachingWhilePlayingService.class);
		intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
				CachingWhilePlayingService.ACTION_PRE_LOADING_STOP);
		startService(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.ACTION_DOWN) {
			stopPreLoading();
		}
		return super.onKeyDown(keyCode, event);
	}
}
