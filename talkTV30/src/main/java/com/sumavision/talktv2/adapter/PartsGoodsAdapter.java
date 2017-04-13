package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.GoodsPiece;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.PreferencesUtils;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

/**
 * 碎片
 */
public class PartsGoodsAdapter extends IBaseAdapter<GoodsPiece> {

	public PartsGoodsAdapter(Context context,List<GoodsPiece> objects) {
		super(context,objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_parts_goods, null);
		}
		GoodsPiece piece = getItem(position);
		ImageView picView = ViewHolder.get(convertView, R.id.parts_goods_img);
		TextView nameText = ViewHolder.get(convertView, R.id.parts_goods_name);
		TextView countText = ViewHolder.get(convertView,
				R.id.parts_goods_count);
		TextView introText = ViewHolder.get(convertView, R.id.parts_goods_intro);
		nameText.setText(piece.goodsName);
		countText.setText(piece.hasCount+"张");
		introText.setText(piece.text);
		loadImage(picView, piece.pic, R.drawable.aadefault);
		return convertView;

	}
}
