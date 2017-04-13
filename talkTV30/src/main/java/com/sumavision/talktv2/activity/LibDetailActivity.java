package com.sumavision.talktv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.help.ListNavHelper;
import com.sumavision.talktv2.activity.help.ListNavHelper.OnNavigationItemSelectedListener;
import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.fragment.lib.LibNormalFragment;
import com.sumavision.talktv2.fragment.lib.LibNormalFragment.FilterListener;
import com.sumavision.talktv2.fragment.lib.LibSubProgramFragment;
import com.sumavision.talktv2.fragment.lib.RankingFragment;
import com.sumavision.talktv2.fragment.lib.SpecialListFragment;

import java.util.ArrayList;

/**
 * 片库类型详情页:排行榜、电视剧等
 * 
 * @author suma-hpb
 * 
 */
public class LibDetailActivity extends BaseActivity implements
		OnNavigationItemSelectedListener, FilterListener {

	ListNavHelper navHelper;
	ArrayList<HotLibType> typeList;
	HotLibType selectedData;
	int selectedPosition;
	int position = 0;
	long id;

	LibNormalFragment normalFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lib_detail);
		typeList = getIntent().getParcelableArrayListExtra("libType");
		id = getIntent().getLongExtra("id", -1);
		if (typeList == null) {
			typeList = new ArrayList<HotLibType>();
		}
		for (int i = 0; i < typeList.size(); i++) {
			if (typeList.get(i).type == HotLibType.TYPE_USHOW) {
				typeList.remove(i);
				break;
			}
		}
		for (int i = 0; i < typeList.size(); i++) {
			if (typeList.get(i).id == id) {
				position = i;
				break;
			}
		}
		navHelper = new ListNavHelper(this, this);
		navHelper.initListActionBar(typeList);
		getSupportActionBar().setSelectedNavigationItem(position);
	}

	MenuItem filterMenuItem;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_lib_detail, menu);
		filterMenuItem = menu.findItem(R.id.action_filter);
		filterMenuItem.setVisible(false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				startActivity(new Intent(this, SearchActivity.class));
				break;
			case R.id.action_filter:
				if (normalFragment != null) {
					normalFragment.changePagerToFilter();
				}
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(int itemPosition) {
		changeFragment(itemPosition);
	}

	private void changeFragment(int itemPosition) {
		setFilterBtn(false);
		int type = typeList.get(itemPosition).type;
		int columnId = (int) typeList.get(itemPosition).id;
		int programType = typeList.get(itemPosition).programType;
		switch (type) {
		case HotLibType.TYPE_RANKING:
			addContainer(RankingFragment.newInstance());
			break;
		case HotLibType.TYPE_SPECIAL:
			addContainer(SpecialListFragment.newInstance(columnId));
			break;
		case HotLibType.TYPE_SUB:
			addContainer(LibSubProgramFragment.newInstance(columnId));
			break;
		default:
			normalFragment = LibNormalFragment.newInstance(columnId,
					programType,getIntent().getIntExtra("tagId",0));
			addContainer(normalFragment);
			break;
		}
	}

	private void addContainer(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commitAllowingStateLoss();
	}

	@Override
	public void setFilterBtn(boolean visible) {
		if (filterMenuItem != null) {
			filterMenuItem.setVisible(visible);
		}
	}
}
