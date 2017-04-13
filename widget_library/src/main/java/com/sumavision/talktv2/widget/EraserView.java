package com.sumavision.talktv2.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * 使用方法：<br/>
 * 1.EraserView view = new EraserView(this);<br/>
 * 2.view.setBackgroundResource(R.drawable.background);<br/>
 * 3.view.setForeGround(R.drawable.cover, 0);//view.setForeGround(Color.RED);<br/>
 * 4.布局 addView(view);
 * 
 * @author pengbing han 16152
 * 
 */
public class EraserView extends View {
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Path mPath;
	private float mX, mY;
	private int width;
	private int height;
	private static final float TOUCH_TOLERANCE = 4;
	OnShowAwardListener showAwardListener;

	private int pointCount = 180;

	public EraserView(Context context) {
		super(context);
		setFocusable(true);
		setViewSize(context);
		int screenHeight = getScreenHeight();
		if (screenHeight >= 1024) {
			pointCount = 50;
		} else {
			pointCount = 40;
		}
		SharedPreferences settings = context.getSharedPreferences("tvfan",
				Context.MODE_PRIVATE);
		isPad = settings.getBoolean("isForPad", false);
	}

	boolean isPad;

	public EraserView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		setFocusable(true);
		setViewSize(context);
		float countale = context.getResources().getDisplayMetrics().density;
		if (countale >= 2) {
			pointCount = 25;
		} else {
			pointCount = 20;
		}
		SharedPreferences settings = context.getSharedPreferences("tvfan",
				Context.MODE_PRIVATE);
		isPad = settings.getBoolean("isForPad", false);
	}

	public EraserView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EraserView(Context context, OnShowAwardListener showAwardListener) {
		super(context);
		setFocusable(true);
		setViewSize(context);
		int screenHeight = getScreenHeight();
		if (screenHeight >= 1024) {
			pointCount = 50;
		}
		SharedPreferences settings = context.getSharedPreferences("tvfan",
				Context.MODE_PRIVATE);
		isPad = settings.getBoolean("isForPad", false);
		this.showAwardListener = showAwardListener;
	}

	private void setViewSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = this.getResources().getDisplayMetrics();
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		int offset = dip2px(context,40);
		width = screenWidth - offset;
		height = screenHeight;

	}
	public static int dip2px(Context context, float dpValue) {
		final float countale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * countale + 0.5f);
	}

	public OnShowAwardListener getShowAwardListener() {
		return showAwardListener;
	}

	public void setShowAwardListener(OnShowAwardListener showAwardListener) {
		this.showAwardListener = showAwardListener;
	}

	/**
	 * 
	 * @param colorARGB
	 *            should like 0x8800ff00
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap createBitmapFromARGB(int colorARGB) {
		int[] argb = new int[width * height];

		for (int i = 0; i < argb.length; i++) {

			argb[i] = colorARGB;

		}
		return Bitmap.createBitmap(argb, width, height, Config.ARGB_8888);
	}

	/**
	 * 
	 * @param bm
	 * @param alpha
	 *            ,alpha should be between 0 and 255
	 * @note set bitmap's alpha
	 * @return
	 */
	private Bitmap setBitmapAlpha(Bitmap bm, int alpha) {
		int[] argb = new int[bm.getWidth() * bm.getHeight()];
		bm.getPixels(argb, 0, bm.getWidth(), 0, 0, bm.getWidth(),
				bm.getHeight());

		for (int i = 0; i < argb.length; i++) {

			argb[i] = ((alpha << 24) | (argb[i] & 0x00FFFFFF));
		}
		return Bitmap.createBitmap(argb, bm.getWidth(), bm.getHeight(),
				Config.ARGB_8888);
	}

	/**
	 * 
	 * @param bm
	 * @note if bitmap is smaller than screen, you can scale it fill the screen.
	 * @return
	 */
	public Bitmap scaleBitmapFillScreen(Bitmap bm) {
		return Bitmap.createScaledBitmap(bm, width, height, true);
	}

	/**
	 * 
	 * @param resId
	 * @param alpha
	 *            透明度
	 * @note set ForeGround , which overlay on background.
	 */
	public void setForeGround(int resId, int alpha) {
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		if (alpha > 0) {
			if (alpha > 255)
				alpha = 255;
			bm = setBitmapAlpha(bm, alpha);
		}
		setCoverBitmap(bm);
	}

	/**
	 * 颜色做cover
	 * 
	 * @param colorARGB
	 *            颜色值
	 */
	public void setForeGround(int colorARGB) {
		Bitmap bm = createBitmapFromARGB(colorARGB);
		setCoverBitmap(bm);
	}

	private void setCoverBitmap(Bitmap bm) {
		// setting paint
		mPaint = new Paint();
		mPaint.setAlpha(0);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAntiAlias(true);

		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(20);// 设置线宽

		// set path
		mPath = new Path();

		// converting bitmap into mutable bitmap
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas();
		mCanvas.setBitmap(mBitmap);
		// drawXY will result on that Bitmap
		// be sure parameter is bm, not mBitmap
		int currX = width / 2 - bm.getWidth() / 2 - 34;

		if (isPad) {
			mCanvas.drawBitmap(bm, currX, 0, null);
		} else {
			mCanvas.drawBitmap(bm, 0, 0, null);
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, null);
		mCanvas.drawPath(mPath, mPaint);
		super.onDraw(canvas);
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);// 设置起点
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	float startX;
	float startY;
	List<Integer> listx = new ArrayList<Integer>();
	List<Integer> listy = new ArrayList<Integer>();
	private boolean DO_NOT = true;

	ArrayList<Point> points = new ArrayList<Point>();

	class Point {
		public Point(float x, float y) {
			this.x = (int) x;
			this.y = (int) y;
		}

		public int x;
		public int y;

		@Override
		public boolean equals(Object o) {
			if (o instanceof Point) {
				Point p = (Point) o;
				if (p.x == this.x && p.y == this.y) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = x;
			startY = y;
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			if (DO_NOT) {
				if (x > 110 && x < 320 && y > 35 && y < 85) {
					Point p = new Point(x, y);
					if (!points.contains(p)) {
						points.add(p);
					}
				}
				if (points.size() >= pointCount) {
					if (showAwardListener != null) {
						showAwardListener.showAwardSucceed();
						Log.e("onTouch", "listener");
					}
					DO_NOT = false;
				}
				// Log.e("add",
				// "listx.size():" + (listx.size()) + ",listy.size():" +
				// +listy.size());
			}
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();// 重新绘图，调用ondraw
			break;
		}
		return true;
	}

	private int getScreenHeight() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public interface OnShowAwardListener {
		public abstract void showAwardSucceed();
	}
}
