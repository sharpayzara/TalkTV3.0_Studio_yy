package com.sumavision.talktv2.activity.help;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.sumavision.talktv2.bean.UpdateData;
import com.sumavision.talktv2.bean.UserNow;
import com.sumavision.talktv2.utils.AppUtil;

/**
 * 本地用户信息存储
 * 
 * @author suma-hpb
 * 
 */
public class LocalInfo {
	public static void SaveUserData(Context context, boolean loginStatus) {
		SharedPreferences sp = context.getSharedPreferences("userInfo", 0);
		Editor spEd = sp.edit();
		if (loginStatus) {
			spEd.putBoolean("login", true);
			spEd.putBoolean("autologin", true);
			spEd.putString("username", UserNow.current().name);
			spEd.putString("name", UserNow.current().name);
			spEd.putString("nickName", UserNow.current().name);
			spEd.putString("password", UserNow.current().passwd);
			spEd.putString("address", UserNow.current().eMail);
			spEd.putString("email", UserNow.current().eMail);
			spEd.putString("sessionID", UserNow.current().sessionID);

			spEd.putInt("checkInCount", UserNow.current().checkInCount);
			spEd.putInt("commentCount", UserNow.current().commentCount);
			spEd.putInt("messageCount",
					UserNow.current().privateMessageAllCount);
			spEd.putInt("fansCount", UserNow.current().fansCount);
			spEd.putInt("friendCount", UserNow.current().friendCount);

			spEd.putString("iconURL", UserNow.current().iconURL);
			spEd.putInt("userID", UserNow.current().userID);
			spEd.putLong("point", UserNow.current().point);
			spEd.putString("level", UserNow.current().level);
			spEd.putInt("gender", UserNow.current().gender);
			spEd.putLong("exp", UserNow.current().exp);
			spEd.putInt("totalPoint", UserNow.current().totalPoint);
			spEd.putInt("diamond", UserNow.current().diamond);
			spEd.putString("signature", UserNow.current().signature);

			spEd.putInt("commentCount", UserNow.current().commentCount);
			spEd.putInt("remindCount", UserNow.current().remindCount);
			spEd.putInt("chaseCount", UserNow.current().chaseCount);
			// 被@数量
			spEd.putInt("atMeCount", UserNow.current().atMeCount);
			// 被回复数量
			spEd.putInt("replyMeCount", UserNow.current().replyMeCount);
			spEd.putInt("badgesCount", UserNow.current().badgesCount);
		} else {
			spEd.putBoolean("isOpenTypeLogin", false);
			spEd.putBoolean("login", false);
			spEd.putBoolean("autologin", false);
			spEd.putString("username", "");
			spEd.putString("password", "");
			spEd.putInt("userID", 0);
		}
		spEd.commit();
	}

	public static void saveFlashData(Context context) {
		SharedPreferences otherSp = context
				.getSharedPreferences("otherInfo", 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		String end = UpdateData.current().endTime;

		Editor edit = otherSp.edit();
		edit.putBoolean("isOnNewLogoTime", UpdateData.current().isOnNewLogoTime);
		edit.putString("logoDownURL", UpdateData.current().logoDownURL);
		edit.putBoolean("isNeedUpdateLogo", true);
		edit.putBoolean("hasNewLogoFile", true);
		edit.putString("logostartTime", UpdateData.current().startTime);
		edit.putString("logoendTime", UpdateData.current().endTime);
		edit.putString("logoFileName", UpdateData.current().logoFileName);
		edit.putString("logoServerFileName",
				UpdateData.current().logoServerFileName);
		if (now.compareTo(end) > 0) {
			AppUtil.clearOldLogo();
			edit.putBoolean("isOnNewLogoTime", false);
			edit.putString("logoDownURL", "");
			edit.putBoolean("isNeedUpdateLogo", false);
			edit.putString("logostartTime", "00");
			edit.putString("logoendTime", "00");
		}

		edit.commit();
	}

	public static void getUserData(Context context) {
		SharedPreferences spUser;
		spUser = context.getSharedPreferences("userInfo", 0);
		SharedPreferences otherSp = context
				.getSharedPreferences("otherInfo", 0);
		UserNow.current().userID = spUser.getInt("userID", 0);
		if (UserNow.current().userID != 0) {
			UserNow.current().nickName = spUser.getString("nickName", "");
			UserNow.current().eMail = spUser.getString("address", "");
			if (TextUtils.isEmpty(UserNow.current().eMail)) {
				UserNow.current().eMail = spUser.getString("email", "");
			}
			UserNow.current().sessionID = spUser.getString("sessionID", "");
			UserNow.current().checkInCount = spUser.getInt("checkInCount", 0);
			UserNow.current().commentCount = spUser.getInt("commentCount", 0);
			UserNow.current().fansCount = spUser.getInt("fansCount", 0);
			UserNow.current().privateMessageAllCount = spUser.getInt(
					"messageCount", 0);

			UserNow.current().iconURL = spUser.getString("iconURL", "");
			UserNow.current().signature = spUser.getString("signature", "");
			UserNow.current().point = (int) spUser.getLong("point", 0);
			UserNow.current().level = spUser.getString("level", "1");
			UserNow.current().gender = spUser.getInt("gender", 1);
			UserNow.current().exp = (int) spUser.getLong("exp", 0);
			UserNow.current().totalPoint = spUser.getInt("totalPoint", 0);
			UserNow.current().diamond = spUser.getInt("diamond", 0);

			UserNow.current().name = spUser.getString("name", "xxx");
			UserNow.current().friendCount = spUser.getInt("friendCount", 0);
			UserNow.current().remindCount = spUser.getInt("remindCount", 0);
			UserNow.current().chaseCount = spUser.getInt("chaseCount", 0);
			UserNow.current().passwd = spUser.getString("password", "");
			UserNow.current().commentCount = spUser.getInt("commentCount", 0);

			// 被@数量
			UserNow.current().atMeCount = spUser.getInt("atMeCount", 0);
			// 被回复数量
			UserNow.current().replyMeCount = spUser.getInt("replyMeCount", 0);
			UserNow.current().badgesCount = spUser.getInt("badgesCount", 0);

		} else {
			UserNow.current().isSelf = false;
		}

		UpdateData.current().logoFileName = otherSp.getString("logoFileName",
				"logo");
		UpdateData.current().logoServerFileName = otherSp.getString(
				"logoServerFileName", "");
		UpdateData.current().logoDownURL = otherSp.getString("logoDownURL", "");
		UpdateData.current().hasLogoFile = otherSp.getBoolean("hasNewLogoFile",
				false);
		UpdateData.current().isOnNewLogoTime = otherSp.getBoolean(
				"isOnNewLogoTime", false);
		UpdateData.current().isNeedUpdateLogo = otherSp.getBoolean(
				"isNeedUpdateLogo", false);
		UpdateData.current().startTime = otherSp.getString("logostartTime",
				"00");
		UpdateData.current().endTime = otherSp.getString("logoendTime", "00");
	}
}
