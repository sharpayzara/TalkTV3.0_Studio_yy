package com.sumavision.talktv2.dlna;

import java.util.List;

import org.cybergarage.upnp.Device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sumavision.talktv.videoplayer.R;

public class DeviceAdapter extends ArrayAdapter<Device> {

	public DeviceAdapter(Context context, List<Device> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.network_device_list_item,
					null);
			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.textView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Device temp = getItem(position);
		String name = temp.getFriendlyName();
		if (name != null)
			viewHolder.textView.setText(name);
		return convertView;
	}

	private static class ViewHolder {
		public TextView textView;
	}
}
