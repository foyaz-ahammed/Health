package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.zjun.widget.RuleView;

/**
 * RulerView class
 */
public class CustomRulerView extends RuleView {
    boolean touchable = true;
    public CustomRulerView(Context context) {
        super(context);
    }

    public CustomRulerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (touchable)
            return super.dispatchTouchEvent(event);
        return true;
    }

    /**
     * touch 가능상태 변경함수
     * @param touchable true 이면 touch 가능, false 이면 아님
     */
    public void setEnableTouchable(boolean touchable) {
        this.touchable = touchable;
        setAlpha(touchable ? 1.0f : 0.5f);
    }

    /**
     * 현재 touch 가능상태 얻는 함수
     * @return true 이면 touch 가능, false 이면 아님
     */
    public boolean getTouchable() {
        return touchable;
    }
}
