package com.sumavision.talktv2.http.request;

import android.content.Context;

import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.GetRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.ChannelDetailCallback;
import com.sumavision.talktv2.http.callback.ChannelListCallback;
import com.sumavision.talktv2.http.callback.ChannelTypeListCallback;
import com.sumavision.talktv2.http.callback.ChaseListCallback;
import com.sumavision.talktv2.http.callback.ColumnVideoListCallback;
import com.sumavision.talktv2.http.callback.DianzanCallback;
import com.sumavision.talktv2.http.callback.DianzanGetCallback;
import com.sumavision.talktv2.http.callback.EmergencyDetailCallback;
import com.sumavision.talktv2.http.callback.EmergencyZoneCallback;
import com.sumavision.talktv2.http.callback.PlayCountCallback;
import com.sumavision.talktv2.http.callback.ProgramDetailCallback;
import com.sumavision.talktv2.http.callback.ProgramHeaderCallback;
import com.sumavision.talktv2.http.callback.ProgramMicroVideoCallback;
import com.sumavision.talktv2.http.callback.RecommendDetailCallback;
import com.sumavision.talktv2.http.callback.ShakePlayCountCallback;
import com.sumavision.talktv2.http.callback.StarDetailCallback;
import com.sumavision.talktv2.http.listener.OnChannelDetailListener;
import com.sumavision.talktv2.http.listener.OnChannelListListener;
import com.sumavision.talktv2.http.listener.OnChannelTypeListListener;
import com.sumavision.talktv2.http.listener.OnChaseListListener;
import com.sumavision.talktv2.http.listener.OnColumnVideoListLitener;
import com.sumavision.talktv2.http.listener.OnDianzanGetListener;
import com.sumavision.talktv2.http.listener.OnDianzanListener;
import com.sumavision.talktv2.http.listener.OnEmergencyDetailListener;
import com.sumavision.talktv2.http.listener.OnEmergencyZoneListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnPlayCountListener;
import com.sumavision.talktv2.http.listener.OnProgramDetailListener;
import com.sumavision.talktv2.http.listener.OnProgramHeaderListener;
import com.sumavision.talktv2.http.listener.OnProgramMicroVideoListener;
import com.sumavision.talktv2.http.listener.OnRecommendDetailListener;
import com.sumavision.talktv2.http.listener.OnStarDetailListener;

/**
 * 请求：频道、节目、明星等相关信息
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class VolleyProgramRequest extends VolleyRequest {

	/**
	 * 推荐页的2 3 4 标签
	 * 
	 * @param columnId
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void columnVideoList(int columnId, int first, int count,
			OnColumnVideoListLitener listener, OnHttpErrorListener errorListener) {

		BaseRequest request = new BaseRequest(url, new ColumnVideoListCallback(
				errorListener, columnId, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);

	}


	/**
	 * 频道列表
	 * 
	 * @param userId
	 * @param typeId
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void channelList(int userId, int typeId, int first,
			int count, OnChannelListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChannelListCallback(
				errorListener, userId, typeId, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * emergencyDetail
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void emergencyDetail(OnEmergencyDetailListener listener,
			OnHttpErrorListener errorListener, long objectId, long zoneId,
			int type, int way) {
		BaseRequest request = new BaseRequest(url, new EmergencyDetailCallback(
				errorListener, listener, objectId, zoneId, type, way)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * emergencyZone
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void emergencyZone(OnEmergencyZoneListener listener,
			OnHttpErrorListener errorListener, int zoneId) {
		BaseRequest request = new BaseRequest(url, new EmergencyZoneCallback(
				errorListener, listener, zoneId)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 频道详情
	 * 
	 * @param userId
	 * @param channelId
	 * @param date
	 * @param listener
	 * @param errorListener
	 */
	public static void getChannelDetail(int userId, int channelId, String date,
			OnChannelDetailListener listener, int whichDayType,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChannelDetailCallback(
				errorListener, userId, date, channelId, whichDayType, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	public static void getFavDetail(int userId, int first, int count,
			Context context, OnChaseListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChaseListCallback(
				errorListener, context, listener, userId, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 节目详情
	 * 
	 * @param programId
	 * @param listener
	 * @param errorListener
	 */
	public static void getProgramDetail(long programId,
			OnProgramDetailListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ProgramDetailCallback(
				errorListener, programId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 
	 * @param programId
	 * @param cpId
	 * @param listener
	 * @param errorListener
	 */
	public static void getProgramHeader(long programId, long cpId,
			OnProgramHeaderListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ProgramHeaderCallback(
				errorListener, programId, cpId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 明星信息
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void getStarDetail(OnStarDetailListener listener,
			int stagerId, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new StarDetailCallback(
				errorListener, stagerId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 播放次数统计
	 * 
	 * @param listener
	 * @param programId
	 * @param subId
	 * @param channelId
	 * @param errorListener
	 */
	public static void playCount(Context context, OnPlayCountListener listener, int programId,int subId,
			int channelId,String version, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new PlayCountCallback(context,
				errorListener, programId, subId, channelId,version, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
	public static void shakePlayCount(OnPlayCountListener listener, Context context,int programId,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ShakePlayCountCallback(
				errorListener, context, programId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 
	 * @param listener
	 * @param url
	 * @param errorListener
	 */
	public static void ProgramMicroVideo(OnProgramMicroVideoListener listener,
			String url, OnHttpErrorListener errorListener) {
		GetRequest request = new GetRequest(url, new ProgramMicroVideoCallback(
				errorListener, listener));
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 首页推荐
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void recommendDetail(OnRecommendDetailListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new RecommendDetailCallback(
				errorListener, listener)) {
		};
		request.setTag("recommendDetail");
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 
	 * @param listener
	 * @param errorListener
	 * @param umengValue
	 */
	public static void channelType(OnChannelTypeListListener listener,
			OnHttpErrorListener errorListener, String umengValue) {
		BaseRequest request = new BaseRequest(url, new ChannelTypeListCallback(
				errorListener, listener, umengValue)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 点赞信息获取
	 * 
	 * @param errorListener
	 * @param programSubId
	 * @param mListener
	 */
	public static void getDianzan(OnHttpErrorListener errorListener,
			Context context, int programSubId, OnDianzanGetListener mListener) {
		BaseRequest request = new BaseRequest(url, new DianzanGetCallback(
				context, errorListener, programSubId, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 点赞
	 * 
	 * @param context
	 * @param errorListener
	 * @param programSubId
	 * @param type
	 *            1-赞,2-踩
	 * @param mListener
	 */
	public static void dianzan(Context context,
			OnHttpErrorListener errorListener, int programSubId, int type,
			OnDianzanListener mListener) {
		BaseRequest request = new BaseRequest(url, new DianzanCallback(
				errorListener, type, programSubId, context, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}
}
