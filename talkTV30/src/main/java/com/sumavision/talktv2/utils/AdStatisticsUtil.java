package com.sumavision.talktv2.utils;


import android.content.Context;
import android.util.Log;

import com.kugou.fanxing.core.FanxingManager;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.HandleAdvertiseRequest;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/5/8.
 */
public class AdStatisticsUtil {
    public static void adCount(Context context, int type){
        VolleyHelper.post(new HandleAdvertiseRequest(context,type).make(), new BaseJsonParser() {
            @Override
            public void parse(JSONObject jsonObject) {
                
            }
        },Constants.testHost2);
    }
    /*
    广告统计
    type 广告位
    adType 广告类型（0展示、1点击）
     */
    public static void adCount(Context context, int type, int adType){
        VolleyHelper.post(new HandleAdvertiseRequest(context,type,adType).make(), new BaseJsonParser() {
            @Override
            public void parse(JSONObject jsonObject) {

            }
        },Constants.testHost2);
    }
    public static void showJvxiaoLog(int type){
//        switch (type){
//            case 0://加载成功
//                Log.e("msg_jvxiao","load ad datas success");
//                break;
//            case 1://加载失败
//                Log.e("msg_jvxiao","load ad datas fail");
//                break;
//            case 2://广告曝光
//                Log.e("msg_jvxiao","show ad datas");
//                break;
//            case 3://广告点击
//                Log.e("msg_jvxiao","click ad datas");
//                break;
//        }
    }
}
