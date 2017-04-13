package com.sumavision.cachingwhileplaying.util;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.cachingwhileplaying.entity.PreLoadingResultInfo;

import crack.cracker.JarCracker;
import crack.listener.JarCrackCompleteListener;
import crack.util.CrackRequest;

public class ParserUtil implements JarCrackCompleteListener {
	private static final String TAG = "ParserUtil";
	private String url;
	private int type;
	private Context context;
	volatile private CrackResultListener crackResultListener;
	int count;
	protected final int CHECK_READY = 0xee13;

//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(android.os.Message msg) {
//			switch (msg.what) {
//			case CHECK_READY:
//				if (checkJar(context)) {
//					loadJar(context);
//				} else {
//					new CrackJarInit(context).init(0);
//				}
//				break;
//			default:
//				break;
//			}
//
//		};
//	};
	private int subId;

	public ParserUtil(Context context, CrackResultListener crackResultListener,
			String url, int type, int subId) {
		this.context = context;
		this.crackResultListener = crackResultListener;
		this.url = url;
		this.type = type;
		this.subId = subId;

		String tmp = CrackRequest.getRequestString(url, type);
		JarCracker.getInstance().crack(context.getApplicationContext(), url,
				tmp, type, this);
//		if (url.contains("iqiyi")) {
//			doInit(url);
//		} else {
//			new GetCrackResult(url, context, this).crack(this.type);
//			new GetCrackResultListene
//		}
		// doInit(url);
	};

//	@SuppressLint("NewApi")
//	public void loadJar(Context ct) {
//		if (type == 0) {
//			File jarFile = new File(ct.getFilesDir().getPath() + "/"
//					+ "Dynamic_temp.jar");
//			DexClassLoader dcl = new DexClassLoader(jarFile.toString(), ct
//					.getFilesDir().getPath(), null, this.getClass()
//					.getClassLoader());
//			try {
//				Class<?> c = dcl
//						.loadClass("com.sumavision.crack.interfacesImp.TestLoader");
//				ILoader tl = (ILoader) c.newInstance();
//				tl.crack(url, this);
//				Log.i(TAG, "loadJar() crack");
//
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (java.lang.InstantiationException e) {
//				e.printStackTrace();
//			}
//		} else if (type == 1) {
//			File jarFile = new File(ct.getFilesDir().getPath() + "/"
//					+ "Dynamic_live.jar");
//			DexClassLoader dcl = new DexClassLoader(jarFile.toString(), ct
//					.getFilesDir().getPath(), null, this.getClass()
//					.getClassLoader());
//			try {
//				Class<?> c = dcl
//						.loadClass("com.sumavision.crack.liveImp.CrackLiveFactory");
//				ILoader tl = (ILoader) c.newInstance();
//				tl.crack(url, this);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (java.lang.InstantiationException e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	public boolean checkJar(Context ct) {
//
//		File jarFile = new File(ct.getFilesDir().getPath() + "/"
//				+ "Dynamic_temp.jar");
//		if (jarFile.exists()) {
//			return true;
//		}
//		return false;
//
//	}

//	public void doInit(String url) {
//		this.url = url;
//		mHandler.sendEmptyMessage(CHECK_READY);
//	}

	private void play(String standUrl, String highUrl, String superUrl,
			String type) {
		PreLoadingResultInfo preLoadingResultInfo = new PreLoadingResultInfo();
		preLoadingResultInfo.type = type;
		preLoadingResultInfo.subId = subId;
		if (!TextUtils.isEmpty(standUrl)) {
			preLoadingResultInfo.path = standUrl;
			preLoadingResultInfo.standUrl = standUrl;
		}
		if (!TextUtils.isEmpty(highUrl)) {
			preLoadingResultInfo.highUrl = highUrl;
			if (TextUtils.isEmpty(standUrl)) {
				preLoadingResultInfo.path = highUrl;
			}
		}
		if (!TextUtils.isEmpty(superUrl)) {
			preLoadingResultInfo.superUrl = superUrl;
			if (TextUtils.isEmpty(standUrl) && TextUtils.isEmpty(highUrl)) {
				preLoadingResultInfo.path = superUrl;
			}
		}
		if (crackResultListener != null) {
			crackResultListener.crackResult(preLoadingResultInfo);
		}
	}

//	@Override
//	public HashMap<String, String> end(HashMap<String, String> result) {
//		Log.i(TAG, "crack end() method");
//		HashMap<String, String> map = result;
//		String standUrl = map.get("standardDef");
//		String highUrl = map.get("hightDef");
//		String superUrl = map.get("superDef");
//		String type = map.get("type");
//		if (null == standUrl) {
//			standUrl = "";
//		}
//		if (null == highUrl) {
//			highUrl = "";
//		}
//		if (null == superUrl) {
//			superUrl = "";
//		}
//		play(standUrl, highUrl, superUrl, type);
//		return null;
//	}

//	@Override
//	public void start() {
//
//	}
//
//	@Override
//	public void DownLoadComplete(int result) {
//		loadJar(context);
//	}

	public void stop() {
		crackResultListener = null;
	}

//	@Override
//	public void getCrackResultOver(HashMap<String, String> map) {
//		Log.i(TAG, "crack end() method");
//		String standUrl = map.get("standar");
//		String highUrl = map.get("hight");
//		String superUrl = map.get("super");
//		String type = map.get("videoType");
//		if (null == standUrl) {
//			standUrl = "";
//		}
//		if (null == highUrl) {
//			highUrl = "";
//		}
//		if (null == superUrl) {
//			superUrl = "";
//		}
//		play(standUrl, highUrl, superUrl, type);
//	}

	@Override
	public void onJarCrackComplete(HashMap<String, String> map) {
		Log.i(TAG, "onJarCrackComplete(), " + map);
		String standUrl = map.get("standardDef");
		String highUrl = map.get("hightDef");
		String superUrl = map.get("superDef");
		String type = map.get("videoType");
		if (null == standUrl) {
			standUrl = "";
		}
		if (null == highUrl) {
			highUrl = "";
		}
		if (null == superUrl) {
			superUrl = "";
		}
		play(standUrl, highUrl, superUrl, type);
	}
}
