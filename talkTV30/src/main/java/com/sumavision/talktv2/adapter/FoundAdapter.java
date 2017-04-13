package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.ExchangeLimitActivity;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.NewYearGuaActivity;
import com.sumavision.talktv2.bean.DiscoveryData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.ViewHolder;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 发现页适配器
 * 
 * @author yanzhidan
 * 
 */
public class FoundAdapter extends IBaseAdapter<DiscoveryData> {

	public FoundAdapter(Context context, List<DiscoveryData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DiscoveryData data = getItem(position);
		ImageView icon, contentImg;
		convertView = inflater.inflate(R.layout.item_found, null);
		icon = ViewHolder.get(convertView, R.id.type_icon);
		contentImg = ViewHolder.get(convertView, R.id.content_pic);

		if (data.discoveryType == DiscoveryData.TYPE_BADGE) {
			icon.setImageResource(R.drawable.ic_found_badge);
		} else if (data.discoveryType == DiscoveryData.TYPE_FESTIVAL) {
//			icon.setImageResource(R.drawable.ic_found_point);
		} else if (data.discoveryType == DiscoveryData.TYPE_GOODS) {
			icon.setImageResource(R.drawable.ic_found_shop);
		}
		contentImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (data.discoveryType == DiscoveryData.TYPE_BADGE) {
					MobclickAgent.onEvent(context, "fxhuodong", data.name);
					Intent intent = new Intent(context, ActivityActivity.class);
					intent.putExtra("activityId", (long) data.id);
					context.startActivity(intent);
				} else if (data.discoveryType == DiscoveryData.TYPE_FESTIVAL) {
					if (UserNow.current().userID > 0) {
						Intent intent = new Intent(context, NewYearGuaActivity.class);
						intent.putExtra("id", data.id);
						context.startActivity(intent);
					} else {
						Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);
					}
				} else if (data.discoveryType == DiscoveryData.TYPE_GOODS) {
					Intent intent = new Intent(context,
							ExchangeLimitActivity.class);
					intent.putExtra("hotGoodsId", (long) data.id);
					context.startActivity(intent);
				}

			}
		});
		loadImageCacheDisk(contentImg, data.pic,
				R.drawable.recommend_pic_default);

		return convertView;
	}

}
