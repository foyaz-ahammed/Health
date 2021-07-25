package org.secuso.privacyfriendlyactivitytracker.water;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 물자료현시화면
 */
public class WaterActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_water);
        super.onCreate(savedInstanceState);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_area, new WaterFragment(), "WaterFragment");
        fragmentTransaction.commit();
    }
}