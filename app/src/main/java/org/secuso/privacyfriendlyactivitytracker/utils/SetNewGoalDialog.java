package org.secuso.privacyfriendlyactivitytracker.utils;

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

import androidx.annotation.NonNull;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.ConfirmLayout;

import java.util.Objects;

/**
 * 새 목표설정권고 대화창
 */
public class SetNewGoalDialog extends Dialog implements View.OnClickListener {
    ConfirmLayout mSetNewGoal;
    Button mCancel;
    Button mSet;

    OnButtonClickListener mButtonClickListener;

    public SetNewGoalDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.layout_set_new_goal);
        mSetNewGoal = findViewById(R.id.set_new_goal);
        mCancel = findViewById(R.id.cancel);
        mSet = findViewById(R.id.ok);
        mCancel.setOnClickListener(this);
        mSet.setOnClickListener(this);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mSetNewGoal.setWidth((int) (entireWidth * 0.90));

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    @Override
    public void onClick(View view) {
        if (view == mCancel) {
            if (mButtonClickListener != null)
                mButtonClickListener.onCancelClicked();
            dismiss();
        }
        else if (view == mSet) {
            if (mButtonClickListener != null)
                mButtonClickListener.onSetClicked();
            dismiss();
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onSetClicked();

        void onCancelClicked();
    }
}
