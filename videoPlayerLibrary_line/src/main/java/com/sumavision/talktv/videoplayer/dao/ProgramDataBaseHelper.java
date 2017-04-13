package com.sumavision.talktv.videoplayer.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProgramDataBaseHelper extends SQLiteOpenHelper {
	private static String DB_NAME = "talktv_program.db";
	private static final int version = 6;

	@Override
	public synchronized void close() {
		super.close();
	}

	public ProgramDataBaseHelper(Context context) {
		super(context, DB_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS program ("
				+ "_id integer primary key autoincrement,"
				+ "id varchar(10),"
				+ "programname varchar(100),"
				+ "lastestintro varchar(100),"
				+ "path varchar(100),"
				+ "position varchar(100),"
				+ "url varchar(100),"
				+ "subid varchar(10),isDownloaded varchar(10),platId varchar(10),topicId varchar(10))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		String sql = "DROP TABLE IF EXISTS program";
		// int currVersion = oldVersion;
		// if (currVersion == 4) {
		// sql = "ALTER TABLE program  ADD isDownloaded varchar(10)";
		// db.execSQL(sql);
		// sql = "ALTER TABLE program  ADD  platId varchar(10)";
		// db.execSQL(sql);
		// currVersion = 5;
		// Log.e("program_history", "database update from 4 TO 5");
		// }
		// if (currVersion == 5) {
		// sql = "ALTER TABLE program  ADD topicId varchar(10)";
		// currVersion = 6;
		// Log.e("program_history", "database update from 5 TO 6");
		// }
		db.execSQL(sql);

		this.onCreate(db);
	}

	public String dropTable(SQLiteDatabase db, String tableName) {
		if (tableName == null) {
			return null;
		}

		try {
			String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
			db.execSQL(DROP_TABLE);
		} catch (Exception ex) {
		}

		db.close();

		return tableName;
	}
}
