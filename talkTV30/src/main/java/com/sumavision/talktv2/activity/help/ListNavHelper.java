package com.sumavision.talktv2.activity.help;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.adapter.IBaseAdapter;
import com.sumavision.talktv2.bean.HotLibType;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * spinner导航帮助类
 * 
 * @author suma-hpb
 * 
 */
public class ListNavHelper {

	SherlockFragmentActivity mActivity;
	NavListAdapter navAdapter;
	OnNavigationItemSelectedListener listener;
	public String selectedTxt = "";

	public ListNavHelper(SherlockFragmentActivity mActivity,
			OnNavigationItemSelectedListener listener) {
		this.mActivity = mActivity;
		this.listener = listener;
	}

	public void initListActionBar(ArrayList<HotLibType> itemList) {
		ActionBar bar = mActivity.getSupportActionBar();
		bar.setDisplayShowTitleEnabled(false);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		navAdapter = new NavListAdapter(mActivity, itemList);
		bar.setListNavigationCallbacks(navAdapter, new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				if (listener != null) {
					listener.onItemSelected(itemPosition);
				}
				return true;
			}
		});
	}

	class NavListAdapter extends IBaseAdapter<HotLibType> {
		public NavListAdapter(Context context, ArrayList<HotLibType> objects) {
			super(context, objects);
			typeList = objects;
		}

		ArrayList<HotLibType> typeList;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.nav_list_item, parent,
						false);
			}
			TextView txtTitle = ViewHolder.get(convertView, R.id.title);
			txtTitle.setText(typeList.get(position).name);
			return convertView;
		}	

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.nav_dropdown_item,
						parent, false);
			}
			TextView txtTitle = ViewHolder.get(convertView, R.id.drop_down_title);
			txtTitle.setText(typeList.get(position).name);
			return convertView;
		}
	}

	public interface OnNavigationItemSelectedListener {
		public void onItemSelected(int itemPosition);
	}
	
	public void notifyChanged() {
		if (navAdapter != null) {
			navAdapter.notifyDataSetChanged();
		}
	}
}
