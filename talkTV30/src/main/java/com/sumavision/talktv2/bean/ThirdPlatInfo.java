package com.sumavision.talktv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 第三方平台信息
 * 
 * @author suma-hpb
 * 
 */
public class ThirdPlatInfo implements Parcelable {

	public static final int TYPE_SINA = 1;
	public static final int TYPE_QQ = 2;
	public static final int TYPE_FLYME = 9;
	public static final int TYPE_RENREN = 3;
	public static final int TYPE_TENCENT_WEIBO = 4;
	public static final int TYPE_KAIXIN = 5;
	public static final int TYPE_FEIXIN = 6;
	public static final int TYPE_MSN = 7;
	public static final int TYPE_SOHU_WEIBO = 8;
	public int bindId;// 绑定映射id;
	public String userId;
	public String userName;
	public String userIconUrl;
	public String userSignature;
	public int type;
	public String token;
	public String expiresIn;
	public String openId;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(userId);
		dest.writeString(userName);
		dest.writeString(userIconUrl);
		dest.writeString(userSignature);
		dest.writeInt(type);
		dest.writeString(token);
		dest.writeString(expiresIn);
		dest.writeString(openId);

	}

	public static final Parcelable.Creator<ThirdPlatInfo> CREATOR = new Creator<ThirdPlatInfo>() {

		@Override
		public ThirdPlatInfo[] newArray(int size) {
			return new ThirdPlatInfo[size];
		}

		@Override
		public ThirdPlatInfo createFromParcel(Parcel source) {
			ThirdPlatInfo i = new ThirdPlatInfo();
			i.userId = source.readString();
			i.userName = source.readString();
			i.userIconUrl = source.readString();
			i.userSignature = source.readString();
			i.type = source.readInt();
			i.token = source.readString();
			i.expiresIn = source.readString();
			i.openId = source.readString();
			return i;
		}
	};
}
