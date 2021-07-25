package org.secuso.privacyfriendlyactivitytracker.weight;

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
 * 몸무게 일별, 주별, 월별, 년별 fragment를 포함하는 fragment
 */
public class WeightFragment extends Fragment {
    FrameLayout loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        container.removeAllViews();

        loading = view.findViewById(R.id.loading);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);

        return view;
    }

    /**
     * 화면읽기부분 취소하는 함수
     */
    public void cancelLoading() {
        if (loading.getVisibility() == View.VISIBLE)
            loading.setVisibility(View.GONE);
    }

    /**
     * 일별, 주별, 월별, 년별 몸무게현시화면들을 viewpager adapter에 추가하는 함수
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(DayWeightFragment.newInstance(this), getString(R.string.day));
        adapter.addFragment(WeekWeightFragment.newInstance(), getString(R.string.week));
        adapter.addFragment(MonthWeightFragment.newInstance(), getString(R.string.month));
        adapter.addFragment(YearWeightFragment.newInstance(), getString(R.string.year));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}
