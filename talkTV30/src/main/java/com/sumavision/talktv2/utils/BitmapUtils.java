package com.sumavision.talktv2.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.sumavision.talktv2.bean.UpdateData;

/**
 * 
 * @author 郭鹏
 * @version 2.0
 * @createTime 2012-6-1
 * @description Bitmap工具
 * @changLog
 */
public class BitmapUtils {

	/**
	 * 图片压缩
	 * 
	 * @param bitMap
	 * @param maxSize
	 *            kb
	 * @return
	 */
	public static Bitmap imageZoom(Bitmap bitMap, double maxSize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
					bitMap.getHeight() / Math.sqrt(i));
		}
		return bitMap;
	}

	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	public static byte[] bitmapToBytes(Bitmap bitmap) {
		if (bitmap == null) {
			return null;

		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
		return os.toByteArray();

	}

	public static byte[] drawableToBytes(Drawable drawable) {
		if (drawable == null) {
			return null;

		}
		return bitmapToBytes(drawableToBitmap(drawable));

	}

	public static Bitmap rotateBitmap(Bitmap bitmapOrg, int angle) {

		if (angle == 0) {
			return bitmapOrg;
		}
		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 旋转图片 动作
		matrix.setRotate(angle, width, height);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
				height, matrix, true);
		bitmapOrg.recycle();
		return resizedBitmap;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	// 指定地址下载图片写入文件
	public static void loadImageFromUrlAnd2File(String url) throws Exception {

		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);
		HttpResponse response = client.execute(getRequest);
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {
			Log.e("PicShow", "Request URL failed, error code =" + statusCode);
		}

		HttpEntity entity = response.getEntity();

		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = entity.getContent();
			byte[] buf = new byte[1024];
			int readBytes = -1;
			while ((readBytes = is.read(buf)) != -1) {
				baos.write(buf, 0, readBytes);
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (is != null) {
				is.close();
			}
		}
		byte[] imageArray = baos.toByteArray();

		Log.e("loadImageFromUrlAnd2File", "start saving");
		String folder = Environment.getExternalStorageDirectory() + "/"
				+ "TVFan/logo";

		FileInfoUtils.writeFile(imageArray, folder,
				UpdateData.current().logoFileName);

		Log.e("loadImageFromUrlAnd2File", "end saving");
	}

	// 指定地址下载图片写入文件
	public static void loadImageFromUrlAnd2File(String url, String folder,
			String name) throws Exception {

		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);
		HttpResponse response = client.execute(getRequest);
		int statusCode = response.getStatusLine().getStatusCode();

		if (statusCode != HttpStatus.SC_OK) {
			Log.e("PicShow", "Request URL failed, error code =" + statusCode);
		}

		HttpEntity entity = response.getEntity();

		if (entity == null) {
			Log.e("PicShow", "HttpEntity is null");
		}

		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = entity.getContent();
			byte[] buf = new byte[1024];
			int readBytes = -1;
			while ((readBytes = is.read(buf)) != -1) {
				baos.write(buf, 0, readBytes);
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (is != null) {
				is.close();
			}
		}
		byte[] imageArray = baos.toByteArray();

		Log.e("loadImageFromUrlAnd2File", "start saving");

		FileInfoUtils.writeFile(imageArray, folder, name + ".jpg");

		Log.e("loadImageFromUrlAnd2File", "end saving");
	}

	// sdcard本地图片加载到imageView
	public static Bitmap getLocalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void saveDrawable(Drawable drawable, String fileDir,
			String name) {
		// String folderPath = Environment.getExternalStorageDirectory()
		// + File.separator + fileDir;
		byte[] buffer = drawableToBytes(drawable);
		FileInfoUtils.writeFile(buffer, fileDir, name + ".jpg");
	}

	public static void saveBitmap(Bitmap bitmap, String fileDir, String name) {
		byte[] buffer = bitmapToBytes(bitmap);
		FileInfoUtils.writeFile(buffer, fileDir, name + ".jpg");
	}

	public static void saveDrawableForTvShot(Drawable drawable, String fileDir,
			String name) {
		byte[] buffer = drawableToBytes(drawable);
		FileInfoUtils.writeFile(buffer, fileDir, name);
	}

	public static void saveDrawableNew(Drawable drawable, String fileDir,
			String name) {
		// String folderPath = Environment.getExternalStorageDirectory()
		// + File.separator + fileDir;
		byte[] buffer = drawableToBytes(drawable);
		FileInfoUtils.writeFile(buffer, fileDir, name);
	}

	public static void deleFolder(String folder) {
		String folderPath = Environment.getExternalStorageDirectory()
				+ File.separator + folder;
		File file = new File(folderPath);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				f.delete();
			}
		}
	}

	public static boolean isFileExist(String folder, String path) {
		String folderPath = Environment.getExternalStorageDirectory()
				+ File.separator + folder + File.separator;

		File file = new File(folderPath, path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static Drawable bytesToDrawable(byte[] bytes) {
		Drawable drawable = null;
		int length = bytes.length;
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, length);
		drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	public static Drawable getSdCardFromDrawable(String url) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist) {
			String fileFolder = Environment.getExternalStorageDirectory()
					+ "/TVFan/temp";
			File dir = new File(fileFolder);
			if (!dir.exists()) {
				dir.mkdirs();
			} else {
				Drawable d = null;
				try {
					String name = getFileNname(url);
					d = Drawable.createFromPath(fileFolder + File.separator
							+ name);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (d != null) {
					return d;
				}
			}
		}
		return null;
	}

	private static String getFileNname(String str) {
		String name;
		try {
			name = str.substring(str.lastIndexOf("/"), str.lastIndexOf("."))
					+ ".jpg";
		} catch (Exception e) {
			return null;
		}
		return name;
	}
}
