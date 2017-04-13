package com.sumavision.talktv.videoplayer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 操控fragment帮助类：<br>
 * 基本操控行为不足以满足需求时，不仅要进行fragment扩展，同时需要扩展帮助类的实现.<br>
 * 扩展帮助类需注意：<br>
 * 1.实现抽象方法外，还需添加静态代码段: <br>
 * static { registerImpl(扩展帮助类名.class); }<br>
 * 2.初始化扩展帮助类：在application中 添加：<br>
 * new 扩展帮助类名();
 * 
 * @author suma-hpb
 * 
 */
public abstract class MediaFragmentHelper {

	public static final int BASIC = 1;
	public static final int EXTENDS = 2;

	public static MediaFragmentHelper getInstacne() {
		SparseArray<Class<? extends MediaFragmentHelper>> impls = MediaFragmentHelper
				.getImpl();
		Constructor<? extends MediaFragmentHelper> ctor = null;
		try {
			if (impls.size() == 1) {
				ctor = impls.get(MediaFragmentHelper.BASIC).getConstructor();
			} else {
				ctor = impls.get(MediaFragmentHelper.EXTENDS).getConstructor();
			}
			return ctor.newInstance();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static SparseArray<Class<? extends MediaFragmentHelper>> implMap = new SparseArray<Class<? extends MediaFragmentHelper>>();

	static {
		implMap.put(BASIC, BaseMediaFragmentHelperImpl.class);
	}

	protected static void registerImpl(
			Class<? extends MediaFragmentHelper> implClass) {
		implMap.put(EXTENDS, implClass);
	}

	public static SparseArray<Class<? extends MediaFragmentHelper>> getImpl() {
		return implMap;
	}

	/**
	 * 实例化扩展操控类
	 * 
	 * @param context
	 * @param bundle
	 * @return 当前扩展fragment类
	 */
	public abstract Fragment getFragment(Context context, Bundle bundle);

	public abstract void feedbackReport(String programId, String channelId, String subId,
			String titleName, String problem);
	
	public abstract void playCount(Context context,int programId,int subId, int channelId);
	
	public abstract void disablePlayVod(int subid, int vType);

}
