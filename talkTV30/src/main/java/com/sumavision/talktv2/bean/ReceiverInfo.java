package com.sumavision.talktv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ReceiverInfo implements Parcelable {
	public String name;
	public String address;
	public String phone;
	public String remark;
	public boolean ticket;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(address);
		dest.writeString(phone);
		dest.writeString(remark);
	}

	public static final Parcelable.Creator<ReceiverInfo> CREATOR = new Parcelable.Creator<ReceiverInfo>() {

		@Override
		public ReceiverInfo createFromParcel(Parcel source) {
			ReceiverInfo p = new ReceiverInfo();
			p.name = source.readString();
			p.address = source.readString();
			p.phone = source.readString();
			p.remark = source.readString();
			return p;
		}

		@Override
		public ReceiverInfo[] newArray(int size) {
			return null;
		}
	};
}
