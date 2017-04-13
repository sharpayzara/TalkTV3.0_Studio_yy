package com.sumavision.talktv2.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.TextActionProvider;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.FestivalListParser;
import com.sumavision.talktv2.http.json.FestivalListRequest;

/**
 * 刮奖列表
 * 
 * @version
 * @description
 */
public class PrizeListActivity extends BaseActivity {
	
	private int id;
	private ListView listView;
	private MyListAdapter adapter;
	private ArrayList<String> listName = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_prize_list);
		getSupportActionBar().setTitle("中奖名单");
		id = getIntent().getIntExtra("id", 0);
		initView();
		getData();
	}
	
	TextActionProvider refresh;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.refresh, menu);
		refresh = (TextActionProvider) menu.findItem(R.id.action_refresh).getActionProvider();
		if (refresh == null){
			return super.onCreateOptionsMenu(menu);
		}
		refresh.setShowText(R.string.navigator_refresh);
		refresh.setOnClickListener(ActionProviderClickListener, "refresh");
		return super.onCreateOptionsMenu(menu);
	}

	OnClickListener ActionProviderClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			getData();
		}
	};
	
	private void initView() {
		initLoadingLayout();
		hideLoadingLayout();
		listView = (ListView) findViewById(R.id.list);
		adapter = new MyListAdapter(this, listName);
		listView.setAdapter(adapter);
	}
	
	private void getData() {
		showLoadingLayout();
		final FestivalListParser lparser = new FestivalListParser();
		VolleyHelper.post(new FestivalListRequest(id).make(), new ParseListener(lparser) {
			@Override
			public void onParse(BaseJsonParser parser) {
				if (lparser.errCode == JSONMessageType.SERVER_CODE_OK) {
					listName.clear();
					listName.addAll(lparser.listName);
					updateView();
				}
				hideLoadingLayout();
			}
		}, null);
	}
	
	private void updateView() {
		adapter.notifyDataSetChanged();
	}
	
	private class MyListAdapter extends IBaseAdapter<String> {

		public MyListAdapter(Context context, List<String> objects) {
			super(context, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_single_line, null);
			}
			TextView content = (TextView) convertView.findViewById(R.id.content);
			content.setText(getItem(position));
			return convertView;
		}
	}
}
