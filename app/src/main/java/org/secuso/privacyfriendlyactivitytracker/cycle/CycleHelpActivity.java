package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.os.Bundle;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 생리도움말화면
 */
public class CycleHelpActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cycle_help);
        super.onCreate(savedInstanceState);
    }
}