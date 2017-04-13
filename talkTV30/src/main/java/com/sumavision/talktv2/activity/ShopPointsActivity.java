package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ExpandableListView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.PointAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.PointList;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.PointParser;
import com.sumavision.talktv2.http.json.PointRequest;
import com.sumavision.talktv2.utils.Constants;
import com.umeng.analytics.MobclickAgent;

/**
 * 赚积分
 * 
 * @author suma-hpb
 * 
 */
public class ShopPointsActivity extends BaseActivity {
	private ExpandableListView expandListView;
	private List<PointList> mList = new ArrayList<PointList>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_point);
		getSupportActionBar().setTitle(getString(R.string.earn_score_title));
		initLoadingLayout();
		expandListView = (ExpandableListView) findViewById(R.id.point_expandablelistview);
		getTaskResponse();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPageEnd("ShopPointsActivity");
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart("ShopPointsActivity");
		super.onResume();
	}

	TextActionProvider accountAction;

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// getSupportMenuInflater().inflate(R.menu.shop_point, menu);
		// accountAction = (TextActionProvider)
		// menu.findItem(R.id.action_account)
		// .getActionProvider();
		// accountAction.setShowText(R.string.earn_go_init);
		// accountAction.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// startActivity(new Intent(getApplicationContext(),
		// MyAccountActivity.class));
		// }
		// });
		return super.onCreateOptionsMenu(menu);
	};

	PointParser pointParser = new PointParser();

	private void getTaskResponse() {
		VolleyHelper.post(new PointRequest().make(), new ParseListener(
				pointParser) {

			@Override
			public void onParse(BaseJsonParser parser) {
				hideLoadingLayout();
				if (pointParser.errCode == JSONMessageType.SERVER_CODE_OK) {
					mList = pointParser.pointLists;
					updateView();
				} else {
					showErrorLayout();
				}

			}
		}, this);
	}

	private void updateView() {
		expandListView.setAdapter(new PointAdapter(this, mList));
		expandListView.setGroupIndicator(null);
		int groupCount = expandListView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandListView.expandGroup(i);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		VolleyHelper.cancelRequest(Constants.userTaskList);
	}

}