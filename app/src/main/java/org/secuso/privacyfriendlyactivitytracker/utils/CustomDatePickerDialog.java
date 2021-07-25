package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import com.lany.picker.DatePicker;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 날자선택 대화창
 */
public class CustomDatePickerDialog extends Dialog implements
        android.view.View.OnClickListener {

    private PickerLayout mDatePicker;
    private TextView mDatePickerTitle;
    public Button yes, no;
    public DatePicker datePicker1;
    public int year, month, day;

    private OnDateSelectedListener mOnDateSelectedListener;

    public CustomDatePickerDialog(int _year, int _month, int _day, Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        year = _year;
        month = _month;
        day = _day;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_datepicker);

        mDatePicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        datePicker1 = (DatePicker)findViewById(R.id.date_picker_1);
        mDatePickerTitle = findViewById(R.id.title);
        mDatePickerTitle.setText(getDateString(year, month + 1, day));
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        datePicker1.setDayViewShown(true);
        datePicker1.init(year, month, day);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mDatePicker.setWidth((int) (entireWidth * 0.9f));
        datePicker1.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) ->
                mDatePickerTitle.setText(getDateString(year, monthOfYear + 1, dayOfMonth)));
    }

    public String getDateString(int year, int month, int day){
        return getContext().getResources().getString(R.string.date_format2, year, month, day);
    }

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