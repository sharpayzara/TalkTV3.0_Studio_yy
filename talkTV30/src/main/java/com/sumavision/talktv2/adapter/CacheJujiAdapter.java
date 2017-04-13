package com.sumavision.talktv2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.CacheInfo;
import com.sumavision.talktv2.bean.JiShuData;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

/**
 * 缓存剧集适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class CacheJujiAdapter extends IBaseAdapter<JiShuData> {

	private int type;
	private int pdVideoDefaultColor;
	ImageView moviePic;
	TextView movieCount,movieTitle;

	public CacheJujiAdapter(Context context, int type, List<JiShuData> objects) {
		super(context, objects);
		pdVideoDefaultColor = context.getResources().getColor(R.color.pd_color);
		this.type = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			switch (type) {
			case ProgramData.TYPE_MOVIE:
				convertView = inflater.inflate(R.layout.item_cache_movie, null);
				break;
			case ProgramData.TYPE_TV:
				convertView = inflater.inflate(R.layout.item_cache_tv, null);
				break;
			case 3:
				convertView = inflater.inflate(R.layout.item_cache_movie_pic_list2,null);
				break;
			default:
				break;
			}
		}
		TextView textView = ViewHolder.get(convertView, R.id.textView);
		RelativeLayout frame = ViewHolder.get(convertView, R.id.frame);
		ImageView imageView = ViewHolder.get(convertView, R.id.img);
		JiShuData jishuData = getItem(position);
		if (type == 3){
			moviePic = ViewHolder.get(convertView,R.id.movie_pic);
			movieCount = ViewHolder.get(convertView,R.id.movie_count);
			movieTitle = ViewHolder.get(convertView,R.id.movie_title);
			movieCount.setText(jishuData.playCount+"");
			movieCount.setVisibility(View.GONE);
			movieTitle.setText(jishuData.shortName);
			loadImage(moviePic, jishuData.pic, R.drawable.default_for_crop);
		}
		if (type == 3){
			textView.setText(jishuData.name);
		}else if(type == ProgramData.TYPE_TV){
			textView.setText(jishuData.shortName);
		}else if(type == ProgramData.TYPE_MOVIE){
			textView.setText((TextUtils.isEmpty(jishuData.shortName)?"":(jishuData.shortName+" : "))+jishuData.name);
		}
		CacheInfo cacheData = jishuData.cacheInfo;
		switch (cacheData.state) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
			frame.setBackgroundResource(R.drawable.item_programhalf_cached);
			imageView.setImageResource(R.drawable.cache_juji_focus);
			textView.setTextColor(context.getResources().getColor(R.color.light_black));
			break;
		case 6:
			frame.setBackgroundResource(R.drawable.item_programhalf);
			textView.setTextColor(pdVideoDefaultColor);
			imageView.setImageResource(R.drawable.cache_juji_focus);
			break;
		case 7:
			frame.setBackgroundResource(R.drawable.cache_selected_gray);
			imageView.setImageResource(R.drawable.cache_juji_unfocus_unable);
			break;
		case 5:
		default:
			frame.setBackgroundResource(R.drawable.item_programhalf);
			textView.setTextColor(pdVideoDefaultColor);
			imageView.setImageResource(R.drawable.cache_juji_unfocus);
			break;
		}
		return convertView;
	}
}
