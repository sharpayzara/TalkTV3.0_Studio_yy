package com.sumavision.talktv2.bean;

import android.app.Application;

public class SimpleClass {
	private static Application instance;

	public SimpleClass(Application application) {
		instance = application;
	}

	public static Application getInstance() {
		return instance;
	}

}
