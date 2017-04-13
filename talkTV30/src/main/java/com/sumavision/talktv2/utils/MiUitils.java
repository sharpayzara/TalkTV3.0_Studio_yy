package com.sumavision.talktv2.utils;

import java.lang.reflect.Field;

import android.app.Notification;
import android.os.Build;

/**
 * 小米适配
 * 
 * @author suma-hpb
 * 
 */
public class MiUitils {

	public static boolean isXiaomi() {
		return Build.MANUFACTURER.equals("Xiaomi");
	}

	/**
	 * 应用桌面角标
	 * 
	 * @param notification
	 */
	public static void addDesktopCornerMark(Notification notification) {
		if (isXiaomi()) {
			try {
				Class<?> miuiNotificationClass = Class
						.forName("android.app.MiuiNotification");
				Object miuiNotification = miuiNotificationClass.newInstance();
				Field field = miuiNotification.getClass().getDeclaredField(
						"messageCount");
				field.setAccessible(true);
				field.set(miuiNotification, 1);// 设置信息数
				field = notification.getClass().getField("extraNotification");
				field.set(notification, miuiNotification);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}
}
