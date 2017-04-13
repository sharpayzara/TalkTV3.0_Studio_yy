package com.sumavision.talktv.videoplayer.dao;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.History;

public class AccessProgram {
	public static final String DATABASE_UPDATE = "com.sumavision.history.update";

	private static AccessProgram instance;
	Context context;

	public static AccessProgram getInstance(Context context) {
		if (instance == null) {
			instance = new AccessProgram(context);
		}
		return instance;
	}

	private AccessProgram(Context context) {
		this.context = context;
	}

	public boolean isExisted(NetPlayData vodProgram, SQLiteDatabase db) {
		try {
			List<History> bean = DataSupport.where(
					"programId = " + vodProgram.id).find(History.class);
			if (bean != null && bean.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public boolean save(NetPlayData program) {
		History bean = History.getNetPlayDataBean(program, System.currentTimeMillis());
		try {
			if (isExisted(program, null)) {
				update(program, null);
				context.sendBroadcast(new Intent(DATABASE_UPDATE));
			} else {
				bean.save();
				context.sendBroadcast(new Intent(DATABASE_UPDATE));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			Connector.getDatabase().close();
		}
		return true;
	}

	/**
	 * 根据节目id和子节目id获取节目信息(position)
	 * 
	 * @param programId
	 * @param subId
	 * @return
	 */
	public NetPlayData findByProgramIdAndSubId(String programId, String subId) {
		try {
			List<History> beans = DataSupport.where(
					"programId = " + programId + " and subid = " + subId).find(
					History.class);
			if (beans.size() > 0) {
				return History.getNetPlayData(beans.get(0));
			} else {
				return new NetPlayData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new NetPlayData();
		}
	}

	private void update(NetPlayData program, SQLiteDatabase db) {
		History bean = History.getNetPlayDataBean(program, System.currentTimeMillis());
		ContentValues values = History.getChangedValues(bean);
		StringBuilder conditions = new StringBuilder("programId = "
				+ program.id);
		DataSupport.updateAll(History.class, values,
				conditions.toString());
	}

	public ArrayList<NetPlayData> findAll() {
		try {
			List<History> list = DataSupport.order("timestamp desc").find(
					History.class);
			ArrayList<NetPlayData> datas = new ArrayList<NetPlayData>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					NetPlayData n = History.getNetPlayData(list.get(i));
					datas.add(n);
				}
			}
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<NetPlayData>();
		}
	}

	public void delete(NetPlayData program) {
		StringBuilder conditions = new StringBuilder("programId = "
				+ program.id);
		DataSupport.deleteAll(History.class, conditions.toString());
	}

	public void clear() {
		DataSupport.deleteAll(History.class);
	}

}
