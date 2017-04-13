package com.sumavision.talktv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 对话框提示信息
 * 
 * @author suma-hpb
 * 
 */
public class DialogInfo implements Parcelable {
	public String title;
	public String content;
	public String confirm;
	public String neutral;
	public String cancel;
	public int iconRes;
	public int contentColorResId;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(confirm);
		dest.writeString(neutral);
		dest.writeString(cancel);
		dest.writeInt(iconRes);
		dest.writeInt(contentColorResId);
	}

	public static final Parcelable.Creator<DialogInfo> CREATOR = new Parcelable.Creator<DialogInfo>() {
		public DialogInfo createFromParcel(Parcel in) {
			DialogInfo info = new DialogInfo();
			info.title = in.readString();
			info.content = in.readString();
			info.confirm = in.readString();
			info.neutral = in.readString();
			info.cancel = in.readString();
			info.iconRes = in.readInt();
			info.contentColorResId = in.readInt();
			return info;
		}

		public DialogInfo[] newArray(int size) {
			return new DialogInfo[size];
		}
	};

}