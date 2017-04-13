package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ExchangeLimitActivity;
import com.sumavision.talktv2.bean.HotGoodsBean;
import com.sumavision.talktv2.bean.ShoppingHomeBean;
import com.sumavision.talktv2.utils.Constants;

public class ShopHomeAdapter extends IBaseAdapter<ShoppingHomeBean> implements
		OnClickListener {

	public ShopHomeAdapter(Context context, List<ShoppingHomeBean> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ShoppingHomeBean shophome = getItem(position);

		convertView = inflater.inflate(R.layout.item_shopping_home_new, null);
		
		RelativeLayout shopLeft = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.shop_left);
		ImageView leftIv = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.left_iv);
		TextView leftName = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.left_name);
		TextView leftScore = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.left_score_txt);
		RelativeLayout shopRight1 = com.sumavision.talktv2.utils.ViewHolder
				.get(convertView, R.id.shop_right);
		ImageView rightIv1 = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.right_iv);
		TextView rightName1 = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.right_name);
		TextView rightScore1 = com.sumavision.talktv2.utils.ViewHolder.get(
				convertView, R.id.right_score_txt);
		
		if (shophome.hotGoods.size() >= 2) {
			loadView(shopLeft, leftIv, leftName, leftScore,
					shophome.hotGoods.get(0));
			loadView(shopRight1, rightIv1, rightName1, rightScore1,
					shophome.hotGoods.get(1));
			shopLeft.setOnClickListener(this);
			shopRight1.setOnClickListener(this);
		} else if (shophome.hotGoods.size() < 2) {
			loadView(shopLeft, leftIv, leftName, leftScore,
					shophome.hotGoods.get(0));
			shopLeft.setOnClickListener(this);
			shopRight1.setVisibility(View.GONE);
		}

		return convertView;
	}

	private void loadView(RelativeLayout layout, ImageView iv, TextView name,
			TextView score, HotGoodsBean hotgoods) {
		layout.setVisibility(View.VISIBLE);
		layout.setTag(hotgoods.id);
		String picUrl = Constants.picUrlFor + hotgoods.pic + "b.jpg";
		loadImage(iv, picUrl, R.drawable.recommend_default);
		name.setText(hotgoods.name);
		score.setText(hotgoods.point + "积分");
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(context, ExchangeLimitActivity.class);
		intent.putExtra("hotGoodsId", (Long) v.getTag());
		context.startActivity(intent);
	}
}
