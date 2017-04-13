package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.PayHistoryData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 充值记录适配
 * 
 * @author suma-hpb
 * 
 */
public class PayHistoryAdapter extends IBaseAdapter<PayHistoryData> {

	public PayHistoryAdapter(Context context, List<PayHistoryData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.item_pay_history, null);
		}
		TextView numTxt = ViewHolder.get(arg1, R.id.purchasenum);
		TextView dateTxt = ViewHolder.get(arg1, R.id.paydate);
		numTxt.setText("" + getItem(arg0).diamond);
		dateTxt.setText(getItem(arg0).date);
		return arg1;
	}
}
