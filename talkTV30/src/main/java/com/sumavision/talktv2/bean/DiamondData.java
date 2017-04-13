package com.sumavision.talktv2.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class DiamondData implements Parcelable {
	public long id;
	public String intro;
	public int num;
	public double price;
	public int canPayCount;
	public int alreadyPayCount;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(intro);
		dest.writeInt(num);
		dest.writeDouble(price);
		dest.writeInt(canPayCount);
		dest.writeInt(alreadyPayCount);
	}

	public static final Parcelable.Creator<DiamondData> CREATOR = new Parcelable.Creator<DiamondData>() {
		public DiamondData createFromParcel(Parcel in) {
			DiamondData data = new DiamondData();
			data.id = in.readLong();
			data.intro = in.readString();
			data.num = in.readInt();
			data.price = in.readDouble();
			data.canPayCount = in.readInt();
			data.alreadyPayCount = in.readInt();
			return data;
		}

		public DiamondData[] newArray(int size) {
			return new DiamondData[size];
		}
	};

}
