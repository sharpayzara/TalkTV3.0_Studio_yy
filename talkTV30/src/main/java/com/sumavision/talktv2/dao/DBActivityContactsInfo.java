package com.sumavision.talktv2.dao;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sumavision.talktv2.bean.ActivityJoinBean;

public class DBActivityContactsInfo extends AbstractDBHelper {

	public DBActivityContactsInfo(Context ctx) {
		super(ctx, "talktv_ac.db", DBActivityContactsInfo.class, 1);
	}

	public boolean isExisted(ActivityJoinBean aJoinBean) {

		String querrySql = "select count(*) from contactinfo where id = ?and num = ?";
		String id = aJoinBean.activityid;
		String num = aJoinBean.activitynum;
		String[] bindArgs = { id, num };
		Cursor cursor = mDb.rawQuery(querrySql, bindArgs);
		if (cursor != null) {
			cursor.moveToFirst();
			long howLong = cursor.getLong(0);
			cursor.close();
			if (howLong > 0) {
				return true;
			}
		}
		return false;

	}

	public boolean save(ActivityJoinBean aJoinBean) {
		String sql = null;
		sql = "INSERT INTO contactinfo (id,manager,num) VALUES (?, ?, ?)";
		mDb.beginTransaction();
		try {

			String id = aJoinBean.activityid;
			String activitymanager = aJoinBean.activitymanager == null ? ""
					: aJoinBean.activitymanager;
			String activitynum = aJoinBean.activitynum == null ? ""
					: aJoinBean.activitynum;

			if (!isExisted(aJoinBean)) {
				Object[] bindArgs = { id, activitymanager, activitynum };
				mDb.execSQL(sql, bindArgs);
			} else {
				update(aJoinBean);
				Log.e("AccessProgramPlayPosition", "@needUpdate");
			}

			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDb.endTransaction();
			mDb.close();
		}
		return true;

	}

	private void update(ActivityJoinBean aJoinBean) {
		String sql = "";
		sql = "update contactinfo set manager=? where id=? and num=? ";
		String id = aJoinBean.activityid;
		String num = aJoinBean.activitynum;
		String manager = aJoinBean.activitymanager;
		// String manager = aJoinBean.activitymanager;
		String[] bindArgs = { manager, id, num };
		try {
			mDb.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public ActivityJoinBean findById(String id, String num) {
		ActivityJoinBean temp = new ActivityJoinBean();
		String sql = "SELECT * FROM contactinfo where id = ?and num = ?";
		String[] selectionArgs = { id, num };

		Cursor cursor = null;
		try {
			mDb.beginTransaction();
			cursor = mDbHelper.getReadableDatabase().rawQuery(sql,
					selectionArgs);
			while (cursor.moveToNext()) {
				temp.activityid = cursor.getString(1);
				temp.activitymanager = cursor.getString(2);
				temp.activitynum = cursor.getString(3);
			}
			cursor.close();
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDb.endTransaction();
			mDbHelper.close();
		}
		return temp;
	}

	public ArrayList<ActivityJoinBean> findAllById(String id) {
		String querrySql = "select * from contactinfo where id = ? ";

		String[] bindArgs = new String[] { id };
		ArrayList<ActivityJoinBean> temp = new ArrayList<ActivityJoinBean>();
		Cursor cursor = null;
		try {
			mDb.beginTransaction();
			cursor = mDbHelper.getReadableDatabase().rawQuery(querrySql,
					bindArgs);
			while (cursor.moveToNext()) {
				ActivityJoinBean data = new ActivityJoinBean();
				data.activityid = cursor.getString(cursor.getColumnIndex("id"));
				data.activitymanager = cursor.getString(cursor
						.getColumnIndex("manager"));
				data.activitynum = cursor.getString(cursor
						.getColumnIndex("num"));
				temp.add(data);
			}
			cursor.close();
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDb.endTransaction();
			mDbHelper.close();
		}
		return temp;

	}

	@Override
	protected String createDBTable() {
		StringBuffer sql = new StringBuffer(
				"CREATE TABLE IF NOT EXISTS contactinfo (");
		sql.append("_id integer primary key autoincrement,").append(
				"id varchar(10),manager varchar(100),num varchar(100))");
		return sql.toString();
	}

	@Override
	protected String dropDBTable() {
		return "DROP TABLE IF EXISTS contactinfo";
	}

}
