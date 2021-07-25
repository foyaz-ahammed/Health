package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 운동측정 끝내기 대화창
 */
public class TrainingWarningDialog extends Dialog implements
        View.OnClickListener {

    PickerLayout mLengthPicker;
    public Button mContinueBtn, mEndBtn;
    TextView mTitle;

    private OnEndClickListener mOnEndClickListener;

    public TrainingWarningDialog(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_training_warning);

        mTitle = findViewById(R.id.title);
        mLengthPicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        mContinueBtn = (Button) findViewById(R.id.btn_continue);
        mEndBtn = (Button) findViewById(R.id.btn_end);

        mContinueBtn.setOnClickListener(this);
        mEndBtn.setOnClickListener(this);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mLengthPicker.setWidth((int) (entireWidth * 0.9f));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_end:
                dismiss();
                if(mOnEndClickListener != null)
                    mOnEndClickListener.onEndClicked(true);
                break;
            case R.id.btn_continue:
                dismiss();
                if(mOnEndClickListener != null)
                    mOnEndClickListener.onEndClicked(false);
                break;
        }
    }

    public void setOnEndClickListener(OnEndClickListener listener) {
        mOnEndClickListener = listener;
    }

    public interface OnEndClickListener {
        void onEndClicked(boolean isEndClicked);
    }
}