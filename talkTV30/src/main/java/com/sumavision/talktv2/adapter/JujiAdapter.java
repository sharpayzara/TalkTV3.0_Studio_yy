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
import com.sumavision.talktv2.utils.ViewHolder;

import java.util.List;

/**
 * jujilist、zongyi/tv等fragment适配：节目剧集
 *
 * @author suma-hpb
 * @description
 */
public class JujiAdapter extends IBaseAdapter<JiShuData> {
    public static final int TYPE_TV = 1;
    public static final int TYPE_MOVIE = 2;
    public static final int TYPE_PIC = 3;
    private int type;
    private int selectedPosition;

    private String name;

    public JujiAdapter(Context context, int type, List<JiShuData> objects) {
        super(context, objects);
        this.type = type;
    }

    @Deprecated
    public JujiAdapter(Context context, int type, List<JiShuData> objects, String name) {
        super(context, objects);
        this.type = type;
        this.name = name;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ImageView moviePic;
    TextView movieCount,movieTitle;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            switch (type) {
                case TYPE_TV:
                    convertView = inflater.inflate(R.layout.item_tv_grid, null);
                    break;
                case TYPE_MOVIE:
                    convertView = inflater.inflate(R.layout.item_movie_list, null);
                    break;
                case TYPE_PIC:
                    convertView = inflater.inflate(R.layout.item_movie_pic_list,null);
                    break;
                default:
                    break;
            }
        }
        JiShuData jishuData = getItem(position);
        TextView textView = ViewHolder.get(convertView, R.id.textView);
        RelativeLayout frame = ViewHolder.get(convertView, R.id.frame);
        TextView cacheView = ViewHolder.get(convertView, R.id.tv_cache);
        if (type == TYPE_PIC){
            moviePic = ViewHolder.get(convertView,R.id.movie_pic);
            movieCount = ViewHolder.get(convertView,R.id.movie_count);
            movieTitle = ViewHolder.get(convertView,R.id.movie_title);
            movieCount.setText(jishuData.playCount+"");
            if (jishuData.playCount>0){
                movieCount.setVisibility(View.VISIBLE);
            }else {
                movieCount.setVisibility(View.GONE);
            }
            movieTitle.setText(jishuData.shortName);
            loadImage(moviePic,jishuData.pic,R.drawable.default_for_crop);
        }
        if (type == TYPE_PIC){
            textView.setText(jishuData.name);
        }else if(type == TYPE_TV){
            textView.setText(jishuData.shortName);
        }else if(type == TYPE_MOVIE){
            textView.setText((TextUtils.isEmpty(jishuData.shortName)?"":(jishuData.shortName+" : "))+jishuData.name);
        }
        if (position == selectedPosition) {
//				frame.setBackgroundResource(R.drawable.pd_tv_item_focus);
            frame.setSelected(true);
            textView.setTextColor(context.getResources()
                    .getColor(R.color.white));
            if (type == TYPE_TV) {
                textView.setBackgroundResource(R.drawable.pd_tv_item_focus);
            }
            if (TYPE_PIC == type){
                movieCount.setTextColor(context.getResources()
                        .getColor(R.color.white));
                movieTitle.setTextColor(context.getResources()
                        .getColor(R.color.white));
                movieCount.setCompoundDrawablesWithIntrinsicBounds(
                        context.getResources().getDrawable(R.drawable.play_count_icon_selected),null,null,null);
            }
        } else {
//				frame.setBackgroundResource(R.drawable.pd_tv_item_bg);
            frame.setSelected(false);
            textView.setTextColor(context.getResources().getColor(
                    R.color.pd_color));
            if (type == TYPE_TV) {
                textView.setBackgroundResource(android.R.color.transparent);
            }
            if (TYPE_PIC == type){
                movieCount.setTextColor(context.getResources()
                        .getColor(R.color.pd_color));
                movieTitle.setTextColor(context.getResources()
                        .getColor(R.color.pd_color));
                movieCount.setCompoundDrawablesWithIntrinsicBounds(
                        context.getResources().getDrawable(R.drawable.play_count_icon), null, null, null);
            }
        }

        CacheInfo cacheData = jishuData.cacheInfo;
        if (cacheData != null) {
            switch (cacheData.state) {
                // 0 等待 //1正在下载 2已下完 3暂停 5 未添加 6 想要添加的
                case 2:
                    cacheView.setVisibility(View.VISIBLE);
                    if (position == selectedPosition) {
                        cacheView.setSelected(true);
                    } else {
                        cacheView.setSelected(false);
                    }
                    break;
                case 1:
                    break;
                default:
                    cacheView.setVisibility(View.GONE);
                    break;
            }
        }
        return convertView;
    }
}
