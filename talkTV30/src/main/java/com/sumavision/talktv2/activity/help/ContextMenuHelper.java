package com.sumavision.talktv2.activity.help;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public abstract class ContextMenuHelper {

	/**
	 * return item menu name
	 * 
	 * @param position
	 *            clicked item,if listview has header,pos contains header
	 * @return
	 */
	public abstract String getContextMenuTitle(int position);

	public static final int MENU_DELETE = 1;

	public ContextMenuHelper(ListView mListView) {
		mListView
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						AdapterView.AdapterContextMenuInfo vi = (AdapterContextMenuInfo) menuInfo;
						menu.setHeaderTitle(getContextMenuTitle(vi.position));
						menu.add(0, MENU_DELETE, 0, "删除");

					}
				});
	}

	/**
	 * activity.onContextItemSelected
	 * 
	 * @param item
	 */
	public void onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			onMenuItemClick(MENU_DELETE, info.position);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param itemId
	 *            menuItem id
	 * @param position
	 *            same as getContextMenuTitle(position)
	 */
	public abstract void onMenuItemClick(int itemId, int position);
}
