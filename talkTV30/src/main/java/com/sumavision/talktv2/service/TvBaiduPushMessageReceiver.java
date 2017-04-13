package com.sumavision.talktv2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sumavision.talktv.videoplayer.ui.PlayerActivity;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.activity.ActivityActivity;
import com.sumavision.talktv2.activity.FeedbackMailActivity;
import com.sumavision.talktv2.activity.InteractiveZoneActivity;
import com.sumavision.talktv2.activity.LoginActivity;
import com.sumavision.talktv2.activity.MyFavActivity;
import com.sumavision.talktv2.activity.ShakeActivity;
import com.sumavision.talktv2.activity.UserMailActivity;
import com.sumavision.talktv2.activity.WebPageActivity;
import com.sumavision.talktv2.bean.EventMessage;
import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.NetPlayData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.http.ParseListener;
import com.sumavision.talktv2.http.VolleyHelper;
import com.sumavision.talktv2.http.json.BaseJsonParser;
import com.sumavision.talktv2.http.json.UploadPushInfoParser;
import com.sumavision.talktv2.http.json.UploadPushInfoRequest;
import com.sumavision.talktv2.utils.AppUtil;
import com.sumavision.talktv2.utils.Constants;
import com.sumavision.talktv2.utils.MiUitils;
import com.sumavision.talktv2.utils.PreferencesUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author
 * @description 接收推送服务的receiver 主要接收 bind 和onMessage 信息
 */
public class TvBaiduPushMessageReceiver extends PushMessageReceiver {

	public static final String TAG = TvBaiduPushMessageReceiver.class
			.getSimpleName();

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(final Context context, int errorCode, String appid,
			final String userId, final String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		Log.e(TAG, responseString);

		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			SharedPreferences spUser = context.getSharedPreferences("userInfo",
					0);
			int tvfanuserId = spUser.getInt("userID", 0);
			if (uploadInfoparser == null) {
				uploadInfoparser = new UploadPushInfoParser();
				VolleyHelper.post(new UploadPushInfoRequest(userId, channelId,
						tvfanuserId).make(),
						new ParseListener(uploadInfoparser) {
							@Override
							public void onParse(BaseJsonParser parser) {
								if (uploadInfoparser.errCode == JSONMessageType.SERVER_CODE_OK) {
									saveBind(context, userId, channelId);
								}
							}
						}, null);
			}
			saveBind(context, userId, channelId);
		}
	}

	private void saveBind(Context context, String userId, String channelId) {
		PushUtils.setBind(context, true, userId, channelId);
	}

	UploadPushInfoParser uploadInfoparser;

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		if (TextUtils.isEmpty(message)) {
			message = "";
		}
		if (TextUtils.isEmpty(customContentString)) {
			customContentString = "";
		}
		String messageString = "透传消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		Log.e(TAG, messageString);
		if (PreferencesUtils.getBoolean(context, null, "isOn", true)) {
			parseMessage(context, message);
		}
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		Log.e(TAG, notifyString);
	}

	@Override
	public void onNotificationArrived(Context context, String s, String s1, String s2) {

	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + successTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> successTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + successTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.e(TAG, responseString);
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		Log.e(TAG, responseString);

	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		Log.e(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			PushUtils.setBind(context, false, "", "");
		}
	}

	public void parseMessage(Context context, String message) {
		Log.e(TAG, message);
		if (!TextUtils.isEmpty(message)) {
			try {
				JSONObject msgJson = new JSONObject(message);
				JSONObject pushJson = msgJson.optJSONObject("push");
				if (pushJson != null) {
					String text = pushJson.optString("text");
					String title = pushJson.optString("title");
					String pic = pushJson.optString("pushPic");
					JSONObject interactive = pushJson
							.optJSONObject("interactive");
					JSONObject concert = pushJson.optJSONObject("live");
					JSONObject programObj = pushJson.optJSONObject("program");
					long activityId = pushJson.optLong("activityId", 0);
					int pushType = pushJson.optInt("type");
					if (pushType ==1){
						EventMessage msg = new EventMessage("FeedbackMailActivity");
						EventBus.getDefault().post(msg);
						sendNotification(context, NOTIFY_FEEDBACK_MESSAGE,
								new Bundle(), title, text, pic);
						return;
					} else if (pushType ==2){
						sendNotification(context, NOTIFY_DAY_LOTTERY,
								new Bundle(), title, text, pic);
					}
					if (concert != null) {
						long id = concert.optLong("id");
						if (id != 0) {
							Bundle bundle = new Bundle();
							bundle.putLong("id", id);
							sendNotification(context, NOTIFY_CONCERT,
									bundle, title, text, pic);
						}
					}
					if (interactive != null) {
						long interactiveId = interactive.optLong(
								"interactiveId", 0);
						long zoneId = interactive.optLong("zoneId");
						if (interactiveId != 0) {
							Bundle bundle = new Bundle();
							bundle.putLong("interactiveId", interactiveId);
							if (zoneId != 0) {
								bundle.putLong("zoneId", zoneId);
							}
							bundle.putBoolean("notice", true);
							sendNotification(context, NOTIFY_INTERACTION,
									bundle, title, text, pic);
						}
					}
					long pProgramId = pushJson.optLong("programId",0);
					if (pProgramId>0){
						Bundle bundle = new Bundle();
						bundle.putLong("id", pProgramId);
						bundle.putBoolean("notice", true);
						bundle.putInt("where", 3);
						bundle.putInt("playType",2);
						sendNotification(context, NOTIFY_PROGRAM, bundle,
								title, text, pic);
					}
					if (programObj != null) {
						Bundle bundle = new Bundle();
						long programId = programObj.optLong("programId",0);
						if (programId<=0){
							return;
						}
						bundle.putLong("id", programId);
						bundle.putBoolean("notice", true);
						int subId = programObj.optInt("subId");
						bundle.putInt("subid",subId);
						if (subId>0){
							bundle.putInt("where",4);
						}else{
							bundle.putInt("where",3);
						}
						bundle.putInt("playType",2);
						sendNotification(context, NOTIFY_PROGRAM, bundle,
								title, text, pic);
					}
					if (activityId != 0) {
						Bundle bundle = new Bundle();
						bundle.putLong("activityId", activityId);
						bundle.putBoolean("notice", true);
						sendNotification(context, NOTIFY_ACTIVITY, bundle,
								title, text, pic);
					}
					JSONObject mailObj = pushJson.optJSONObject("mail");
					SharedPreferences spUser = context.getSharedPreferences(
							"userInfo", 0);
					int userId = spUser.getInt("userID", 0);
					if (mailObj != null) {
						if (userId == 0) {
							return;
						}
						int sid = mailObj.optInt("sendUserId");
						String sUserName = mailObj.optString("sendUserName");
						// String content = mailObj.optString("content");
						Bundle bundle = new Bundle();
						bundle.putString("otherUserName", sUserName);
						bundle.putInt("otherUserId", sid);
						SharedPreferences pushMsgPreferences = context
								.getSharedPreferences(Constants.pushMessage, 0);
						Editor edit = pushMsgPreferences.edit();
						edit.putBoolean(Constants.key_privateMsg, true);
						StringBuffer singleKey = new StringBuffer(
								Constants.key_privateMsg);
						singleKey.append("_").append(sid).append("-")
								.append(userId);
						edit.putBoolean(singleKey.toString(), true);
						edit.commit();
						if (AppUtil.getTopActivity(context).contains(
								UserMailActivity.class.getSimpleName())) {
							return;
						}
						sendNotification(context, NOTIFY_MESSAGE, bundle,
								title, text, pic);
					}
					JSONObject fensiObj = pushJson.optJSONObject("fensi");
					JSONObject replyObj = pushJson.optJSONObject("reply");
					JSONObject discovery = pushJson.optJSONObject("discovery");
					if (discovery != null) {
						// 发现对象类型objectType=1,活动类型，objectType=2，推荐物品类型
						@SuppressWarnings("unused")
						int type = discovery.optInt("objectType");
						// 发现对象ID：objectType=1,活动ID，objectType=2，推荐物品ID
						@SuppressWarnings("unused")
						int id = discovery.optInt("objectId");
						PreferencesUtils.putBoolean(context,
								Constants.pushMessage, Constants.KEY_FOUND,
								true);
						if (type == 1){
							PreferencesUtils.putBoolean(context,
									Constants.pushMessage, Constants.KEY_ACTIVITY,
									true);
						}else if(type == 2){
							PreferencesUtils.putBoolean(context,
									Constants.pushMessage, Constants.KEY_GOODS,
									true);
						}
					}
					if (fensiObj != null || replyObj != null) {
						if (userId == 0) {
							return;
						}
						PreferencesUtils.putBoolean(context,
								Constants.pushMessage,
								Constants.KEY_USER_CENTER, true);
						if (fensiObj != null) {
							PreferencesUtils.putBoolean(context,
									Constants.pushMessage, Constants.key_fans,
									true);
						}
						if (replyObj != null) {
							PreferencesUtils.putBoolean(context,
									Constants.pushMessage, Constants.key_reply,
									true);
							PreferencesUtils.putBoolean(context,
									Constants.pushMessage,
									Constants.key_user_comment, true);
						}

					}
					JSONObject favUpdate = pushJson
							.optJSONObject("programUpdate");
					if (favUpdate != null) {
						if (userId == 0) {
							return;
						}
						if (TextUtils.isEmpty(text)) {
							text = "您收藏的节目有更新咯";
						}
						long pid = favUpdate.optLong("programId");
						PreferencesUtils.putBoolean(context,
								Constants.pushMessage, Constants.key_favourite,
								true);
						Bundle bundle = new Bundle();
						bundle.putLong("id", pid);
						bundle.putBoolean("isHalf", true);
						sendNotification(context, NOTIFY_FAV, bundle, title,
								text, pic);
					}
						JSONObject shakeObj = pushJson.optJSONObject("shake");
						if (shakeObj != null){
							sendNotification(context,NOTIFY_SHAKE,new Bundle(),title,text,pic);
						}
					JSONObject channelObj = pushJson.optJSONObject("channel");
					if (channelObj != null){
						Bundle bundle = new Bundle();
						bundle.putInt("channelId", (int) channelObj.optLong("id"));
						JSONArray urls = channelObj.optJSONArray("play");
						if (urls != null && urls.length()>0){
							bundle.putString("url", urls.optJSONObject(0).optString("url"));
							bundle.putString("p2pChannel", TextUtils.isEmpty(urls.optJSONObject(0).optString("channelIdStr")) ? "" : urls.optJSONObject(0).optString("channelIdStr"));
							bundle.putInt("playType", 1);
							bundle.putString("title", TextUtils.isEmpty(channelObj.optString("name")) ? "电视直播"
									: channelObj.optString("name"));
							ArrayList<NetPlayData> playList = new ArrayList<NetPlayData>();
							for (int i=0; i<urls.length();i++){
								NetPlayData temp = new NetPlayData();
								JSONObject obj = urls.optJSONObject(i);
								temp.url = obj.optString("url");
								temp.videoPath = obj.optString("videoPath");
								temp.channelIdStr = obj.optString("channelIdStr");
								temp.showUrl = obj.optString("showUrl");
								playList.add(temp);
							}
							bundle.putSerializable("NetPlayData", playList);
							sendNotification(context, NOTIFY_LIVE_CHANNEL, bundle, title, text, pic);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static final int NOTIFY_ACTIVITY = 1;
	private static final int NOTIFY_PROGRAM = 2;
	private static final int NOTIFY_INTERACTION = 3;
	private static final int NOTIFY_MESSAGE = 4;
	private static final int NOTIFY_FAV = 5;
	private static final int NOTIFY_CONCERT = 6;
	private static final int NOTIFY_SHAKE = 7;
	private static final int NOTIFY_LIVE_CHANNEL = 8;
	private static final int NOTIFY_FEEDBACK_MESSAGE = 9;
	private static final int NOTIFY_DAY_LOTTERY = 10;


	private void sendNotification(final Context context, int type,
			Bundle bundle, String title, final String text, String pic) {
		Intent notificationIntent = new Intent();
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		Class<?> mActivity = null;
		switch (type) {
		case NOTIFY_ACTIVITY:
			mActivity = ActivityActivity.class;
			break;
		case NOTIFY_PROGRAM:
			mActivity = PlayerActivity.class;
			notificationIntent.putExtra("isHalf", true);
			notificationIntent.putExtra("where", 3);
			break;
		case NOTIFY_INTERACTION:
			mActivity = InteractiveZoneActivity.class;
			break;
		case NOTIFY_MESSAGE:
			mActivity = UserMailActivity.class;
			break;
		case NOTIFY_FAV:
			mActivity = MyFavActivity.class;
			break;
		case NOTIFY_SHAKE:
			mActivity = ShakeActivity.class;
			break;
		case NOTIFY_LIVE_CHANNEL:
			mActivity = PlayerActivity.class;
			break;
		case NOTIFY_FEEDBACK_MESSAGE:
			mActivity = FeedbackMailActivity.class;
			break;
		case NOTIFY_DAY_LOTTERY:
			if (UserNow.current().userID>0){
				notificationIntent.putExtra("url",Constants.host.substring(0,Constants.host.lastIndexOf("/")+1)+"newweb/deckGame/game.jsp");
				notificationIntent.putExtra("title","每日抽奖");
				mActivity = WebPageActivity.class;
			} else {
				notificationIntent.putExtra("where","WebPageActivity");
				mActivity = LoginActivity.class;
			}
			break;
		default:
			break;
		}
		stackBuilder.addParentStack(mActivity);
		notificationIntent.setClass(context, mActivity);
		stackBuilder.addNextIntent(notificationIntent);
		notificationIntent.putExtras(bundle);
		if (TextUtils.isEmpty(title)) {
			title = context.getString(R.string.notification_content_title);
		}
		PendingIntent contentIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_CANCEL_CURRENT);
		if(type != NOTIFY_PROGRAM ){
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		}else{
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			contentIntent = PendingIntent.getActivity(context, type, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		}

		final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setTicker(title).setSmallIcon(R.drawable.icon)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_VIBRATE)
				.setContentTitle(title).setContentText(text)
				.setContentIntent(contentIntent);

		if (android.os.Build.VERSION.SDK_INT >= 16 && !TextUtils.isEmpty(pic)) {
			StringBuilder picPath = new StringBuilder(Constants.picUrlFor + pic
					+ Constants.PIC_SUFF);
			ImageLoader imageLoader = ImageLoader.getInstance();
			imageLoader.loadImage(picPath.toString(),
					new ImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							NotificationCompat.BigPictureStyle pictureStyle = new NotificationCompat.BigPictureStyle();
							pictureStyle.bigPicture(loadedImage);
							pictureStyle.setSummaryText(text);
							mBuilder.setStyle(pictureStyle);
							NotificationManager notificationManager = (NotificationManager) context
									.getSystemService(Context.NOTIFICATION_SERVICE);
							notificationManager.notify(NOTIFICATION_ID,
									mBuilder.build());
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
						}
					});
		}

		Notification notification = mBuilder.build();
		MiUitils.addDesktopCornerMark(notification);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}

	public static final int NOTIFICATION_ID = 2001;

}
