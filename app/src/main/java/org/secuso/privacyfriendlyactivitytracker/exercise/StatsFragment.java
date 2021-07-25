package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.adapters.ViewPagerAdapter;

/**
 * 주별, 월별, 년별, 전체상태를 보여주는 fragment들을 담고있는 fragment
 */
public class StatsFragment extends Fragment {
    StatsActivity statsActivity;
    FrameLayout mLoading;

    StatsFragment(StatsActivity statsActivity) {
        this.statsActivity = statsActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container, false);

        mLoading = view.findViewById(R.id.loading);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);

        return view;
    }

    /**
     * 읽기 UI 취소함수
     */
    public void cancelLoading() {
        if (mLoading.getVisibility() == View.VISIBLE)
            mLoading.setVisibility(View.GONE);
    }

    /**
     * 주별, 월별, 년별 전체 상태현시화면들을 viewpager adapter에 추가하는 함수
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(WeekStatsFragment.newInstance(statsActivity, this), getString(R.string.week));
        adapter.addFragment(MonthStatsFragment.newInstance(statsActivity), getString(R.string.month));
        adapter.addFragment(YearStatsFragment.newInstance(statsActivity), getString(R.string.year));
        adapter.addFragment(TotalStatsFragment.newInstance(statsActivity), getString(R.string.total));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }
}