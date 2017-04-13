package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.UserNow;
import com.umeng.analytics.MobclickAgent;

public class UserLevelInfoActivity extends BaseActivity {
	private ImageView icon,vipMark;
	private TextView leftLevel,rightExp;
	private TextView levelText,levelCur,levelNext;
	private SeekBar levelSeek;
	private String[] leftArray,rightArray;
	int levelPos;
	int[] levelMax = {199,799,1999,1999,1999,2999,4999,5000};
	int[] levelMin = {0,200,1000,3000,5000,7000,10000,15000};
	private boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_level);
		getSupportActionBar().setTitle("用户等级");
		leftArray = getResources().getStringArray(R.array.level_name);
		rightArray = getResources().getStringArray(R.array.level_exprience);
		initView();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("UserLevel");
		super.onPause();
	}

	private void initView() {
		icon = (ImageView) findViewById(R.id.user_header);
		vipMark = (ImageView) findViewById(R.id.user_vip_mark);
		leftLevel = (TextView) findViewById(R.id.ul_intor1_text1);
		rightExp = (TextView) findViewById(R.id.ul_intor1_text2);
		levelText = (TextView) findViewById(R.id.level_text);
		loadImage(icon,UserNow.current().iconURL,R.drawable.list_headpic_default);
		if (UserNow.current().isVip){
			vipMark.setVisibility(View.VISIBLE);
		} else {
			vipMark.setVisibility(View.GONE);
		}
		levelPos = Integer.parseInt(UserNow.current().level) - 1;
		levelText.setText(leftArray[levelPos]);
		setLevelText();
		levelCur = (TextView) findViewById(R.id.level_cur);
		levelNext = (TextView) findViewById(R.id.level_next);
		levelSeek = (SeekBar) findViewById(R.id.level_seek);
		levelCur.setText("LV " + (levelPos + 1));
		levelNext.setText("LV " + (levelPos + 2));
		levelSeek.setMax(levelMax[levelPos]);
		levelSeek.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				return true;
			}
		});
		myProgress = UserNow.current().exp - levelMin[levelPos];
		incProgress = myProgress*50/1500 ;
		if (incProgress<1){
			incProgress = 1;
		}
	}
	public void setLevelText(){
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i=0; i<leftArray.length; i++){
			sb1.append(leftArray[i]);
			sb2.append(rightArray[i]);
			if (i<leftArray.length-1){
				sb1.append("\n");
				sb2.append("\n");
			}
		}
		leftLevel.setText(sb1.toString());
		rightExp.setText(sb2.toString());
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("UserLevel");
		super.onResume();
		if (isFirst){
			handler.sendEmptyMessageDelayed(100,50);
			isFirst = false;
		}
	}
	int curProgress;
	int myProgress;
	int incProgress;
	public Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 100:
					curProgress += incProgress;
					if (curProgress<myProgress){
						levelSeek.setProgress(curProgress);
						sendEmptyMessageDelayed(100,50);
					} else {
						levelSeek.setProgress(myProgress);
					}
					break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(100);
	}
}