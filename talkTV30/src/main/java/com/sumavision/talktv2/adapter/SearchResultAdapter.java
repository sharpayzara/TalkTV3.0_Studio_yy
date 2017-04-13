package com.sumavision.talktv2.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.utils.ViewHolder;
import com.sumavision.talktv2.widget.sticky.StickyListHeadersAdapter;

/**
 * 搜索结果适配
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class SearchResultAdapter extends IBaseAdapter<VodProgramData> implements
		StickyListHeadersAdapter {

	public SearchResultAdapter(Context context, List<VodProgramData> objects) {
		super(context, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search_result, null);
		}
		ImageView pic = ViewHolder.get(convertView, R.id.pic);
		TextView name = ViewHolder.get(convertView, R.id.name);
		TextView type = ViewHolder.get(convertView, R.id.type);
		TextView subName = ViewHolder.get(convertView, R.id.tv_sub_name);
		TextView moreTxt = ViewHolder.get(convertView, R.id.more);
		LinearLayout normalView = ViewHolder.get(convertView,
				R.id.llayout_normal);
		VodProgramData vpd = getItem(position);
		boolean moreProgram = TextUtils.isEmpty(vpd.id)
				&& vpd.subSearchType == VodProgramData.SEARCH_TYPE_PROGRAM;
		boolean moreVideo = TextUtils.isEmpty(vpd.subId)
				&& vpd.subSearchType == VodProgramData.SEARCH_TYPE_VIDEO;
		if (moreProgram || moreVideo) {
			pic.setVisibility(View.GONE);
			moreTxt.setText(vpd.name);
			moreTxt.setVisibility(View.VISIBLE);
			normalView.setVisibility(View.GONE);
		} else {
			pic.setVisibility(View.VISIBLE);
			moreTxt.setVisibility(View.GONE);
			normalView.setVisibility(View.VISIBLE);
			String typeName = VodProgramData.ProgramType.getTypeName(vpd.ptype);
			if (typeName != null) {
				name.setText(vpd.name);
				type.setVisibility(View.VISIBLE);
				type.setText(typeName);
			} else {
				name.setText(vpd.name);
				type.setVisibility(View.GONE);
			}
			String url = vpd.pic;
			if (vpd.subSearchType == VodProgramData.SEARCH_TYPE_PROGRAM) {
				loadImage(pic, url, R.drawable.recommend_default);
				subName.setVisibility(View.GONE);
			} else {
				loadImage(pic, null, R.drawable.search_video_bg);
				subName.setText(vpd.subName);
				subName.setVisibility(View.VISIBLE);

			}
		}
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_search_head, null);
		}
		TextView titleName = ViewHolder.get(convertView,
				R.id.search_group_title_name);
		VodProgramData vtp = getItem(position);
		if (vtp.subSearchType == VodProgramData.SEARCH_TYPE_PROGRAM) {
			titleName.setText("节目影片");
		} else {
			titleName.setText("综合视频");
		}
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return getItem(position).subSearchType + 1;
	}
}
