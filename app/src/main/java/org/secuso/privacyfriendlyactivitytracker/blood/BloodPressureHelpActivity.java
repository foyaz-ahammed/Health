package org.secuso.privacyfriendlyactivitytracker.blood;

import android.os.Bundle;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 혈압도움말화면
 */
public class BloodPressureHelpActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_blood_pressure_help);
        super.onCreate(savedInstanceState);
    }
}