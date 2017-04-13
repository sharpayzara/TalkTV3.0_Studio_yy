package com.sumavision.talktv2.dao;

import android.content.Context;
import android.database.Cursor;

import com.sumavision.talktv2.bean.ActivityJoinBean;

public class DBActivityInfo extends AbstractDBHelper {
	public DBActivityInfo(Context ctx) {
		super(ctx, "talktv_activity.db", DBActivityInfo.class, 1);
	}

	public boolean isExisted(ActivityJoinBean aJoinBean) {

		String querrySql = "select count(*) from activityinfo where id = ?";
		String id = aJoinBean.activityid;
		String[] bindArgs = { id };
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
		mDb = mDbHelper.getWritableDatabase();
		mDb.beginTransaction();
		try {
			String id = aJoinBean.activityid;
			String activityfriend = aJoinBean.activityfriend == null ? ""
					: aJoinBean.activityfriend;
			String activitycircle = aJoinBean.activitycircle == null ? ""
					: aJoinBean.activitycircle;
			String activityweibo = aJoinBean.activityweibo == null ? ""
					: aJoinBean.activityweibo;
			Object[] bindArgs = { activityfriend, activitycircle,
					activityweibo, id };
			if (isExisted(aJoinBean)) {
				sql = "update activityinfo set friend=?, circle = ?,weibo =? where id=?  ";
			} else {
				sql = "INSERT INTO activityinfo (friend,circle,weibo,id) VALUES (?, ?, ?, ?)";
			}
			mDb.execSQL(sql, bindArgs);
			mDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDb.endTransaction();
			mDbHelper.close();
		}
		return true;

	}

	public ActivityJoinBean findById(String id) {
		ActivityJoinBean temp = new ActivityJoinBean();
		String sql = "SELECT * FROM activityinfo where id = ?";
		String[] selectionArgs = { id };
		Cursor cursor = null;
		try {
			mDbHelper.getWritableDatabase().beginTransaction();
			cursor = mDbHelper.getReadableDatabase().rawQuery(sql,
					selectionArgs);
			while (cursor.moveToNext()) {
				temp.activityid = cursor.getString(1);
				temp.activityfriend = cursor.getString(2);
				temp.activitycircle = cursor.getString(3);
				temp.activityweibo = cursor.getString(4);
			}
			cursor.close();
			mDbHelper.getWritableDatabase().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDbHelper.getWritableDatabase().endTransaction();
			mDbHelper.close();
		}
		return temp;
	}

	@Override
	protected String createDBTable() {
		return "CREATE TABLE IF NOT EXISTS activityinfo ("
				+ "_id integer primary key autoincrement," + "id varchar(10),"
				+ "friend varchar(100)," + "circle varchar(100),"
				+ "weibo varchar(100)" + ")";
	}

	@Override
	protected String dropDBTable() {
		return "DROP TABLE IF EXISTS activityinfo";
	}

}
