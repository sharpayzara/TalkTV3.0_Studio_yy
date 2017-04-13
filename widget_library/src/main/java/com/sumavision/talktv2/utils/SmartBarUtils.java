package com.sumavision.talktv2.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ActionBar;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * 魅族smartbar适配
 * 
 * @author suma-hpb
 * 
 */
public class SmartBarUtils {
	public static boolean hasSmartBar() {
		try {
			// 新型号可用反射调用Build.hasSmartBar()
			Method method = Class.forName("android.os.Build").getMethod(
					"hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}

		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if (Build.DEVICE.equals("mx2")) {
			return true;
		} else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}

		return false;
	}

	/**
	 * 调用 ActionBar.setActionBarViewCollapsable(boolean) 方法。
	 * 
	 * <p>
	 * 设置ActionBar顶栏无显示内容时是否隐藏。
	 * 
	 * <p>
	 * 示例：
	 * </p>
	 * 
	 * <pre class="prettyprint">
	 * public class MyActivity extends Activity {
	 * 
	 * 	protected void onCreate(Bundle savedInstanceState) {
	 *         super.onCreate(savedInstanceState);
	 *         ...
	 *         
	 *         final ActionBar bar = getActionBar();
	 *         
	 *         // 调用setActionBarViewCollapsable，并设置ActionBar没有显示内容，则ActionBar顶栏不显示
	 *         SmartBarUtils.setActionBarViewCollapsable(bar, true);
	 *         bar.setDisplayOptions(0);
	 *     }
	 * }
	 * </pre>
	 */
	public static void setActionBarViewCollapsable(ActionBar actionbar,
			boolean collapsable) {
		try {
			Method method = Class.forName("android.app.ActionBar").getMethod(
					"setActionBarViewCollapsable",
					new Class[] { boolean.class });
			try {
				method.invoke(actionbar, collapsable);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置返回键图标
	 * 
	 */
	public static void setBackIcon(ActionBar actionbar, Drawable backIcon) {
		try {
			Method method = Class.forName("android.app.ActionBar").getMethod(
					"setBackButtonDrawable", new Class[] { Drawable.class });
			try {
				method.invoke(actionbar, backIcon);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
