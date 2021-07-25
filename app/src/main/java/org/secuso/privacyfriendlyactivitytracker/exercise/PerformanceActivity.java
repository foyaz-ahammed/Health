package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.os.Bundle;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * SWOLF 상세정보화면
 */
public class PerformanceActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_performance);
        super.onCreate(savedInstanceState);
    }
}