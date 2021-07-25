package org.secuso.privacyfriendlyactivitytracker.utils;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.android.tu.circlelibrary.CirclePercentBar;
import com.android.tu.circlelibrary.DisplayUtil;
import com.android.tu.circlelibrary.R.color;
import com.android.tu.circlelibrary.R.styleable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;

/**
 * 걸음수표시를 위한 circle bar
 */
public class CustomCirclePercentBar extends CirclePercentBar {
    private Context mContext;
    private int mArcColor;
    private int mArcWidth;
    private int mCenterTextColor;
    private int mCenterTextSize;
    private int mCircleRadius;
    private Paint arcPaint;
    private Paint arcCirclePaint;
    private Paint centerTextPaint;
    private Paint stepTextPaint;
    private RectF arcRectF;
    private Rect textBoundRect;
    private Rect stepBoundRect;
    private int mCurData; // 현재 표시한 자료
    private int mGoal = 10000; // 목표자료
    private int arcStartColor;
    private int arcEndColor;
    private Paint startCirclePaint;
    private Paint shadowPaint;

    Bitmap canvasBitmap;

    public CustomCirclePercentBar(Context context) {
        this(context, (AttributeSet)null);
    }

    public CustomCirclePercentBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCirclePercentBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCurData = 0;
        this.mContext = context;
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, styleable.CirclePercentBar, defStyleAttr, 0);
        this.mArcColor = typedArray.getColor(styleable.CirclePercentBar_arcColor, 16711680);
        this.mArcWidth = typedArray.getDimensionPixelSize(styleable.CirclePercentBar_arcWidth, DisplayUtil.dp2px(context, 20.0F));
        this.mCenterTextColor = typedArray.getColor(styleable.CirclePercentBar_centerTextColor, 255);
        this.mCenterTextSize = typedArray.getDimensionPixelSize(styleable.CirclePercentBar_centerTextSize, DisplayUtil.dp2px(context, 20.0F));
        this.mCircleRadius = typedArray.getDimensionPixelSize(styleable.CirclePercentBar_circleRadius, DisplayUtil.dp2px(context, 100.0F));
        this.arcStartColor = typedArray.getColor(styleable.CirclePercentBar_arcStartColor, ContextCompat.getColor(this.mContext, color.colorStart));
        this.arcEndColor = typedArray.getColor(styleable.CirclePercentBar_arcEndColor, ContextCompat.getColor(this.mContext, color.colorEnd));
        typedArray.recycle();
        this.initPaint();
    }

    /**
     * 초기화함수
     */
    private void initPaint() {
        this.startCirclePaint = new Paint(1);
        this.startCirclePaint.setStyle(Style.FILL);
        this.startCirclePaint.setColor(this.arcStartColor);
        this.arcCirclePaint = new Paint(1);
        this.arcCirclePaint.setStyle(Style.STROKE);
        this.arcCirclePaint.setStrokeWidth((float)this.mArcWidth);
        this.arcCirclePaint.setColor(ContextCompat.getColor(this.mContext, color.colorCirclebg));
        this.arcCirclePaint.setStrokeCap(Cap.ROUND);
        this.shadowPaint = new Paint(1);
        this.shadowPaint.setColor(this.mArcColor);
        this.shadowPaint.setShadowLayer(12, 0, 0, Color.GRAY);
        this.arcPaint = new Paint(1);
        this.arcPaint.setStyle(Style.STROKE);
        this.arcPaint.setStrokeWidth((float)this.mArcWidth);
        this.arcPaint.setColor(this.mArcColor);
        this.arcPaint.setStrokeCap(Cap.ROUND);
        this.centerTextPaint = new Paint(1);
        this.centerTextPaint.setColor(this.mCenterTextColor);
        this.centerTextPaint.setTextSize((float)this.mCenterTextSize);
        this.stepTextPaint = new Paint(1);
        this.stepTextPaint.setColor(this.mCenterTextColor);
        this.stepTextPaint.setTextSize(40.0f);
        this.arcRectF = new RectF();
        this.textBoundRect = new Rect();
        this.stepBoundRect = new Rect();
    }

    /**
     * 그리기함수
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_sneakers);
        canvasBitmap = drawableToBitmap(drawable).copy(Bitmap.Config.ARGB_8888, true);

        canvas.rotate(-90.0F, (float)(this.getWidth() / 2), (float)(this.getHeight() / 2));
        this.arcRectF.set((float)(this.getWidth() / 2 - this.mCircleRadius + this.mArcWidth / 2), (float)(this.getHeight() / 2 - this.mCircleRadius + this.mArcWidth / 2), (float)(this.getWidth() / 2 + this.mCircleRadius - this.mArcWidth / 2), (float)(this.getHeight() / 2 + this.mCircleRadius - this.mArcWidth / 2));
        canvas.drawArc(this.arcRectF, 0.0F, 360.0F, false, this.arcCirclePaint);
        this.arcPaint.setShader(new SweepGradient((float)(this.getWidth() / 2), (float)(this.getHeight() / 2), this.arcStartColor, this.arcEndColor));
        canvas.drawArc(this.arcRectF, 0.0F, 360.0F * this.mCurData / mGoal, false, this.arcPaint);

        // 현재 자료가 목표에 거의 접근한 경우 shadow 효과 추가
        if (mCurData > mGoal * 0.9) {
            double currentAngle = 360.0F * this.mCurData / mGoal;
            double shadowXRadius = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.cos(Math.toRadians(currentAngle));
            double shadowYRadius = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.sin(Math.toRadians(currentAngle));
            double noneShadowXRadius = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.cos(Math.toRadians(currentAngle - 7));
            double noneShadowYRadius = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.sin(Math.toRadians(currentAngle - 7));
            double noneShadowXRadiusBefore = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.cos(Math.toRadians(currentAngle - 3));
            double noneShadowYRadiusBefore = ((float) (this.getWidth() / 2 - mArcWidth / 2)) * Math.sin(Math.toRadians(currentAngle - 3));
            canvas.drawCircle((float) (this.getWidth() / 2 + shadowXRadius),
                    (float) (this.getWidth() / 2 + (float) shadowYRadius), (float)(mArcWidth / 2), shadowPaint);
            canvas.drawCircle((float) (this.getWidth() / 2 + noneShadowXRadius),
                    (float) (this.getWidth() / 2 + (float) noneShadowYRadius), (float)(mArcWidth / 2), startCirclePaint);
            canvas.drawCircle((float) (this.getWidth() / 2 + noneShadowXRadiusBefore),
                    (float) (this.getWidth() / 2 + (float) noneShadowYRadiusBefore), (float)(mArcWidth / 2), startCirclePaint);
        }
        canvas.rotate(90.0F, (float)(this.getWidth() / 2), (float)(this.getHeight() / 2));
        canvas.drawCircle((float)(this.getWidth() / 2), (float)(this.getHeight() / 2 - this.mCircleRadius + this.mArcWidth / 2), (float)(this.mArcWidth / 2), this.startCirclePaint);
        String data = String.valueOf(this.mCurData);
        String step = getResources().getString(R.string.main_circlebar_steps);
        this.centerTextPaint.getTextBounds(data, 0, data.length(), this.textBoundRect);
        this.stepTextPaint.getTextBounds(step, 0, step.length(), this.stepBoundRect);
        canvas.drawBitmap(canvasBitmap, (float)(this.getWidth() / 2 - canvasBitmap.getWidth() / 2),
                (float)(this.getHeight() / 2 - this.textBoundRect.height() / 2 - this.centerTextPaint.getTextSize() - Utils.dip2px(8)), this.centerTextPaint);
        canvas.drawText(data, (float)(this.getWidth() / 2 - this.textBoundRect.width() / 2), (float)(this.getHeight() / 2 + this.textBoundRect.height() / 2), this.centerTextPaint);
        canvas.drawText(step, (float)(this.getWidth() / 2 - this.stepBoundRect.width() / 2),
                (float)(this.getHeight() / 2 + this.textBoundRect.height() / 2 + this.centerTextPaint.getTextSize()), this.stepTextPaint);
    }

    /**
     * 걸음수 상태표시함수
     * @param steps 걸음수
     * @param interpolator
     */
    public void setPercentData(int steps, TimeInterpolator interpolator) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(this.mCurData, steps);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                CustomCirclePercentBar.this.mCurData = value;
                CustomCirclePercentBar.this.invalidate();
            }
        });
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.start();
    }

    /**
     * 목표갱신
     * @param goal 갱신할 새로운 목표
     */
    public void setGoal(int goal) {
        mGoal = goal;
        invalidate();
    }

    /**
     * Drawable 을 Bitmap 로 변환하는 함수
     * @param drawable 변환할 drawable
     * @return 변환된 Bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
