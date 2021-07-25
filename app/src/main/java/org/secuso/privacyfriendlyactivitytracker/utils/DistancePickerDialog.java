package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 거리입력대화창
 */
public class DistancePickerDialog extends Dialog implements
        View.OnClickListener {

    public Button yes, no;
    EditText mDistance;
    TextView mUnit;
    TextView mTitle;

    boolean isMeter;
    float min, max;
    String title = null;

    private OnDistanceSelectedListener mOnDistanceSelectedListener;

    public DistancePickerDialog(Context context, String title, boolean isMeter, float min, float max) {
        super(context);
        this.title = title;
        this.isMeter = isMeter;
        this.min = min;
        this.max = max;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.distance_picker);

        PickerLayout mDistancePicker = findViewById(R.id.parentLayout);
        mTitle = findViewById(R.id.title);
        mDistance = findViewById(R.id.distance);
        mUnit = findViewById(R.id.unit);
        mTitle.setText(title);
        mUnit.setText(getContext().getResources().getString(isMeter ? R.string.meter : R.string.kilometer));
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);

        yes.setOnClickListener(this);
        no.setOnClickListener(this);

        yes.setClickable(false);
        yes.setAlpha(0.3f);

        mDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    yes.setClickable(true);
                    yes.setAlpha(1.0f);
                } else {
                    yes.setClickable(false);
                    yes.setAlpha(0.3f);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        int entireWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mDistancePicker.setWidth((int) (entireWidth * 0.9f));
    }

    @SuppressLint({"ShowToast", "NonConstantResourceId", "StringFormatInvalid"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (!mDistance.getText().toString().equals("")) {
                    float distance = Float.parseFloat(mDistance.getText().toString());
                    if (isMeter) {
                        if (distance < min || distance > max) {
                            Toast.makeText(getContext(), getContext().getString(R.string.distance_range_limit_meter, min, (int) max), Toast.LENGTH_LONG).show();
                        } else {
                            dismiss();
                            if (mOnDistanceSelectedListener != null) {
                                mOnDistanceSelectedListener.onDistanceChanged(distance);
                            }
                        }
                    } else {
                        if (distance > max || distance < min) {
                            Toast.makeText(getContext(), R.string.distance_range_limit_kilometer, Toast.LENGTH_LONG).show();
                        } else {
                            dismiss();
                            if (mOnDistanceSelectedListener != null) {
                                mOnDistanceSelectedListener.onDistanceChanged(distance);
                            }
                        }
                    }
                }

                break;
            case R.id.btn_no:
            default:
                dismiss();
                break;
        }
    }

    public void setOnDistanceSelectedListener(OnDistanceSelectedListener listener) {
        mOnDistanceSelectedListener = listener;
    }

    public interface OnDistanceSelectedListener {
        void onDistanceChanged(float distance);
    }
}