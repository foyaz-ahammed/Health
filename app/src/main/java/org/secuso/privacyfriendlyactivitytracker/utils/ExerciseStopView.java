package org.secuso.privacyfriendlyactivitytracker.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;

/**
 * 운동측정중지 view
 */
public class ExerciseStopView extends ViewGroup {
    View mStopBtn;

    Paint mBorderPaint = new Paint();

    boolean isClicked = false;
    int borderAngle = 0;
    ObjectAnimator borderAnimator;

    OnFinishListener mOnFinishListener;

    public ExerciseStopView(Context context) {
        this(context, null);
    }

    public ExerciseStopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExerciseStopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        if (isClicked)
            mStopBtn.layout(Utils.dip2px(7), Utils.dip2px(7), getWidth() - Utils.dip2px(7), getHeight() - Utils.dip2px(7));
        else
            mStopBtn.layout(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (isClicked) {
            RectF circleBounds = new RectF(Utils.dip2px(3), Utils.dip2px(3), getWidth() - Utils.dip2px(3), getHeight() - Utils.dip2px(3));
            canvas.drawArc(circleBounds, -90, borderAngle, false, mBorderPaint);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    public int getAngle() {
        return borderAngle;
    }

    public void setAngle(int value){
        borderAngle = value;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClicked = true;
                requestLayout();
                //Create or reverse border animation when action is down
                if (borderAnimator != null && borderAnimator.isRunning()) {
                    borderAnimator.reverse();
                } else {
                    borderAnimator = ObjectAnimator.ofInt(this, "angle", 0, 360).setDuration(700);
                    borderAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (borderAngle == 360) {
                                if (mOnFinishListener != null)
                                    mOnFinishListener.onFinished();
                            }
                            isClicked = false;
                            requestLayout();
                        }
                    });
                    borderAnimator.start();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (borderAnimator != null && borderAnimator.isRunning()) {
                    borderAnimator.reverse();
                }
                if (borderAngle != 360) {
                    if (mOnFinishListener != null)
                        mOnFinishListener.onCanceled();
                }
                return true;

        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOnFinishListener = null;
    }

    /**
     * 초기화함수
     */
    private void init() {
        mStopBtn = LayoutInflater.from(getContext()).inflate(R.layout.layout_exercise_stop, this,false);
        addView(mStopBtn);

        //Init paint of stop progress
        mBorderPaint.setColor(getResources().getColor(R.color.exercise_stop_border_color));
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        mBorderPaint.setStrokeWidth(Utils.dip2px(4));
        mBorderPaint.setAntiAlias(true);
    }

    public void setOnFinishListener(OnFinishListener listener) {
        mOnFinishListener = listener;
    }

    public interface OnFinishListener {
        void onFinished();

        void onCanceled();
    }
}
