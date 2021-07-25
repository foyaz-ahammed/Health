package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 생리화면
 */
public class CycleActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cycle);
        super.onCreate(savedInstanceState);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_area, new CycleFragment(), "CycleFragment");
        fragmentTransaction.commit();
    }

    /**
     * Menu생성 함수
     * @param menu 생성될 menu
     * @return true이면 menu 보여주기, false이면 보여주지 않기
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_cycle);
        return true;
    }

    /**
     * Menu 항목을 눌렀을때 처리를 진행하는 함수
     * @param item 눌러진 항목
     * @return 처리가 진행되였으면 true, 아니면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_edit_period:
                intent = new Intent(this, EditPeriodActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_help:
                intent = new Intent(this, CycleHelpActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}