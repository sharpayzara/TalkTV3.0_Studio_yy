package com.sumavision.talktv2.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.StatementParser;
import com.sumavision.talktv2.http.json.StatementRequest;
import com.umeng.analytics.MobclickAgent;

/**
 * 声明页
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class StatementActivity extends BaseActivity {

	private TextView statementTxt;
	private StatementParser sparser = new StatementParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.statementxx);
		setContentView(R.layout.activity_statement);

		statementTxt = (TextView) findViewById(R.id.statement_txt);
		statementTxt.setText("");
		initLoadingLayout();
		showLoadingLayout();
		VolleyHelper.post(new StatementRequest().make(), new ParseListener(
				sparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (sparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					if (!TextUtils.isEmpty(sparser.statement)) {
						statementTxt.setText(sparser.statement);
					} else {
						statementTxt
								.setText(getString(R.string.statementcontent));
					}
				} else {
					statementTxt.setText(getString(R.string.statementcontent));
				}
			}
		}, this);
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("StatementActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("StatementActivity");
		super.onResume();
	}
}
