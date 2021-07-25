package org.secuso.privacyfriendlyactivitytracker.weight;

import android.os.Bundle;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 몸무게정보화면
 */
public class WeightHelpActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weight_help);
        super.onCreate(savedInstanceState);
    }
}