package org.secuso.privacyfriendlyactivitytracker.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;

/**
 * 혈압상태표시 view
 */
public class TriangularIndicatorBar extends View {
    private Rect textBoundRect;
    public TriangularIndicatorBar(Context context) {
        this(context, null);
    }

    public TriangularIndicatorBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangularIndicatorBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TriangularIndicatorBar);
        enableLevelText = attributes.getBoolean(R.styleable.TriangularIndicatorBar_enable_level_text, true);
        init();
    }
    private Path mPath;
    private int mStartLeft = Utils.dip2px(6);
    private int mTriangleLeftX = 0;
    private int mTriangleRightX = Utils.dip2px(12);
    private int mTriangleY = Utils.dip2px(12);
    private int mTextY = Utils.dip2px(35);
    private int mTextSize = Utils.dip2px(10);
    private int mMoveX = 0;
    private int mWidth = 0;
    private int mTopbgHeight = Utils.dip2px(4);
    private Paint mTopbgPaint;
    private RectF mTopbgRect;
    private Paint mPaintText;
    private Paint mPaintTriangle;
    private int[] mClolorArray;
    private float[] mGradientPostionArray;
    private int mDivisor = 5;
    private int[] mTextMember = new int[]{0, 1, 2, 3, 4, 5};
    private String[] mTextArray = new String[]{"0", "20", "40", "60", "80", "100"};
    private int mTotalInt = 100;
    private static final int DEFAULT_HEIGHT = Utils.dip2px(30);

    private float mGradientOffset = getResources().getDimension(R.dimen.indicatorbar_offset);
    boolean enableLevelText = true;

    /**
     * 초기화함수
     */
    private void init() {
        mPath = new Path();
        mTopbgPaint = new Paint();
        mTopbgRect = new RectF();
        mPaintText = new Paint();
        mPaintTriangle = new Paint();
        mPaintTriangle.setColor(getResources().getColor(R.color.default_text_color));
        mClolorArray = new int[]{getResources().getColor(R.color.gradient_0), getResources().getColor(R.color.gradient_50), getResources().getColor(R.color.gradient_75), getResources().getColor(R.color.gradient_100)};
        mGradientPostionArray = new float[]{0, 0.5f, 0.75f, 1};
        mTextArray = new String[]{getResources().getString(R.string.low), getResources().getString(R.string.normal), getResources().getString(R.string.high)};
        mDivisor = 10;
        mTextMember = new int[]{0, 5, 10};
        mTotalInt = 100;
        textBoundRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode == MeasureSpec.UNSPECIFIED
                || heightMode == MeasureSpec.AT_MOST){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        LinearGradient backGradient = new LinearGradient(0, 0, mWidth, 0, mClolorArray, mGradientPostionArray, Shader.TileMode.CLAMP);
        mTopbgPaint.setShader(backGradient);
        mTopbgRect.left = mStartLeft;
        mTopbgRect.right = mWidth - mStartLeft;
        mTopbgRect.top = 0;
        mTopbgRect.bottom = mTopbgHeight;

        canvas.save();
        canvas.translate(0, mGradientOffset);
        canvas.drawRoundRect(mTopbgRect, 5, 5, mTopbgPaint);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setColor(getResources().getColor(R.color.default_text_color));
        canvas.restore();

        if (enableLevelText)
            drawTheText(canvas, mPaintText);

        mPath.reset();
        mPath.moveTo(mStartLeft + mMoveX, 5);// 起点
        mPath.lineTo(mTriangleLeftX + mMoveX, mTriangleY);
        mPath.lineTo(mTriangleRightX + mMoveX, mTriangleY);
        mPath.close(); //

        canvas.save();
        canvas.rotate(180, mMoveX + (mTriangleRightX - mTriangleLeftX) / 2f, (mTriangleY + 5)/2f);
        canvas.drawPath(mPath, mPaintTriangle);
        canvas.restore();
    }

    /**
     * 상태기준점들의 text 그리기함수
     * @param canvas
     * @param paint
     */
    private void drawTheText(Canvas canvas, Paint paint) {
        for (int i = 0; i < mTextArray.length; i++) {
            paint.getTextBounds(mTextArray[i], 0, mTextArray[i].length(), this.textBoundRect);
            if (i == 2) {
                canvas.drawText(mTextArray[i], mWidth - textBoundRect.width(), mTextY, paint);
            } else if (i == 1) {
                canvas.drawText(mTextArray[i], (mWidth / mDivisor) * mTextMember[i] - textBoundRect.width() / 2, mTextY, paint);
            } else {
                canvas.drawText(mTextArray[i], (mWidth / mDivisor) * mTextMember[i], mTextY, paint);
            }
        }
    }

    private int mScore = 0;
    public void setScore(int score, int totalInt) {
        mScore = score;
        mTotalInt = totalInt;
        if (score < 0) mScore = 0;
        if (score > mTotalInt) mScore = mTotalInt;
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator animator = ValueAnimator.ofInt(mMoveX, (((getWidth() - mStartLeft * 2) * mScore / mTotalInt)));
                animator.setDuration(500);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mMoveX = Integer.parseInt(animation.getAnimatedValue().toString());
                        invalidate();
                    }
                });
                animator.start();
            }
        });
    }
}

