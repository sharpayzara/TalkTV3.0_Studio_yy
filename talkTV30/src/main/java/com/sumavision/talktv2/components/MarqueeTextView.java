package com.sumavision.talktv2.components;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

    /** �Ƿ�ֹͣ���� */
    private boolean mStopMarquee;
    private String mText;//�ı�����
    private float mCoordinateX = 1280;//��ǰ����λ��
    private float mCoordinateY = 50;
    private float mTextWidth;//�ı����
    private int mScrollWidth = 1280;//����������
    private int speed = 1;//�����ٶ�
    public float getCurrentPosition() {
		return mCoordinateX;
	}
    
	public float getmCoordinateY() {
		return mCoordinateY;
	}

	public void setCurrentHeight(float mCoordinateY) {
		this.mCoordinateY = mCoordinateY;
	}

	public void setCurrentPosition(float mCoordinateX) {
		this.mCoordinateX = mCoordinateX;
	}

	public int getScrollWidth() {
		return mScrollWidth;
	}

	public void setScrollWidth(int mScrollWidth) {
		this.mScrollWidth = mScrollWidth;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text) {
        this.mText = text;
        mTextWidth = getPaint().measureText(mText);
        //mTextWidth = 1280;
        if (mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0, 10);
    }


	@Override
    protected void onAttachedToWindow() {
        mStopMarquee = false;
        if (!isEmpty(mText))
            mHandler.sendEmptyMessageDelayed(0, 2000);
        super.onAttachedToWindow();
    }

    public static boolean isEmpty(String str) {
    	return str == null || str.length() == 0;
	}

	@Override
    protected void onDetachedFromWindow() {
        mStopMarquee = true;
        if (mHandler.hasMessages(0))
            mHandler.removeMessages(0);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isEmpty(mText))
            canvas.drawText(mText, mCoordinateX, mCoordinateY, getPaint());
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:                    
            	if (mCoordinateX < (-mTextWidth)) {//���ֹ������ˣ��ӹ���������ұ߳���
            		mCoordinateX = mScrollWidth;
                	invalidate();
                	if (!mStopMarquee) {
                	sendEmptyMessageDelayed(0, 500);
                	}
                } else {
                    mCoordinateX -= speed;
                    invalidate();
                    if (!mStopMarquee) {
                        sendEmptyMessageDelayed(0, 30);
                    }
                }

                break;
            }
            super.handleMessage(msg);
        }
    };

}
