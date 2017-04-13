package com.sumavision.talktv2.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.widget.TextView;

import com.sumavision.talktv2.bean.DialogInfo;

/**
 * 普通提示对话框
 * 
 * @author suma-hpb
 * 
 */
public class CommonDialogFragment extends DialogFragment {
	public static CommonDialogFragment newInstance(DialogInfo info,
			boolean cancelable) {
		CommonDialogFragment fragment = new CommonDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("info", info);
		bundle.putBoolean("cancelable", cancelable);
		fragment.setArguments(bundle);
		return fragment;
	}

	boolean cancelable;
	OnCommonDialogListener mOnClickListener;

	public void setOnClickListener(OnCommonDialogListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
	}

	DialogInfo info;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		info = getArguments().getParcelable("info");
		cancelable = getArguments().getBoolean("cancelable");
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setIcon(info.iconRes);
		builder.setMessage(info.content);
		builder.setCancelable(cancelable);
		if (!TextUtils.isEmpty(info.confirm)) {
			builder.setPositiveButton(info.confirm, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
					if (mOnClickListener != null) {
						mOnClickListener.onPositiveButtonClick();
					}
				}
			});
		}
		if (!TextUtils.isEmpty(info.neutral)) {
			builder.setNeutralButton(info.neutral,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mOnClickListener != null) {
								mOnClickListener.onNeutralButtonClick();
							}
						}
					});
		}
		if (!TextUtils.isEmpty(info.cancel)) {
			builder.setNegativeButton(info.cancel,
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismiss();
							if (mOnClickListener != null) {
								mOnClickListener.onNegativeButtonClick();
							}
						}
					});
		}
		AlertDialog dialog = builder.create();
		if (TextUtils.isEmpty(info.title)) {
			setStyle(STYLE_NO_TITLE, 0);
		} else {
			dialog.setTitle(info.title);
		}
		
		return dialog;
	}

	TextView contentView;

	@Override
	public void onStart() {
		super.onStart();
		contentView = (TextView) getDialog().findViewById(android.R.id.message);
		if (info.contentColorResId > 0) {
			contentView.setTextColor(getResources().getColor(
					info.contentColorResId));
		}
	}

	public interface OnCommonDialogListener {
		public void onPositiveButtonClick();

		public void onNeutralButtonClick();

		public void onNegativeButtonClick();

	}

}
