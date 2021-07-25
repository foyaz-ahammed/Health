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

import com.lany.picker.TimePicker;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 시간선택 대화창
 */
public class CustomTimeDialog extends Dialog implements
        View.OnClickListener {

    PickerLayout mTimePicker;
    public Button yes, no;
    public TimePicker timePicker;
    int hour, minute, second;

    private OnTimeSelectedListener mOnTimeSelectedListener;

    public CustomTimeDialog(Context context, int hour, int minute, int second) {
        super(context);
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_timepicker);

        mTimePicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setCurrentSecond(second);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mTimePicker.setWidth((int) (entireWidth * 0.9f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                if(mOnTimeSelectedListener != null)
                    mOnTimeSelectedListener.onTimeChanged(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), timePicker.getCurrentSecond());
                break;
            case R.id.btn_no:
            default:
                dismiss();
                break;
        }
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener listener) {
        mOnTimeSelectedListener = listener;
    }

    public interface OnTimeSelectedListener {
        void onTimeChanged(int hour, int minute, int second);
    }
}