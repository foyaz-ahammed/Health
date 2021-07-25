package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.adapters.ViewPagerAdapter;

/**
 * 심박수통계화면
 */
public class HeartRateActivity extends ToolbarActivity implements View.OnClickListener {
    TextView mMeasure;
    FrameLayout mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_heart_rate);
        super.onCreate(savedInstanceState);

        mMeasure = findViewById(R.id.measure);
        mMeasure.setOnClickListener(this);
        mLoading = findViewById(R.id.loading);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * 일별, 주별, 월별, 년별 심박수통계화면들을 viewpager adapter에 추가하는 함수
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(DayHeartRateFragment.newInstance(this), getString(R.string.day));
        adapter.addFragment(WeekHeartRateFragment.newInstance(), getString(R.string.week));
        adapter.addFragment(MonthHeartRateFragment.newInstance(), getString(R.string.month));
        adapter.addFragment(YearHeartRateFragment.newInstance(), getString(R.string.year));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.measure) {
            Intent intent = new Intent(this, HeartRateMeasureActivity.class);
//            intent.putExtra(HeartRateMeasureActivity.MEASURE_RESULT, 113);
//            intent.putExtra(HeartRateMeasureActivity.MEASURE_TIME, System.currentTimeMillis());
            startActivity(intent);
        }
    }

    /**
     * 읽기 UI 취소함수
     */
    public void cancelLoading() {
        if (mLoading.getVisibility() == View.VISIBLE)
            mLoading.setVisibility(View.GONE);
    }
}