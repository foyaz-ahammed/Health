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
 * 삭제확인대화창
 */
public class DeleteConfirmDialog extends Dialog implements View.OnClickListener {
    ConfirmLayout mDeleteConfirm;
    Button mCancel;
    Button mDelete;

    OnButtonClickListener mButtonClickListener;

    public DeleteConfirmDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.delete_confirm_layout);
        mDeleteConfirm = findViewById(R.id.delete_confirm);
        mCancel = findViewById(R.id.cancel);
        mDelete = findViewById(R.id.ok);
        mCancel.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mDeleteConfirm.setWidth((int) (entireWidth * 0.90));

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    @Override
    public void onClick(View view) {
        if (view == mCancel) {
            dismiss();
        }
        else if (view == mDelete) {
            if (mButtonClickListener != null)
                mButtonClickListener.onDeleteClicked();
            dismiss();
        }
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        void onDeleteClicked();
    }
}
