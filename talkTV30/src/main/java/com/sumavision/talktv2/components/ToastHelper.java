package com.sumavision.talktv2.components;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
	
	private static Toast toast;
	
	public static void showToast(Context context, String text) {
		if (text == null || context == null) {
			return;
		}
		if (toast != null) {
			toast.setText(text);
		} else {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}
		toast.show();
	}
}
