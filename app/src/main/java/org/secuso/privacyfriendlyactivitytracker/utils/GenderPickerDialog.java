package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.Objects;

/**
 * 성별선택 대화창
 */
public class GenderPickerDialog extends Dialog implements
        View.OnClickListener {

    PickerLayout mLengthPicker;
    RadioButton mMaleBtn;
    RadioButton mFemaleBtn;
    LinearLayout mMaleSelectArea;
    LinearLayout mFemaleSelectArea;
    Button no;
    TextView mTitle;

    String title;

    SharedPreferences sharedPref;

    public GenderPickerDialog(Context context, String title) {
        super(context);
        this.title = title;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gender_picker);

        mTitle = findViewById(R.id.title);
        mLengthPicker = findViewById(R.id.parentLayout);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Setup dialog at bottom
        no = (Button) findViewById(R.id.btn_no);
        mMaleSelectArea = findViewById(R.id.male_select_area);
        mFemaleSelectArea = findViewById(R.id.female_select_area);
        mMaleBtn = findViewById(R.id.male_btn);
        mFemaleBtn = findViewById(R.id.female_btn);
        mMaleSelectArea.setOnClickListener(this);
        mFemaleSelectArea.setOnClickListener(this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String gender = sharedPref.getString(getContext().getString(R.string.pref_gender), "");
        mMaleBtn.setChecked(gender.equals("male"));
        mFemaleBtn.setChecked(gender.equals("female"));

        mTitle.setText(title);

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
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (v.getId()) {
            case R.id.btn_no:
                dismiss();
                break;
            case R.id.male_select_area:
                mMaleBtn.setChecked(true);
                mFemaleBtn.setChecked(false);
                editor.putString(getContext().getString(R.string.pref_gender), "male");
                editor.apply();
                dismiss();
                break;
            case R.id.female_select_area:
                mMaleBtn.setChecked(false);
                mFemaleBtn.setChecked(true);
                editor.putString(getContext().getString(R.string.pref_gender), "female");
                editor.apply();
                dismiss();
                break;
        }
    }
}