package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.DiamondData;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 我的账号：充值列表适配
 * 
 * @author suma-hpb
 * 
 */
public class DiamondAdapter extends IBaseAdapter<DiamondData> {

	public DiamondAdapter(Context context, List<DiamondData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = inflater.inflate(R.layout.item_diamond, null);
		}
		TextView numTxt = (TextView) ViewHolder.get(arg1, R.id.purchasenum);
		TextView introTxt = (TextView) ViewHolder.get(arg1, R.id.purchaseintro);
		TextView priceTxt = (TextView) ViewHolder.get(arg1, R.id.purchaseprice);
		TextView purchaseTxt = (TextView) ViewHolder.get(arg1, R.id.purchase);
		numTxt.setText("" + getItem(arg0).num);
		introTxt.setText(getItem(arg0).intro);
		priceTxt.setText("￥" + getItem(arg0).price);
		DiamondData temp = getItem(arg0);
		purchaseTxt.setTag(temp);
		purchaseTxt.setOnClickListener((OnClickListener) context);
		return arg1;
	}
}
