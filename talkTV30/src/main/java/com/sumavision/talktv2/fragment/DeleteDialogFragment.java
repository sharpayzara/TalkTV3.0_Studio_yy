package com.sumavision.talktv2.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sumavision.talktv2.R;

/**
 * 收藏等删除单个提示
 * 
 * @author suma-hpb
 * 
 */
public class DeleteDialogFragment extends BaseDialogFragment implements
		OnClickListener {

	public interface OnClickDeleteListener {
		public void onDeleteClick();
	}

	public static DeleteDialogFragment newInstance(String text) {
		DeleteDialogFragment fragment = new DeleteDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(RES_ID, R.layout.dialog_delete);
		bundle.putString("text", text);
		fragment.setArguments(bundle);
		return fragment;
	}

	private OnClickDeleteListener listener;

	public void setOnClickDeleteListener(OnClickDeleteListener listener) {
		this.listener = listener;
	}

	private String text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, R.style.MyDialog);
		text = getArguments().getString("text");
	}

	@Override
	protected void initViews(View view) {
		TextView delView = (TextView) view.findViewById(R.id.tv_delete);
		delView.setText(text);
		delView.setOnClickListener(this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (listener != null) {
			listener.onDeleteClick();
		}

	}
}
