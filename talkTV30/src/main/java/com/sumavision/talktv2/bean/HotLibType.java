package com.sumavision.talktv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 片库首页
 * */
public class HotLibType implements Parcelable {
	// FIXME
	public static final int TYPE_RANKING = 16;
	public static final int TYPE_SUB = 15;
	public static final int TYPE_USHOW = 17;
	public static final int TYPE_PROGRAM = 1;
	public static final int TYPE_SPECIAL = 18;
	
	public static final int TYPE_MOVIE = 3;
	public static final int TYPE_TV = 4;
	public static final int TYPE_MICRO = 5;
	public static final int TYPE_ZONGYI = 6;
	public static final int TYPE_CARTOON = 7;
	public static final int TYPE_OVERSEA = 8;
	public static final int TYPE_DOCUMENTARY = 9;
	public static final int TYPE_OTHER = 10;
	public long id;
	public String icon;
	public String photo;
	public String name;
	public int type;
	public int programType;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(icon);
		dest.writeString(name);
		dest.writeInt(type);
		dest.writeInt(programType);
	}

	public static final Parcelable.Creator<HotLibType> CREATOR = new Creator<HotLibType>() {

		@Override
		public HotLibType[] newArray(int size) {
			return new HotLibType[size];
		}
		
		@Override
		public HotLibType createFromParcel(Parcel source) {
			HotLibType h = new HotLibType();
			h.id = source.readLong();
			h.icon = source.readString();
			h.name = source.readString();
			h.type = source.readInt();
			h.programType = source.readInt();
			return h;
		}
	};
}
