package com.sumavision.talktv2.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库help基类： <br>
 * more:开源greenDAO
 * 
 * @author suma-hpb
 * 
 */
public abstract class AbstractDBHelper {
	public AbstractDBHelper(Context ctx, String dbName, Class<?> tagClass,
			int dbVersion) {
		this.dbName = dbName;
		this.dbVersion = dbVersion;
		this.tag = tagClass.getSimpleName();
		open(ctx);
	}

	private String dbName;
	protected int dbVersion = 1;
	private String tag;
	// SQLite数据库实例
	protected SQLiteDatabase mDb = null;
	public CreateDBHelper mDbHelper = null;

	// 创建数据库表的SQL语句
	protected abstract String createDBTable();

	// 删除数据库表的SQL语句
	protected abstract String dropDBTable();

	// 内部数据库创建
	class CreateDBHelper extends SQLiteOpenHelper {
		public CreateDBHelper(Context ctx) {
			super(ctx, dbName, null, dbVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			executeBatch(createDBTable(), db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(tag, "Upgrading database '" + getDatabaseName()
					+ "' from version " + oldVersion + " to " + newVersion);
			executeBatch(dropDBTable(), db);
			onCreate(db);
		}

		private void executeBatch(String sql, SQLiteDatabase db) {
			if (sql == null) {
				return;
			}

			db.beginTransaction();
			try {
				db.execSQL(sql);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.e(tag, e.getMessage(), e);
			} finally {
				db.endTransaction();
			}
		}
	}

	// 打开或者创建一个指定名称的数据库
	private void open(Context ctx) {
		Log.i(tag, "Open database '" + dbName + "'");
		try {
			mDbHelper = new CreateDBHelper(ctx);
			if (mDbHelper != null) {
				mDb = mDbHelper.getWritableDatabase();
			}
		} catch (SQLException e) {
			Log.e("open", e.getMessage());
		}

	}

	// 关闭数据库
	public void close() {
		try {
			if (mDbHelper != null) {
				Log.i(tag, "Close database '" + dbName + "'");
				mDbHelper.close();
			}
		} catch (SQLException e) {
			Log.e("close", e.getMessage());
		}
	}
}
