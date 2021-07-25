package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 운동종목선택 대화창
 */
public class ActivityPickerDialog extends Dialog implements
        View.OnClickListener {

    private PickerLayout mActivityPicker;
    public Button no;
    LinearLayout mOutdoorRun, mOutdoorWalk, mOutdoorCycle, mIndoorRun, mPoolSwim;

    public int hour, minute;

    private OnActivitySelectedListener mOnActivitySelectedListener;

    public ActivityPickerDialog(Activity a) {
        super(a);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picker);

        mActivityPicker = findViewById(R.id.parentLayout);
        mOutdoorRun = findViewById(R.id.outdoor_run);
        mOutdoorRun.setOnClickListener(this);
        mOutdoorWalk = findViewById(R.id.outdoor_walk);
        mOutdoorWalk.setOnClickListener(this);
        mOutdoorCycle = findViewById(R.id.outdoor_cycle);
        mOutdoorCycle.setOnClickListener(this);
        mIndoorRun = findViewById(R.id.indoor_run);
        mIndoorRun.setOnClickListener(this);
        mPoolSwim = findViewById(R.id.pool_swim);
        mPoolSwim.setOnClickListener(this);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        no = (Button) findViewById(R.id.btn_no);
        no.setOnClickListener(this);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mActivityPicker.setWidth((int) (entireWidth * 0.9f));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_no) {
            dismiss();
        } else if (v.getId() == R.id.outdoor_run) {
            dismiss();
            if (mOnActivitySelectedListener != null)
                mOnActivitySelectedListener.onActivityChanged(1);
        } else if (v.getId() == R.id.outdoor_walk) {
            dismiss();
            if (mOnActivitySelectedListener != null)
                mOnActivitySelectedListener.onActivityChanged(2);
        } else if (v.getId() == R.id.outdoor_cycle) {
            dismiss();
            if (mOnActivitySelectedListener != null)
                mOnActivitySelectedListener.onActivityChanged(3);
        } else if (v.getId() == R.id.indoor_run) {
            dismiss();
            if (mOnActivitySelectedListener != null)
                mOnActivitySelectedListener.onActivityChanged(4);
        } else if (v.getId() == R.id.pool_swim) {
            dismiss();
            if (mOnActivitySelectedListener != null)
                mOnActivitySelectedListener.onActivityChanged(5);
        }
    }

    public void setOnActivitySelectedListener(OnActivitySelectedListener listener) {
        mOnActivitySelectedListener = listener;
    }

    public interface OnActivitySelectedListener {
        void onActivityChanged(int whichActivity);
    }
}