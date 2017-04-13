package com.sumavision.talktv2.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.SimpleClass;

public class DialogUtil {
	/**
	 * 
	 * @param contenxt
	 * @param amount
	 *            //积分数量
	 * 
	 */
	public static void showScoreAddToast(Context context, String s) {
		if (s == null || s.equals("")) {
			return;
		}
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_layout,
				(ViewGroup) (((Activity) context)
						.findViewById(R.id.toast_layout_root)));
		TextView textView = (TextView) view.findViewById(R.id.score_value);
		textView.setText(s);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 200);
		toast.show();
	}

	/**
	 * 
	 * @param contenxt
	 * @param amount
	 *            //积分数量
	 * 
	 */
	public static void updateScoreToast(String s) {
		if (s == null || s.equals("")) {
			return;
		}
		Toast toast = new Toast(SimpleClass.getInstance());
		toast.setDuration(Toast.LENGTH_SHORT);
		LayoutInflater inflater = LayoutInflater
				.from(SimpleClass.getInstance());
		View view = inflater.inflate(R.layout.score_toast, null);
		TextView textView = (TextView) view.findViewById(R.id.text_view);
		textView.setText(s);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 200);
		toast.show();
	}

	/**
	 * 
	 * @param contenxt
	 * @param s
	 *            //显示的消息
	 * 
	 */
	public static void showBadgeAddToast(Context context, String s) {
		if (s == null || s.equals("")) {
			return;
		}
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_badge_layout,
				(ViewGroup) (((Activity) context)
						.findViewById(R.id.toast_layout_root)));
		TextView textView = (TextView) view.findViewById(R.id.content);
		textView.setText(s);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER, 0, 200);
		toast.show();
	}

	public static void alertToast(Context context, String msg) {
		try {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}
