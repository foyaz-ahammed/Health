package org.secuso.privacyfriendlyactivitytracker.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyactivitytracker.adapters.ViewPagerAdapter;
import org.secuso.privacyfriendlyactivitytracker.fragments.HealthFragment;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.fragments.ExerciseFragment;
import org.secuso.privacyfriendlyactivitytracker.fragments.SettingsFragment;
import org.secuso.privacyfriendlyactivitytracker.models.Stopwatch;
import org.secuso.privacyfriendlyactivitytracker.utils.StepDetectionServiceHelper;

import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.PAUSED;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RUNNING;

/**
 * 기본화면
 */
public class MainActivity extends AppCompatActivity {
    /** Stopwatch의 상태를 보관하는 key */
    private static final String STATE = "sw_state";

    ViewPager viewPager;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] navIcons = {
                R.drawable.ic_health,
                R.drawable.ic_run,
                R.drawable.ic_main_settings
        };

        int[] navIconsActive = {
                R.drawable.ic_health_active,
                R.drawable.ic_run_active,
                R.drawable.ic_main_settings_active
        };

        int[] navLabels = {
                R.string.app_name,
                R.string.exercise,
                R.string.action_settings
        };

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();

                TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);

                tab_label.setTextColor(getResources().getColor(R.color.tab_active_color));
                tab_icon.setImageResource(navIconsActive[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.getCustomView();

                TextView tab_label = (TextView) tabView.findViewById(R.id.nav_label);
                ImageView tab_icon = (ImageView) tabView.findViewById(R.id.nav_icon);

                tab_label.setTextColor(getResources().getColor(R.color.black));
                tab_icon.setImageResource(navIcons[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for (int i = 0; i < tabLayout.getTabCount(); i ++) {
            @SuppressLint("InflateParams") LinearLayout tab = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.nav_tab, null);
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);
            tab_label.setText(getResources().getString(navLabels[i]));
            tab_icon.setImageDrawable(ContextCompat.getDrawable(this, navIcons[i]));
            if (i == 0) {
                tab_icon.setImageDrawable(ContextCompat.getDrawable(this, navIconsActive[i]));
                tab_label.setTextColor(getResources().getColor(R.color.tab_active_color));
            }
            tabLayout.getTabAt(i).setCustomView(tab);
        }

        StepDetectionServiceHelper.startAllIfEnabled(true, getApplicationContext());

        //Stopwatch상태가 진행중이거나 중지된 상태이면 운동측정화면 현시
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final int stateIndex = sharedPref.getInt(STATE, RESET.ordinal());
        final Stopwatch.State state = Stopwatch.State.values()[stateIndex];
        if (state == RUNNING || state == PAUSED) {
            final Intent showTraining = new Intent(this, ExerciseMeasureActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(showTraining);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 단추를 두번눌렀으면 app 끝내기
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.touch_again_exit_app), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    /**
     * 건강관리화면, 운동화면, 설정화면들을 viewpager adapter에 추가하는 함수
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HealthFragment.newInstance(), null);
        adapter.addFragment(ExerciseFragment.newInstance(), null);
        adapter.addFragment(SettingsFragment.newInstance(), null);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}