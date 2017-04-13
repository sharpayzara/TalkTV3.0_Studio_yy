package com.sumavision.talktv2.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mediav.ads.sdk.adcore.Mvad;
import com.mediav.ads.sdk.interfaces.IMvNativeAd;
import com.mediav.ads.sdk.interfaces.IMvNativeAdListener;
import com.mediav.ads.sdk.interfaces.IMvNativeAdLoader;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.SpecialProgramActivity;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.ProgramData;
import com.sumavision.talktv2.bean.VodProgramData;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.HandleAdvertiseRequest;
import com.sumavision.talktv2.utils.AdStatisticsUtil;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.ImageLoaderHelper;
import com.sumavision.talktv2.utils.ViewHolder;

import org.json.JSONObject;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 电影、电视剧等适配
 * 
 * @author suma-hpb
 * 
 */
public class LibBoardNormalAdapter extends BaseAdapter implements OnClickListener {
	Context mContext;
	LayoutInflater inflater;
	ArrayList<ArrayList<VodProgramData>> programs;
	ImageLoaderHelper imageLoader = new ImageLoaderHelper();
	int programType;
	int screenWidth;
	/**
	 *
	 * @param mContext
	 * @param programs
	 *            片库影片类型：电视剧、电影
	 */
	public LibBoardNormalAdapter(Context mContext, int programType,
								 ArrayList<ArrayList<VodProgramData>> programs) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		this.programs = programs;
		this.programType = programType;
		screenWidth = AppUtil.getScreenWidth(mContext);
		initMvAdLoader((Activity)mContext,"Fak6k8jyN6");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ArrayList<VodProgramData> itemList = getItem(position);
		if (itemList.size() == 1 && itemList.get(0).selected){
			convertView = inflater.inflate(R.layout.lib_board_title_list_item, null);
			LinearLayout titleLayout = ViewHolder.get(convertView,R.id.rmcd_main_list_item_top_);
			TextView titleView = ViewHolder.get(convertView,R.id.rmcd_main_list_item_top_name);
            ImageView img = ViewHolder.get(convertView, R.id.rmcd_main_list_item_top_arrow);
			titleView.setText(itemList.get(0).name);
			titleLayout.setTag(itemList.get(0));
            if (itemList.get(0).activityId>0){
                img.setVisibility(View.VISIBLE);
                titleLayout.setOnClickListener(this);
            }else {
                img.setVisibility(View.GONE);
            }
		}else {
			convertView = inflater.inflate(R.layout.item_lib_normal, null);
			RelativeLayout leftLayout = ViewHolder.get(convertView,
					R.id.layout_left);
			ImageView leftPicView = ViewHolder
					.get(convertView, R.id.imagv_pic_left);
			TextView leftInroView = ViewHolder.get(convertView, R.id.tv_intro_left);
			TextView leftTitleView = ViewHolder
					.get(convertView, R.id.tv_title_left);
			RelativeLayout rightLayout = ViewHolder.get(convertView,
					R.id.layout_right);
			ImageView rightPicView = ViewHolder.get(convertView,
					R.id.imagv_pic_right);
			TextView rightInroView = ViewHolder.get(convertView,
					R.id.tv_intro_right);
			TextView rightTitleView = ViewHolder.get(convertView,
					R.id.tv_title_right);
			TextView rightAdLogo = ViewHolder.get(convertView,
					R.id.mvad_logo);

			leftLayout.setOnClickListener(this);
			VodProgramData leftProgram = itemList.get(0);
			leftLayout.setTag(leftProgram);
			imageLoader.loadImage(leftPicView, leftProgram.pic,
					R.drawable.default_for_crop);
			leftTitleView.setText(leftProgram.name);
			if (isShort(programType)){
				leftTitleView.setText(leftProgram.subName);
			}
			setData(leftInroView, programType, leftProgram);

			if (itemList.size() == 1) {
				rightLayout.setVisibility(View.INVISIBLE);
			} else {
				VodProgramData rightProgram = itemList.get(1);
				if (!TextUtils.isEmpty(rightProgram.adId) && hasAdData()){
					IMvNativeAd temp;
					if (rightProgram.adObj != null){
						temp = (IMvNativeAd) rightProgram.adObj;
					} else {
						temp = getAdView();
					}
					if (temp != null){
						JSONObject obj = temp.getContent();
						imageLoader.loadImage(rightPicView, obj.optString("contentimg"),
								R.drawable.default_vertical);
						String title = obj.optString("title");
						SpannableString spanText = new SpannableString("推广  "+title);
						spanText.setSpan(new BackgroundColorSpan(Color.parseColor("#167FFF")), 0, 2,
								Spannable.SPAN_INCLUSIVE_INCLUSIVE);
						spanText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 2,
								Spannable.SPAN_INCLUSIVE_INCLUSIVE);
						rightTitleView.setText(spanText);
						rightInroView.setVisibility(View.GONE);
//						rightAdLogo.setVisibility(View.VISIBLE);
						rightLayout.setTag(R.id.jvxiao_ad_tag, temp);
						if (rightProgram.adObj == null){
							temp.onAdShowed();
							adCount(0);
							AdStatisticsUtil.showJvxiaoLog(2);
						}
						rightProgram.adObj = temp;
					}
				} else {
					imageLoader.loadImage(rightPicView, rightProgram.pic,
							R.drawable.default_for_crop);
					rightTitleView.setText(rightProgram.name);
					if (isShort(programType)){
						rightTitleView.setText(rightProgram.subName);
					}
					setData(rightInroView, programType, rightProgram);
					rightAdLogo.setVisibility(View.GONE);
					rightLayout.setTag(R.id.jvxiao_ad_tag, null);
				}
				rightLayout.setVisibility(View.VISIBLE);
				rightLayout.setOnClickListener(this);
				rightLayout.setTag(rightProgram);
			}
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return programs.size();
	}

	@Override
	public ArrayList<VodProgramData> getItem(int position) {
		return programs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.layout_left || v.getId() == R.id.layout_right) {
			if (v.getTag(R.id.jvxiao_ad_tag) != null){
				((IMvNativeAd)v.getTag(R.id.jvxiao_ad_tag)).onAdClicked();
				adCount(1);
				AdStatisticsUtil.showJvxiaoLog(3);
				return;
			}
			VodProgramData program = (VodProgramData) v.getTag();
			if (program.pcount <= 0) {
				openProgramActivity(program);
			} else {
				Intent intent = new Intent(mContext, SpecialProgramActivity.class);
				intent.putExtra("isSub", false);
				intent.putExtra("title", program.name);
				intent.putExtra("programId",
						Integer.parseInt(program.id));
				mContext.startActivity(intent);
			}
		} else if (v.getId() == R.id.rmcd_main_list_item_top_){
			VodProgramData program = (VodProgramData) v.getTag();
			if (program.picType == 1){
				//切换至其他标签
				EventMessage msg = new EventMessage("LibNormalFragment");
                msg.bundle.putInt("tagId",program.activityId);
				EventBus.getDefault().post(msg);
			}else if (program.picType == 2){
				//跳转专题
				Intent intent = new Intent(mContext, SpecialProgramActivity.class);
				intent.putExtra("isSub", false);
				intent.putExtra("title", program.name);
				intent.putExtra("columnId", program.activityId);
				mContext.startActivity(intent);
			}
		}
	}

	private void openProgramActivity(VodProgramData program) {
		String programId = String.valueOf(program.id);
		String topicId = program.topicId;
		openProgramDetailActivity(programId, topicId,
				program.name, 0,programType);
	}
	
	public void openProgramDetailActivity(String id, String topicId,
			String updateName, long cpId, int type) {
		Intent intent = new Intent(mContext, PlayerActivity.class);
		long programId = Long.valueOf(id);
		long longTopicId = 0;
		try {
			longTopicId = Long.valueOf(topicId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		intent.putExtra("id", programId);
		intent.putExtra("isHalf", true);
		intent.putExtra("topicId", longTopicId);
		intent.putExtra("cpId", cpId);
		intent.putExtra("updateName", updateName);
		intent.putExtra("where",2);
		intent.putExtra("ptype",type);

		mContext.startActivity(intent);
	}
	
	private void setData(TextView tv, int programType, VodProgramData v) {
		if (v == null) {
			tv.setVisibility(View.GONE);
			return;
		}
		String s = "";
		if (programType == ProgramData.TYPE_TV 
				|| programType == ProgramData.TYPE_DONGMAN) {
			s = v.updateName;
		} else if (programType == ProgramData.TYPE_MOVIE 
				|| programType == ProgramData.TYPE_MICRO_MOVIE) {
			if (TextUtils.isEmpty(v.point)) {
				s = "";
			} else {
				s = v.point + "分";
			}
		} else if (isShort(programType)) {
			s = v.name;
		}else {
			s = v.updateName;
		}
		
		if (!TextUtils.isEmpty(s)) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(s);
		} else {
			tv.setVisibility(View.GONE);
		}
	}
	private boolean isShort(int type){
		if (type == 24 || type ==22 || type == 9){
			return true;
		}
		return false;
	}
	//360聚效平台广告
	private ArrayList<IMvNativeAd> mvNativeAds = new ArrayList<IMvNativeAd>();
	private IMvNativeAd adView;
	private void initMvAdLoader(Activity context, String adSpaceid){
		nativeLoader = Mvad.initNativeAdLoader(context, adSpaceid, adListener, false);
//		nativeLoader.loadAds();
	}
	private IMvNativeAd getAdView(){
		IMvNativeAd result = null;
		if (mvNativeAds.size()>0){
			result = mvNativeAds.remove(0);
		}
		if (mvNativeAds.size()<1){
			nativeLoader.loadAds();
		}
		return result;
	}
	private IMvNativeAdLoader nativeLoader;
	private IMvNativeAdListener adListener = new IMvNativeAdListener() {

		@Override
		public void onNativeAdLoadSucceeded(ArrayList<IMvNativeAd> nativeAds) {
			mvNativeAds.addAll(nativeAds);
			notifyDataSetChanged();
			AdStatisticsUtil.showJvxiaoLog(0);
		}

		@Override
		public void onNativeAdLoadFailed() {
			AdStatisticsUtil.showJvxiaoLog(1);
		}
	};
	private boolean hasAdData(){
		if (mvNativeAds.size()>0){
			return true;
		} else {
			nativeLoader.loadAds();
		}
		return false;
	}
	private void adCount(int type){
		AdStatisticsUtil.adCount(mContext, 19, type);
	}
}
