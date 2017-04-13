package com.sumavision.talktv2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediav.ads.sdk.adcore.Mvad;
import com.mediav.ads.sdk.interfaces.IMvNativeAd;
import com.mediav.ads.sdk.interfaces.IMvNativeAdListener;
import com.mediav.ads.sdk.interfaces.IMvNativeAdLoader;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ShakeActivity;
import com.sumavision.talktv2.activity.help.AlimamaHelper;
import com.sumavision.talktv2.bean.ColumnData;
import com.sumavision.talktv2.bean.RecommendCommonData;
import com.sumavision.talktv2.fragment.RecommendFragment;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.HandleAdvertiseRequest;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.ViewHolder;
import com.taobao.newxp.view.feed.Feed;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends
		IBaseAdapter<List<RecommendCommonData>> {
	private RecommendFragment fragment;
	private Context context;
	private final int slotid = 19;
	public Feed feed;

	public RecommendListAdapter(Context context,
			RecommendFragment recommendFragmentNew,
			List<List<RecommendCommonData>> recommendDatas,
			Feed feed) {
		super(context, recommendDatas);
		this.fragment = recommendFragmentNew;
		this.context = context;
		this.feed = feed;
		initMvAdLoader((Activity)context,"aPFGPIjeWs","PF5vad4erj");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		List<RecommendCommonData> datas = getItem(position);
		int size = datas.size();
		ImageView imageView, imageView1, imageView2, userPic1, userPic;
		TextView nameView, introView, titleView, subjectView;
		TextView nameView1, introView1, titleView1, subjectView1;
		TextView nameView2, introView2, titleView2, subjectView2;
		
		if (size == 1) {
			RecommendCommonData recommendCommonData = datas.get(0);
			if (recommendCommonData.getColumnData() == null) {
				convertView = inflater.inflate(
						R.layout.item_rcmd_column_vertical, null);
			} else {
				ColumnData data = recommendCommonData.getColumnData();
				if (data.type != slotid) {
					convertView = inflater.inflate(R.layout.rcmd_main_list_item,
							null);
					TextView textView = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_name);
					TextView moreView = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_arrow_tips);
					TextView moreView1 = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_arrow_tips1);
					TextView moreView2 = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_arrow_tips2);
					ImageView iconView = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_pic);
					ImageView arrow = ViewHolder.get(convertView,
							R.id.rmcd_main_list_item_top_arrow);
					arrow.setImageResource(R.drawable.arrow_more_red);
					fillColumn(data, moreView, iconView);
					textView.setText("" + data.name);
					convertView.setTag(R.id.tag_stickyheader_content, data);
					convertView.setOnClickListener(fragment);
					moreView.setVisibility(View.GONE);
					moreView1.setVisibility(View.GONE);
					moreView2.setVisibility(View.GONE);
					if (data.recommendTags != null && data.recommendTags.size()>0){
						for (int i=0; i<data.recommendTags.size(); i++){
							if (i==0){
								moreView.setText(data.recommendTags.get(i).name);
								moreView.setVisibility(View.VISIBLE);
								moreView.setOnClickListener(fragment);
								moreView.setTag(R.id.tag_stickyheader_content, data);
							}else if(i==1){
								moreView1.setText(data.recommendTags.get(i).name);
								moreView1.setVisibility(View.VISIBLE);
								moreView1.setOnClickListener(fragment);
								moreView1.setTag(R.id.tag_stickyheader_content, data);
							}else if(i==2){
								moreView2.setText(data.recommendTags.get(i).name);
								moreView2.setVisibility(View.VISIBLE);
								moreView2.setOnClickListener(fragment);
								moreView2.setTag(R.id.tag_stickyheader_content, data);
							}
						}
					}
					return convertView;
				} else if (data.type == slotid) {
					convertView = inflater.inflate(R.layout.taobao_layout,
							null);
					RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
					AlimamaHelper alimamaHelper = new AlimamaHelper((Activity)context);
					View view = alimamaHelper.getFeedView(feed);
					if (view != null) {
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
								 LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
						View child0 = layout.getChildAt(0);
						lp.addRule(RelativeLayout.BELOW, child0.getId());
						view.setLayoutParams(lp);
						layout.addView(view);
						layout.setVisibility(View.VISIBLE);
					} else {
						layout.setVisibility(View.GONE);
					}
			        return convertView;
				}
			}
		} else if (size == 3) {
			RecommendCommonData recommendCommonData = datas.get(0);
			if (recommendCommonData.columnPicType == ColumnData.PIC_TYPE_THREE) {
				convertView = inflater.inflate(R.layout.item_rcmd_column_three,
						null);
			} else {
				convertView = inflater.inflate(
						R.layout.item_rcmd_column_horizontal, null);
			}
			subjectView1 = ViewHolder.get(convertView, R.id.tv_special_flag1);
			imageView1 = ViewHolder.get(convertView, R.id.imageView1);
			nameView1 = ViewHolder.get(convertView, R.id.name1);
			introView1 = ViewHolder.get(convertView, R.id.intro1);
			titleView1 = ViewHolder.get(convertView, R.id.title1);
			subjectView2 = ViewHolder.get(convertView, R.id.tv_special_flag2);
			imageView2 = ViewHolder.get(convertView, R.id.imageView2);
			nameView2 = ViewHolder.get(convertView, R.id.name2);
			introView2 = ViewHolder.get(convertView, R.id.intro2);
			titleView2 = ViewHolder.get(convertView, R.id.title2);
			fillContent(imageView1, nameView1, introView1, titleView1,
					subjectView1, datas.get(1));
			fillContent(imageView2, nameView2, introView2, titleView2,
					subjectView2, datas.get(2));
		} else if (size == 2) {
			RecommendCommonData commonData = datas.get(1);
			ColumnData data = commonData.getColumnData();
			convertView = inflater.inflate(R.layout.item_rcmd_column_normal,
					null);
			imageView1 = ViewHolder.get(convertView, R.id.imageView1);
			nameView1 = ViewHolder.get(convertView, R.id.name1);
			introView1 = ViewHolder.get(convertView, R.id.intro1);
			titleView1 = ViewHolder.get(convertView, R.id.title1);
			userPic1 = ViewHolder.get(convertView, R.id.user_pic1);
			subjectView1 = ViewHolder.get(convertView, R.id.tv_special_flag1);
			fillContent(imageView1, userPic1, nameView1, introView1, titleView1,
					subjectView1, commonData);
			if (userPic1!=null){
				if (commonData.type == 21 && TextUtils.isEmpty(commonData.adId)){
					userPic1.setVisibility(View.VISIBLE);
				}else {
					userPic1.setVisibility(View.GONE);
				}
			}
		}
		RecommendCommonData commonData = datas.get(0);
		ColumnData data = commonData.getColumnData();
		userPic = ViewHolder.get(convertView, R.id.user_pic);
		imageView = ViewHolder.get(convertView, R.id.imageView);
		nameView = ViewHolder.get(convertView, R.id.name);
		introView = ViewHolder.get(convertView, R.id.intro);
		titleView = ViewHolder.get(convertView, R.id.title);
		subjectView = ViewHolder.get(convertView, R.id.tv_special_flag);
		if(!TextUtils.isEmpty(commonData.name) && commonData.name.equals("shake_shake")){
			loadImage(imageView, "", R.drawable.shake_entry_recommend);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					context.startActivity(new Intent(context, ShakeActivity.class));
					MobclickAgent.onEvent(context, "tjyaojiapian");
				}
			});
			nameView.setVisibility(View.GONE);
			introView.setVisibility(View.GONE);
			titleView.setVisibility(View.GONE);
			subjectView.setVisibility(View.GONE);
		}else{
			fillContent(imageView, userPic, nameView, introView, titleView, subjectView,
					commonData);
		}
		if(userPic != null){
			if (commonData.type == 21 && TextUtils.isEmpty(commonData.adId)){
				userPic.setVisibility(View.VISIBLE);
			}else {
				userPic.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	private void fillColumn(ColumnData data, TextView moreView,
			ImageView iconView) {
		int p = data.parameter;
		if (p != 0) {
			if (data.type == ColumnData.TYPE_LIVE) {
				moreView.setText(p + "个节目源");
			} else if (data.type == ColumnData.TYPE_ACTIVITY) {
				moreView.setText(p + "人参与");
			} else {
				moreView.setText(p + "个节目更新");
			}
		} else {
			moreView.setText("更多");
		}
		if (data.type == 21){
			moreView.setText("进入");
		}
		int defaultResId = R.drawable.rcmd_all_tips_pic;
		if (data.type == ColumnData.TYPE_LIVE) {
			defaultResId = R.drawable.recommend_live;
		}
		if (data.name.equals("电视剧")) {
			defaultResId = R.drawable.recommend_tv;
		} else if (data.name.equals("每日电影")) {
			defaultResId = R.drawable.recommend_hot_movie;
		} else if (data.name.equals("新闻专区")) {
			defaultResId = R.drawable.recommend_news;
		} else if (data.name.equals("公开课")) {

		} else if (data.name.equals("排行榜")) {

		} else if (data.name.equals("纪录片")) {

		} else if (data.name.equals("体育")) {

		} else if (data.name.equals("微电影")) {

		} else if (data.name.equals("音乐")) {

		} else if (data.name.equals("海外剧")) {
			defaultResId = R.drawable.recommend_oversea;
		} else if (data.name.equals("综艺")) {
			defaultResId = R.drawable.recommend_zongyi;
		} else if (data.name.equals("纪录片")) {
			defaultResId = R.drawable.recommend_record;
		} else if (data.name.contains("动漫")) {
			defaultResId = R.drawable.recommend_cartoon;
		} else if (data.name.contains("游戏")) {
			defaultResId = R.drawable.recommend_game;
		} else if (data.type == 21){
			defaultResId = R.drawable.recommend_shake_icon;
		}
		loadImageCacheDisk(iconView, data.icon, defaultResId);

	}

	private void fillContent(ImageView img, TextView name, TextView intro,
			TextView title, TextView subject, final RecommendCommonData data) {
		if (data != null) {
			if (!TextUtils.isEmpty(data.adId)){
				final IMvNativeAd temp;
				if ( data.adObj != null){
					temp = (IMvNativeAd) data.adObj;
				} else {
					temp = getAdView(data.adId);
				}
				if (temp != null){
					JSONObject obj = temp.getContent();
					loadImageCacheDisk(img, obj.optString("contentimg") + "", R.drawable.rcmd_main_item_pic_normal);
					name.setText(obj.optString("title") + "");
					title.setVisibility(View.GONE);
					intro.setVisibility(View.VISIBLE);
					intro.setText(obj.optString("desc") + "");
					subject.setVisibility(View.VISIBLE);
					subject.setText("推广");
					if (data.adObj == null){
						temp.onAdShowed();
						adCount(data.adId,0);
						AdStatisticsUtil.showJvxiaoLog(2);
					}
					data.adObj = temp;
					img.setTag(data);
					img.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							temp.onAdClicked();
							adCount(data.adId,1);
							AdStatisticsUtil.showJvxiaoLog(3);
						}
					});
					return;
				}
			}
			String url = data.icon;
			if (!TextUtils.isEmpty(data.pic)) {
				url = data.pic;
			}
			if (!TextUtils.isEmpty(data.adId)){
				url = "";
			}
			loadImageCacheDisk(img, url, R.drawable.rcmd_main_item_pic_normal);
			if (!TextUtils.isEmpty(data.name)) {
				name.setText("" + data.name);
				name.setVisibility(View.VISIBLE);
			} else {
				name.setVisibility(View.GONE);
			}

			if (!TextUtils.isEmpty(data.title)) {
				title.setText(data.title);
				title.setVisibility(View.VISIBLE);
			} else {
				title.setVisibility(View.GONE);
			}

			if (!TextUtils.isEmpty(data.intro) && !data.intro.contains("null")) {
				intro.setText(data.intro);
				intro.setVisibility(View.VISIBLE);
			} else {
				intro.setVisibility(View.GONE);
			}
			img.setTag(data);
			img.setOnClickListener(fragment);
			subject.setVisibility(data.subject ? View.VISIBLE : View.GONE);

		}
	}
	
	private void fillContent(ImageView img, ImageView userPic, TextView name, TextView intro,
			TextView title, TextView subject, RecommendCommonData data) {
		fillContent(img, name, intro, title, subject, data);
		if (!TextUtils.isEmpty(data.userPic) && userPic != null) {
			loadImage(userPic, data.userPic, R.drawable.icon);
		}
	}

	//360聚效平台广告
	private ArrayList<IMvNativeAd> mvNativeAds = new ArrayList<IMvNativeAd>();
	private ArrayList<IMvNativeAd> mvNativeAds2 = new ArrayList<IMvNativeAd>();
	private IMvNativeAd adView;
	private void initMvAdLoader(Activity context, String adSpaceid,String adSpaceid2){
		adListener1 = new MyAdlistener("aPFGPIjeWs");
		adListener2 = new MyAdlistener("PF5vad4erj");
		nativeLoader1 = Mvad.initNativeAdLoader(context, adSpaceid, adListener1, false);
		nativeLoader2 = Mvad.initNativeAdLoader(context, adSpaceid2, adListener2, false);
//		nativeLoader1.loadAds();
//		nativeLoader2.loadAds();
	}
	private IMvNativeAd getAdView(String id){
		IMvNativeAd result = null;
		if ("aPFGPIjeWs".equals(id)){
			if (mvNativeAds.size()>0){
				result = mvNativeAds.remove(0);
			}
			if (mvNativeAds.size()<1){
				nativeLoader1.loadAds(5);
			}
		} else if ("PF5vad4erj".equals(id)) {
			if (mvNativeAds2.size()>0){
				result = mvNativeAds2.remove(0);
			}
			if (mvNativeAds2.size()<1){
				nativeLoader2.loadAds(5);
			}
		}
		if (fragment.refreshTimes>0 && fragment.refreshTimes%2 == 0){
			AdStatisticsUtil.showJvxiaoLog(0);
		}
		return result;

	}
	private IMvNativeAdLoader nativeLoader1,nativeLoader2;
	private MyAdlistener adListener1,adListener2;
	private class MyAdlistener implements  IMvNativeAdListener{
		public String id;
		public MyAdlistener(String id){
			this.id = id;
		}
		@Override
		public void onNativeAdLoadSucceeded(ArrayList<IMvNativeAd> arrayList) {
			if ("aPFGPIjeWs".equals(id)){
				mvNativeAds.addAll(arrayList);
			} else if ("PF5vad4erj".equals(id)){
				mvNativeAds2.addAll(arrayList);
			}
			notifyDataSetChanged();
			if (fragment.refreshTimes == 0){
				AdStatisticsUtil.showJvxiaoLog(0);
			}
		}

		@Override
		public void onNativeAdLoadFailed() {
			if (fragment.refreshTimes == 0){
				AdStatisticsUtil.showJvxiaoLog(1);
			}
		}
	}

	private void adCount(String adId, int type){
		if (adId.equals("aPFGPIjeWs")){
			AdStatisticsUtil.adCount(context, 16, type);
		} else if (adId.equals("PF5vad4erj")){
			AdStatisticsUtil.adCount(context, 17, type);
		}

	}

}
