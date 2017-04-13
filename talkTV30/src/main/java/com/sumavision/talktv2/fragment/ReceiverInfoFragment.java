package com.sumavision.talktv2.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.ReceiverInfo;

/**
 * 实体类礼品收货人信息对话框
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class ReceiverInfoFragment extends BaseDialogFragment implements
		OnClickListener {

	public static ReceiverInfoFragment newInstance(ReceiverInfo info) {
		ReceiverInfoFragment fragment = new ReceiverInfoFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("info", info);
		bundle.putInt("resId", R.layout.fragment_receiverinfo);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			info = getArguments().getParcelable("info");
		}
		setStyle(DialogFragment.STYLE_NORMAL, R.style.FullHeightDialog);
	}

	ReceiverInfo info;

	OnInputReceiverInfoListener mOnInputReceiverInfoListener;

	public void setOnInputReceiverInfoListener(
			OnInputReceiverInfoListener mOnInputReceiverInfoListener) {
		this.mOnInputReceiverInfoListener = mOnInputReceiverInfoListener;
	}

	private EditText nameEdit;

	private EditText addressEdit;

	private EditText phoneEdit;
	private EditText remarkEdit;
	private Button commit;

	protected void initViews(View view) {
		nameEdit = (EditText) rootView.findViewById(R.id.receiver_name);
		addressEdit = (EditText) rootView.findViewById(R.id.receiver_address);
		phoneEdit = (EditText) rootView.findViewById(R.id.receiver_phone);
		remarkEdit = (EditText) rootView.findViewById(R.id.receiver_remark);
		commit = (Button) rootView.findViewById(R.id.commit);
		commit.setOnClickListener(this);
		if (info != null) {
			nameEdit.setText(info.name);
			addressEdit.setText(info.address);
			phoneEdit.setText(info.phone);
		} else {
			info = new ReceiverInfo();
		}
	}

	@Override
	public void onClick(View v) {
		String name = nameEdit.getText().toString().trim();
		if (name.length() == 0) {
			Toast.makeText(getActivity(), "姓名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		info.name = name;
		String address = addressEdit.getText().toString().trim();
		if (address.length() == 0) {
			Toast.makeText(getActivity(), "地址不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		info.address = address;
		String phone = phoneEdit.getText().toString().trim();
		if (phone.length() != 11) {
			Toast.makeText(getActivity(), "请输入有效手机号", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		info.phone = phone;
		String remark = remarkEdit.getText().toString().trim();
		if (remark.length() > 0) {
			info.remark = remark;
		}
		if (mOnInputReceiverInfoListener != null) {
			mOnInputReceiverInfoListener.onUpdateInfo(info);
		}
		this.dismiss();
	}

	/**
	 * 更新中奖用户信息
	 * 
	 * @author hpb-16152
	 * @version 2014-6-5
	 * @since
	 */
	public interface OnInputReceiverInfoListener {
		public void onUpdateInfo(ReceiverInfo mReceiverInfo);
	}
}
