package com.sumavision.talktv2.http.request;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.sumavision.talktv2.bean.FeedbackData;
import com.sumavision.talktv2.bean.ThirdPlatInfo;
import com.sumavision.talktv2.bean.UserModify;
import com.sumavision.talktv2.http.BaseRequest;
import com.sumavision.talktv2.http.VolleyQueueManage;
import com.sumavision.talktv2.http.callback.AddGuanzhuCallback;
import com.sumavision.talktv2.http.callback.AddRemindCallback;
import com.sumavision.talktv2.http.callback.BadgeDetailCallback;
import com.sumavision.talktv2.http.callback.BindAccountCallback;
import com.sumavision.talktv2.http.callback.BindDeleteCallback;
import com.sumavision.talktv2.http.callback.BindLogInCallback;
import com.sumavision.talktv2.http.callback.ChangePasswdCallback;
import com.sumavision.talktv2.http.callback.ChaseDeleteCallback;
import com.sumavision.talktv2.http.callback.ChaseListCallback;
import com.sumavision.talktv2.http.callback.ChaseProgramCallback;
import com.sumavision.talktv2.http.callback.CheckCodeCheckCallback;
import com.sumavision.talktv2.http.callback.DeleteGuanzhuCallback;
import com.sumavision.talktv2.http.callback.DeleteRemindCallback;
import com.sumavision.talktv2.http.callback.EventRoomCallback;
import com.sumavision.talktv2.http.callback.FeedbackCallback;
import com.sumavision.talktv2.http.callback.ForgetInitCallback;
import com.sumavision.talktv2.http.callback.LogInCallback;
import com.sumavision.talktv2.http.callback.LogOffCallback;
import com.sumavision.talktv2.http.callback.MailCallback;
import com.sumavision.talktv2.http.callback.MyBadgeCallback;
import com.sumavision.talktv2.http.callback.MyFollowCallback;
import com.sumavision.talktv2.http.callback.MyPrivateMsgCallback;
import com.sumavision.talktv2.http.callback.OtherFansCallback;
import com.sumavision.talktv2.http.callback.OtherSpaceCallback;
import com.sumavision.talktv2.http.callback.ReSendEmailCallback;
import com.sumavision.talktv2.http.callback.RecommendAppCallback;
import com.sumavision.talktv2.http.callback.RecommendUserListCallback;
import com.sumavision.talktv2.http.callback.RegisterCallback;
import com.sumavision.talktv2.http.callback.RemindListCallback;
import com.sumavision.talktv2.http.callback.SendMailCallback;
import com.sumavision.talktv2.http.callback.UserInfoQueryCallback;
import com.sumavision.talktv2.http.callback.UserUpdateCallback;
import com.sumavision.talktv2.http.listener.OnAddGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnAddRemindListener;
import com.sumavision.talktv2.http.listener.OnBadgeDetailListener;
import com.sumavision.talktv2.http.listener.OnBindAccountListener;
import com.sumavision.talktv2.http.listener.OnBindDeleteListener;
import com.sumavision.talktv2.http.listener.OnBindLogInListener;
import com.sumavision.talktv2.http.listener.OnChangePasswdListener;
import com.sumavision.talktv2.http.listener.OnChaseDeleteListener;
import com.sumavision.talktv2.http.listener.OnChaseListListener;
import com.sumavision.talktv2.http.listener.OnChaseProgramListener;
import com.sumavision.talktv2.http.listener.OnCheckCodeCheckListener;
import com.sumavision.talktv2.http.listener.OnDeleteGuanzhuListener;
import com.sumavision.talktv2.http.listener.OnDeleteRemindListener;
import com.sumavision.talktv2.http.listener.OnEventRoomListener;
import com.sumavision.talktv2.http.listener.OnFeedbackListener;
import com.sumavision.talktv2.http.listener.OnForgetInitListener;
import com.sumavision.talktv2.http.listener.OnHttpErrorListener;
import com.sumavision.talktv2.http.listener.OnLogInListener;
import com.sumavision.talktv2.http.listener.OnLogOffListener;
import com.sumavision.talktv2.http.listener.OnMailListListener;
import com.sumavision.talktv2.http.listener.OnMyBadgeListener;
import com.sumavision.talktv2.http.listener.OnMyFansListener;
import com.sumavision.talktv2.http.listener.OnMyFollowListener;
import com.sumavision.talktv2.http.listener.OnMyPrivateMsgListener;
import com.sumavision.talktv2.http.listener.OnOtherSpaceListener;
import com.sumavision.talktv2.http.listener.OnReSendEmailListener;
import com.sumavision.talktv2.http.listener.OnRecommendAppListener;
import com.sumavision.talktv2.http.listener.OnRecommendUserListListener;
import com.sumavision.talktv2.http.listener.OnRegisterListener;
import com.sumavision.talktv2.http.listener.OnRemindListListener;
import com.sumavision.talktv2.http.listener.OnSendMailListener;
import com.sumavision.talktv2.http.listener.OnUserInfoQueryListener;
import com.sumavision.talktv2.http.listener.OnUserUpdateListener;

/**
 * 用户相关通信请求：追剧、预约、关注、收藏频道等、登录、绑定、私信
 * 
 * @author suma-hpb
 * @version
 * @description
 */
public class VolleyUserRequest extends VolleyRequest {

	/**
	 * 用户信息查询：积分、宝石
	 * 
	 * @param errorListener
	 * @param mContext
	 * @param mListener
	 */
	public static void queryInfo(OnHttpErrorListener errorListener,
			Context mContext, OnUserInfoQueryListener mListener) {
		BaseRequest request = new BaseRequest(url, new UserInfoQueryCallback(
				errorListener, mContext, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 添加关注
	 * 
	 * @param userId
	 * @param listener
	 * @param errorListener
	 */
	public static void addGuanzhu(int userId, OnAddGuanzhuListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new AddGuanzhuCallback(
				userId, listener, errorListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 添加预约
	 * 
	 * @param userId
	 * @param programId
	 * @param listener
	 * @param errorListener
	 */
	public static void addRemind(int userId, int programId,
			OnAddRemindListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new AddRemindCallback(
				errorListener, userId, programId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 第三方账号绑定
	 * 
	 * @param listener
	 * @param thirdInfo
	 * @param errorListener
	 */
	public static void bindAccount(ThirdPlatInfo thirdInfo,
			OnBindAccountListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new BindAccountCallback(
				errorListener, thirdInfo, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 第三方账号解绑
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void bindDelete(OnBindDeleteListener listener,
			OnHttpErrorListener errorListener, int id) {
		BaseRequest request = new BaseRequest(url, new BindDeleteCallback(
				errorListener, listener, id)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 绑定登录
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void bindLogIn(ThirdPlatInfo thirdInfo,
			OnBindLogInListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new BindLogInCallback(
				thirdInfo, errorListener, listener)) {
		};
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		VolleyQueueManage.getRequestQueue().add(request);
	}


	/**
	 * 取消追剧
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void chaseDelete(String pid, OnChaseDeleteListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChaseDeleteCallback(pid,
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);

	}

	/**
	 * 追剧
	 * 
	 * @param programId
	 * @param listener
	 * @param errorListener
	 */
	public static void chaseProgram(int programId,
			OnChaseProgramListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChaseProgramCallback(
				errorListener, programId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);

	}


	/**
	 * 取消关注
	 * 
	 * @param userId
	 * @param listener
	 * @param errorListener
	 */
	public static void deleteGuanzhu(int userId,
			OnDeleteGuanzhuListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new DeleteGuanzhuCallback(
				errorListener, userId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 取消预约
	 * 
	 * @param userId
	 * @param cpIds
	 * @param listener
	 * @param errorListener
	 */
	public static void deleteRemind(int userId, String cpIds,
			OnDeleteRemindListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new DeleteRemindCallback(
				errorListener, userId, cpIds, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 反馈
	 * 
	 * @param userId
	 * @param contactNum
	 * @param source
	 * @param content
	 * @param listener
	 * @param errorListener
	 */
	public static void feedback(FeedbackData feedBack,
			OnFeedbackListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new FeedbackCallback(
				errorListener, feedBack, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 徽章详情
	 * 
	 * @param badgeId
	 * @param listener
	 * @param errorListener
	 */
	public static void getBadgeDetail(int badgeId,
			OnBadgeDetailListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new BadgeDetailCallback(
				errorListener, badgeId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void getEventRoom(int first, int count,
			OnEventRoomListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new EventRoomCallback(
				errorListener, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 我的徽章
	 * 
	 * @param context
	 * @param listener
	 * @param errorListener
	 */
	public static void getMyBadge(Context context, int first, int count,
			OnMyBadgeListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new MyBadgeCallback(
				errorListener, context, listener, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 预约列表：我的预约，其他用户预约
	 * 
	 * @param context
	 * @param userId
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void getRemind(Context context, int userId, int first,
			int count, OnRemindListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new RemindListCallback(
				errorListener, context, listener, userId, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 追剧列表:我的追剧、其他用户追剧
	 * 
	 * @param context
	 * @param userId
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void chaseList(Context context, int userId, int first,
			int count, OnChaseListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new ChaseListCallback(
				errorListener, context, listener, userId, first, count)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	public static void getOtherFans(Context context, int first, int count,
			int otherid, OnMyFansListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new OtherFansCallback(
				errorListener, context, first, count, otherid, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 关注
	 * 
	 * @param context
	 * @param listener
	 * @param errorListener
	 */
	public static void getMyFollow(Context context, int id, int first,
			int count, OnMyFollowListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new MyFollowCallback(
				errorListener, context, id, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 我的私信
	 * 
	 * @param context
	 * @param listener
	 * @param errorListener
	 */
	public static void getMyPrivateMsg(Context context, int first, int count,
			OnMyPrivateMsgListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new MyPrivateMsgCallback(
				errorListener, context, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 其他用户信息获取
	 * 
	 * @param userId
	 * @param errorListener
	 * @param listener
	 */
	public static void getOtherSpace(int userId,
			OnHttpErrorListener errorListener, OnOtherSpaceListener listener) {
		BaseRequest request = new BaseRequest(url, new OtherSpaceCallback(
				errorListener, userId, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);

	}

	/**
	 * 应用推荐
	 * 
	 * @param first
	 * @param count
	 * @param listener
	 * @param errorListener
	 */
	public static void getRecommendApp(int first, int count,
			OnRecommendAppListener listener, OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new RecommendAppCallback(
				errorListener, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 推荐好友列表
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void getRecommendUserList(
			OnRecommendUserListListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url,
				new RecommendUserListCallback(errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 电视粉账号登录
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void login(OnLogInListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new LogInCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 电视粉账号注销
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void logOff(OnLogOffListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new LogOffCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 
	 * @param listener
	 * @param errorListener
	 * @param userId
	 * @param first
	 *            原othercachedata中offset
	 * @param count
	 *            原othercachedata中pagecount
	 */
	public static void mail(OnMailListListener listener,
			OnHttpErrorListener errorListener, int userId, int first, int count) {
		BaseRequest request = new BaseRequest(url, new MailCallback(
				errorListener, userId, first, count, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 注册
	 * 
	 * @param listener
	 * @param errorListener
	 */
	public static void register(OnRegisterListener listener,
			OnHttpErrorListener errorListener) {
		BaseRequest request = new BaseRequest(url, new RegisterCallback(
				errorListener, listener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 发私信
	 * 
	 * @param errorListener
	 * @param userId
	 * @param listener
	 * @param content
	 * @param pic
	 */
	public static void sendMail(OnHttpErrorListener errorListener, int userId,
			OnSendMailListener listener, String content, String pic) {
		BaseRequest request = new BaseRequest(url, new SendMailCallback(
				errorListener, userId, listener, content, pic)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 用户信息更新
	 * 
	 * @param errorListener
	 * @param listener
	 */
	public static void UserUpdate(OnHttpErrorListener errorListener,
			OnUserUpdateListener listener, UserModify userModify) {
		BaseRequest request = new BaseRequest(url, new UserUpdateCallback(
				errorListener, listener, userModify)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 密码找回-第一步
	 * 
	 * @param errorListener
	 * @param input
	 * @param mListener
	 */
	public static void initForget(OnHttpErrorListener errorListener,
			String input, OnForgetInitListener mListener) {
		BaseRequest request = new BaseRequest(url, new ForgetInitCallback(
				errorListener, input, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 密码找回-发送验证码邮件
	 * 
	 * @param errorListener
	 * @param mListener
	 */
	public static void resendEmail(OnHttpErrorListener errorListener,
			OnReSendEmailListener mListener) {
		BaseRequest request = new BaseRequest(url, new ReSendEmailCallback(
				errorListener, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 密码找回-校验验证码
	 * 
	 * @param errorListener
	 * @param checkCode
	 * @param mListener
	 */
	public static void checkCode(OnHttpErrorListener errorListener,
			String checkCode, OnCheckCodeCheckListener mListener) {
		BaseRequest request = new BaseRequest(url, new CheckCodeCheckCallback(
				errorListener, checkCode, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

	/**
	 * 修改密码
	 * 
	 * @param errorListener
	 * @param checkCode
	 * @param password
	 * @param mListener
	 */
	public static void ChangePasswd(OnHttpErrorListener errorListener,
			String checkCode, String password, OnChangePasswdListener mListener) {
		BaseRequest request = new BaseRequest(url, new ChangePasswdCallback(
				errorListener, checkCode, password, mListener)) {
		};
		VolleyQueueManage.getRequestQueue().add(request);
	}

}
