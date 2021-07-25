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

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Calendar;
import java.util.Objects;

/**
 * 생리날자선택 대화창
 */
public class CustomCycleDatePickerDialog extends Dialog implements
        View.OnClickListener {

    public Button yes, no;
    public android.widget.DatePicker datePicker1;
    public int year, month, day;

    private OnDateSelectedListener mOnDateSelectedListener;

    public CustomCycleDatePickerDialog(Context context) {
        super(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_cycle_datepicker);

        PickerLayout mDatePicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        datePicker1 = findViewById(R.id.date_picker_1);
        datePicker1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.dialog_bg));
        datePicker1.setMaxDate(System.currentTimeMillis());
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.MONTH, -2);
        datePicker1.setMinDate(minDate.getTimeInMillis());
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mDatePicker.setWidth((int) (entireWidth * 0.9f));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                dismiss();
                if(mOnDateSelectedListener != null)
                    mOnDateSelectedListener.onDateSelected(datePicker1.getYear(), datePicker1.getMonth(), datePicker1.getDayOfMonth());
                break;
            case R.id.btn_no:
            default:
                dismiss();
                break;
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mOnDateSelectedListener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int monthOfYear,
                            int dayOfMonth);
    }
}