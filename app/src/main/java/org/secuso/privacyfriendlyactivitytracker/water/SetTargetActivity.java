package org.secuso.privacyfriendlyactivitytracker.water;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomRulerView;

/**
 * 물목표설정화면
 */
public class SetTargetActivity extends ToolbarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    TextView mToggleText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch mTargetToggle;
    TextView mGlassCount;
    CustomRulerView mGlassRuler;
    LinearLayout mTargetSetArea;

    int target = 0; // 하루 물목표

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_target);
        super.onCreate(savedInstanceState);

        mToggleText = findViewById(R.id.toggle_text);
        mTargetToggle = findViewById(R.id.target_toggle);
        mGlassCount = findViewById(R.id.glass_count);
        mGlassRuler = findViewById(R.id.glass_ruler);
        mTargetSetArea = findViewById(R.id.target_set_area);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        updateTarget(sharedPref);

        mTargetToggle.setOnCheckedChangeListener((compoundButton, b) -> {
            target = b ? Integer.parseInt(mGlassCount.getText().toString()) : 0;
            editor.putInt(getString(R.string.pref_daily_water_goal), target);
            editor.apply();
            mToggleText.setText(b ? getResources().getString(R.string.on) : getResources().getString(R.string.off));
            mTargetSetArea.setAlpha(b ? 1.0f : 0.5f);
            mGlassRuler.setEnableTouchable(b);
        });

        mGlassRuler.setOnValueChangedListener(value -> {
            if (mGlassRuler.getTouchable()) {
                target = (int) value;
                mGlassCount.setText(String.valueOf((int) value));
            }
        });
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_daily_water_goal))) {
            updateTarget(sharedPreferences);
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_daily_water_goal), target);
        editor.apply();
        super.onBackPressed();
    }

    /**
     * 물목표갱신하는 함수
     * @param sharedPref
     */
    private void updateTarget(SharedPreferences sharedPref) {
        target = sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0);
        mToggleText.setText(target > 0 ? getResources().getString(R.string.on) : getResources().getString(R.string.off));
        mTargetSetArea.setAlpha(target > 0 ? 1.0f : 0.5f);
        mGlassRuler.setEnableTouchable(target > 0);
        mGlassCount.setText(String.valueOf(target > 0 ? target : 8));
        mGlassRuler.setCurrentValue(target > 0 ? target : 8);
        mTargetToggle.setChecked(target > 0);
    }
}