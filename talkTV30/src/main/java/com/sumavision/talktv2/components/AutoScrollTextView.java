package com.sumavision.talktv2.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.sumavision.talktv2.utils.AppUtil;

/**
 * 
 * @author
 * @version 1.0
 * @description 单行文本跑马灯控件
 */
public class AutoScrollTextView extends TextView implements OnClickListener {
	public final static String TAG = AutoScrollTextView.class.getSimpleName();

	private float textLength = 0f;// 文本长度
	private float viewWidth = 0f;
	private float step = 0f;// 文字的横坐标
	private float y = 0f;// 文字的纵坐标
	private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量
	private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量
	public boolean isStarting = false;// 是否开始滚动
	private Paint paint = null;// 绘图样式
	private String text = "";// 文本内容

	public AutoScrollTextView(Context context) {
		super(context);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		setOnClickListener(this);
	}

	/**
	 * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下
	 */
	public void init(WindowManager windowManager) {
		paint = getPaint();
		paint.setARGB(255, 180, 54, 34);
		text = getText().toString();

		textLength = paint.measureText(text);
		viewWidth = getWidth();
		if (viewWidth == 0) {
			viewWidth = AppUtil.getScreenWidth(getContext());
		}
		step = textLength;
		temp_view_plus_text_length = viewWidth + textLength;
		temp_view_plus_two_text_length = viewWidth + textLength * 2;
		y = getTextSize() + getPaddingTop();
	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		@SuppressWarnings("unused")
		private SavedState(Parcel in) {
			super(in);
			boolean[] b = null;
			if (in != null) {
				try {
					in.readBooleanArray(b);
					if (b != null && b.length > 0)
						isStarting = b[0];
					step = in.readFloat();
				} catch (NullPointerException e) {
				}
			}
		}
	}

	/**
	 * 开始滚动
	 */
	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	/**
	 * 停止滚动
	 */
	public void stopScroll() {
		isStarting = false;
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			canvas.drawText(text, temp_view_plus_text_length - step, y, paint);
			if (!isStarting) {
				return;
			}
			// 修改滚动速度
			step += 1.0;
			if (step > temp_view_plus_two_text_length)
				step = textLength;
			invalidate();
		}

	}

	@Override
	public void onClick(View v) {
		// if (isStarting)
		// stopScroll();
		// else
		// startScroll();

	}

}