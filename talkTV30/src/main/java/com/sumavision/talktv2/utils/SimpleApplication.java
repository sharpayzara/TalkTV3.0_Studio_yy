package com.sumavision.talktv2.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;

public class SimpleApplication {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static SimpleApplication instance;

	private SimpleApplication() {
	}

	public static SimpleApplication getInstance() {
		if (null == instance) {
			instance = new SimpleApplication();
		}
		return instance;

	}

	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

}
