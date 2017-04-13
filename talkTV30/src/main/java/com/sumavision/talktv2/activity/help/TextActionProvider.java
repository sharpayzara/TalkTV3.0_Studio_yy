package com.sumavision.talktv2.activity.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.ActionProvider;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.actionprovider.bean.CustomChooserTextView;

/**
 * 纯文字action
 * 
 * @author suma-hpb
 * 
 */
public class TextActionProvider extends ActionProvider {

	private Context mContext;

	public TextActionProvider(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 
	 * @param listener
	 * @param tag
	 *            指定view tag
	 */
	public void setOnClickListener(OnClickListener listener, String tag) {
		this.listener = listener;
		this.tag = tag;
		if (customChooserView != null) {
			customChooserView.addClickListener(listener);
			customChooserView.setButtonTag(tag);
		}
	}

	private int resId;
	String tag;
	OnClickListener listener;

	public void setOnClickListener(OnClickListener listener) {
		this.listener = listener;
		if (customChooserView != null) {
			customChooserView.addClickListener(listener);
		}
	}

	public void setShowText(int resId) {
		this.resId = resId;
		if (customChooserView != null) {
			customChooserView.setExpandActivityOverflowText(resId);
		}
	}

	CustomChooserTextView customChooserView;

	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateActionView() {
		customChooserView = new CustomChooserTextView(mContext);
		customChooserView.setExpandActivityOverflowTextColor(R.color.white);
		customChooserView.setProvider(this);
		customChooserView.setExpandActivityOverflowText(resId);
		customChooserView.addClickListener(listener);
		customChooserView.setButtonTag(tag);
		return customChooserView;
	}

}
