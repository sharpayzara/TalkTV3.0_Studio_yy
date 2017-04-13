package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.fragment.DeleteDialogFragment;
import com.sumavision.talktv2.http.request.VolleyRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;
/**
 * 
 * @author 郭鹏
 * @description 程序关于界面
 * 
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener{
	long lastTime;
	long curTime;
	int times;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.my_about);
		setContentView(R.layout.activity_about);
		String versionName = "Ver " + AppUtil.getAppVersionId(this);
		((TextView) findViewById(R.id.version_text)).setText(versionName);
		findViewById(R.id.center_image).setOnClickListener(this);
		findViewById(R.id.version_text).setOnClickListener(this);
	}
	private void switchAddress(int type){
		if (!Constants.canSwitch) return;
		curTime = System.currentTimeMillis();
		long temp = curTime - lastTime;
		if (temp > 0 && temp < 2000) {
			times++;
		} else {
			times = 0;
		}
		lastTime = curTime;
		if (times >= 4) {
			if (type == 1){
				if (Constants.host.startsWith(Constants.url)) {
					Constants.host = Constants.url_test + Constants.suffix;
				} else {
					Constants.host = Constants.url + Constants.suffix;
				}
				PreferencesUtils.putString(this,null,"req_host",Constants.host);
				VolleyRequest.url = Constants.host;
				Toast.makeText(AboutActivity.this, Constants.host, Toast.LENGTH_SHORT).show();
			} else if (type ==2){
				if (Constants.picUrlFor.startsWith(Constants.url)){
					Constants.picUrlFor = Constants.url_test +"resource/";
				} else {
					Constants.picUrlFor = Constants.url +"resource/";
				}
				Toast.makeText(AboutActivity.this, Constants.picUrlFor, Toast.LENGTH_SHORT).show();
			}
			times = 0;
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("AboutActivity");
		super.onResume();
	}

	@Override
	public void onPause() {
		MobclickAgent.onPageEnd("AboutActivity");
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.center_image:
				switchAddress(1);
				break;
			case R.id.version_text:
				switchAddress(2);
				break;
		}
	}
}
