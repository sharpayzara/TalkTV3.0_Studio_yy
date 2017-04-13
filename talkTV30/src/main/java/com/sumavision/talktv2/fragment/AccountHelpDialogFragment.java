package com.sumavision.talktv2.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;

/**
 * 我的帐号页-帮助
 * 
 * @author suma-hpb
 * 
 */
public class AccountHelpDialogFragment extends DialogFragment {

	public static final int HELP_TYPE_POINT = 0;
	public static final int HELP_TYPE_GOLDEN = 1;
	public static final int HELP_TYPE_DIAMOND = 2;
	private int type;

	public static AccountHelpDialogFragment newInstance(int type) {
		AccountHelpDialogFragment fragment = new AccountHelpDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments() != null ? getArguments().getInt("type") : 0;
	}

	ImageView typeIcon;
	TextView titleTxt, introTxt, descTxt;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Dialog dialog = new Dialog(getActivity(), R.style.MyDialog);
		View rootView = inflater.inflate(R.layout.dialog_account_help, null,
				false);
		initViews(rootView);
		dialog.setContentView(rootView);
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	protected void initViews(View view) {
		typeIcon = (ImageView) view.findViewById(R.id.imgView_type);
		titleTxt = (TextView) view.findViewById(R.id.tv_title);
		introTxt = (TextView) view.findViewById(R.id.tv_intro);
		descTxt = (TextView) view.findViewById(R.id.tv_desc);
		switch (type) {
		case HELP_TYPE_POINT:
			typeIcon.setImageResource(R.drawable.credits);
			titleTxt.setTextColor(getResources().getColor(
					R.color.userinfo_point));
			titleTxt.setText(R.string.point);
			introTxt.setText(getResources().getText(R.string.creditintro));
			descTxt.setText(getResources().getText(R.string.credit_desc));
			break;
		case HELP_TYPE_GOLDEN:
			typeIcon.setImageResource(R.drawable.coins);
			titleTxt.setTextColor(getResources().getColor(R.color.yellow));
			titleTxt.setText(R.string.gold);
			introTxt.setText(getResources().getText(R.string.coinintro));
			descTxt.setText(getResources().getText(R.string.coincome));
			break;
		case HELP_TYPE_DIAMOND:
			typeIcon.setImageResource(R.drawable.diamond_large);
			titleTxt.setTextColor(getResources().getColor(
					R.color.userinfo_diamond));
			titleTxt.setText(R.string.diamond);
			introTxt.setText(getResources().getText(R.string.diamondintro));
			descTxt.setText(getResources().getText(R.string.diamond_desc));
			break;
		default:
			break;
		}
	}

}
