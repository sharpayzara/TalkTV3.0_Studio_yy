package com.sumavision.talktv2.activity.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.view.ActionProvider;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.actionprovider.bean.CustomChooserTextView;
import com.sumavision.talktv2.bean.DialogInfo;
import com.sumavision.talktv2.fragment.CommonDialogFragment;
import com.sumavision.talktv2.fragment.CommonDialogFragment.OnCommonDialogListener;

/**
 * 收藏页菜单操作
 * 
 * @author suma-hpb
 * 
 */
public class ClearActionProvider extends ActionProvider implements
		OnClickListener {
	private Context mContext;

	public ClearActionProvider(Context context) {
		super(context);
		this.mContext = context;
	}

	DialogInfo info;
	CustomChooserTextView customChooserView;

	@SuppressLint("ResourceAsColor")
	@Override
	public View onCreateActionView() {
		customChooserView = new CustomChooserTextView(mContext);
		customChooserView.setExpandActivityOverflowText(R.string.clear_empty);
		customChooserView.setExpandActivityOverflowTextColor(R.color.white);
		customChooserView.setProvider(this);
		customChooserView.addClickListener(this);
		if (showLayout) {
			customChooserView.showCustomLayout();
		} else {
			customChooserView.hideCustomLayout();
		}
		return customChooserView;
	}

	private boolean showLayout;

	public void showOption() {
		this.showLayout = true;
		if (customChooserView != null) {
			customChooserView.showCustomLayout();
		}
	}

	public void hideOption() {
		this.showLayout = false;
		if (customChooserView != null) {
			customChooserView.hideCustomLayout();
		}
	}

	public void setDialogTitle(String title) {
		info = new DialogInfo();
		info.confirm = mContext.getResources().getString(R.string.ensure);
		info.cancel = mContext.getResources().getString(R.string.cancel);
		info.title = title;
	}

	@Override
	public void onClick(View v) {
		CommonDialogFragment dialog = CommonDialogFragment.newInstance(info,
				true);
		dialog.setOnClickListener(mOnClickListener);
		dialog.show(((FragmentActivity) mContext).getSupportFragmentManager(),
				"dialog");

	}

	OnCommonDialogListener mOnClickListener;

	public void setOnClickListener(OnCommonDialogListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
	}

	@Override
	public boolean onPerformDefaultAction() {
		return super.onPerformDefaultAction();
	}
}
