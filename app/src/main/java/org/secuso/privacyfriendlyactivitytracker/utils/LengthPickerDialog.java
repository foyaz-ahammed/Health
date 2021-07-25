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
import android.widget.TextView;

import com.lany.numberpicker.NumberPicker;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 기간선택 대화창
 */
public class LengthPickerDialog extends Dialog implements
        View.OnClickListener {

    PickerLayout mLengthPicker;
    public Button yes, no;
    public NumberPicker numberPicker;
    TextView mTitle;
    public int hour, minute;

    private OnLengthChangedListener mOnLengthChangedListener;
    int min, max, current;
    String title;

    public LengthPickerDialog(Activity a, String title, int min, int max, int current) {
        super(a);
        this.min = min;
        this.max = max;
        this.current = current;
        this.title = title;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.length_picker);

        mTitle = findViewById(R.id.title);
        mLengthPicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        mTitle.setText(title);
        numberPicker = (NumberPicker) findViewById(R.id.length_picker);
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(current);
        numberPicker.setFormatter(i -> getContext().getResources().getString(R.string.with_days, i));

        try {
            @SuppressLint("DiscouragedPrivateApi") Method method = numberPicker.getClass().getDeclaredMethod("changeValueByOne", boolean.class);
            method.setAccessible(true);
            method.invoke(numberPicker, true);
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

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
            case R.id.btn_yes:
                dismiss();
                if(mOnLengthChangedListener != null)
                    mOnLengthChangedListener.onLengthChanged(numberPicker.getValue());
                break;
            case R.id.btn_no:
            default:
                dismiss();
                break;
        }
    }

    public void setOnLengthChangedListener(OnLengthChangedListener listener) {
        mOnLengthChangedListener = listener;
    }

    public interface OnLengthChangedListener {
        void onLengthChanged(int value);
    }
}