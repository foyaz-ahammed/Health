package org.secuso.privacyfriendlyactivitytracker.water;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.adapters.ViewPagerAdapter;

/**
 * 일별, 주별, 월별상태를 보여주는 fragment들을 담고있는 fragment
 */
public class WaterFragment extends Fragment {
    FrameLayout mLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setSubtitle(R.string.action_main);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        container.removeAllViews();

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
     * 일별, 주별, 월별상태현시화면들을 viewpager adapter에 추가하는 함수
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(DayWaterFragment.newInstance(this), getString(R.string.day));
        adapter.addFragment(WeekWaterFragment.newInstance(), getString(R.string.week));
        adapter.addFragment(MonthWaterFragment.newInstance(), getString(R.string.month));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}
