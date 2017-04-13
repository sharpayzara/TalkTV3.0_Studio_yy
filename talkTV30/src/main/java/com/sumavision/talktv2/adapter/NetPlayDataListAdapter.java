package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.NetworkLiveBean;
import com.sumavision.talktv2.utils.ViewHolder;

/**
 * 网络地址列表适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class NetPlayDataListAdapter extends IBaseAdapter<NetworkLiveBean> {
	
	private int selectedPosition;

	public NetPlayDataListAdapter(Context context, List<NetworkLiveBean> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_net_live, null);
		}
		TextView textView = ViewHolder.get(convertView, R.id.name);
		ImageView imageView = ViewHolder.get(convertView, R.id.pic);
		NetworkLiveBean temp = getItem(position);
		String name = temp.netLiveText;
		if (name != null) {
			textView.setText(name);
		}
		if (temp.netLiveImg > 0) {
			imageView.setImageResource(temp.netLiveImg);
		} else {
			imageView.setVisibility(View.GONE);
		}
		if (selectedPosition == position) {
			textView.setSelected(true);
		} else {
			textView.setSelected(false);
		}
		return convertView;
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
}
