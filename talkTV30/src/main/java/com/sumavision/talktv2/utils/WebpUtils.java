package com.sumavision.talktv2.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WebpUtils {

	public static Bitmap getAssetBitmap(Context context, String path) {
		Bitmap mBitmap = null;
		try {
			InputStream is = context.getAssets().open(path);
			mBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}

}
