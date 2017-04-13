package com.sumavision.talktv2.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.sumavision.talktv2.bean.JSONMessageType;
import com.sumavision.talktv2.bean.UserNow;

/**
 * 
 * @author 郭鹏
 * @version 2.0
 * @createTime 2012-6-1
 * @description 图片工具
 * @changLog
 */
public class PicUtils {
	public static final long PIC_SIZE_LIMITE = 150000;
	public static final int PIC_SIZE_LIMITE_W = 150;
	public static final int PIC_SIZE_LIMITE_H = 150;
	public final static String FOLDERNAME = "TVFan/sendPic";
	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String FILE_EXTENTION = ".jpg";
	@SuppressLint("SdCardPath")
	public final static String SDCARD = "/sdcard";
	public static String SD_PATH = "";
	private Activity mActivity;

	public PicUtils(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public String getAbsoluteImagePath(Uri uri) {
		String imagePath = "";
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = mActivity.managedQuery(uri, proj, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}

	public Bitmap loadImgThumbnail(String imgName, int kind) {
		Bitmap bitmap = null;

		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DISPLAY_NAME };
		@SuppressWarnings("deprecation")
		Cursor cursor = mActivity.managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = mActivity.getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb,
					cursor.getInt(0), kind, options);
		}
		return bitmap;
	}

	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}

	public static String getCamerPath() {
		return Environment.getExternalStorageDirectory() + "/TVFan/tempCamera/";
	}

	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath()
					+ File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	public static Bitmap getScaleBitmap(Context context, String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts); // 此时返回bm为空
		opts.inJustDecodeBounds = false;
		int ratio = (int) (opts.outHeight / (float) 200);
		if (ratio <= 0)
			ratio = 1;

		opts.inSampleSize = ratio;
		bitmap = BitmapFactory.decodeFile(filePath, opts);

		if (FileInfoUtils.getFileSize(filePath) > PIC_SIZE_LIMITE) {
			savePic2SD(bitmap, filePath, JSONMessageType.USER_PIC_SDCARD_FOLDER);
		}
		return bitmap;
	}

	public static Bitmap getFinalScaleBitmapBigPic(Context context,
			String filePath) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts); // 此时返回bm为空
		opts.inJustDecodeBounds = false;

		int ratio = (int) (opts.outHeight / (float) 200);
		if (ratio <= 0)
			ratio = 1;

		Log.e("InfomationHelper", ratio + "");

		if (FileInfoUtils.getFileSize(filePath) > PIC_SIZE_LIMITE) {
			opts.inSampleSize = ratio;
		} else {
			opts.inSampleSize = 3;
		}

		bitmap = BitmapFactory.decodeFile(filePath, opts);
		SD_PATH = Environment.getExternalStorageDirectory() + File.separator
				+ FOLDERNAME + File.separator;

		String s = SD_PATH + getFileName() + FILE_EXTENTION;
		UserNow.current().bitmapPath = s;

		return bitmap;
	}

	public static void savePic2SD(Bitmap bitmap, String path, String folder) {

		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			File fileDir = new File(folder);
			if (!fileDir.exists()) {
				fileDir.mkdir();
			}
		}

		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);

			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
