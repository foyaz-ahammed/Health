package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.adapters.ViewPagerAdapter;

import java.util.Objects;

/**
 * PeriodFragment, RecordFragment들을 담고 있는 Fragment
 */
public class CycleFragment extends Fragment implements PeriodFragment.OnFragmentInteractionListener {
    ViewPager viewPager;
    FrameLayout mLoading;

    public CycleFragment() {
        // Required empty public constructor
    }

    public static CycleFragment newInstance() {
        CycleFragment fragment = new CycleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cycle, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        container.removeAllViews();

        mLoading = view.findViewById(R.id.loading);

        int[] navIcons = {
                R.drawable.ic_cycle,
                R.drawable.ic_add
        };

        int[] navLabels = {
                R.string.cycle,
                R.string.records
        };

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i ++) {
            @SuppressLint("InflateParams") LinearLayout tab = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.nav_tab, null);
            TextView tab_label = (TextView) tab.findViewById(R.id.nav_label);
            ImageView tab_icon = (ImageView) tab.findViewById(R.id.nav_icon);
            tab_label.setText(getResources().getString(navLabels[i]));
            tab_icon.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), navIcons[i]));
            tabLayout.getTabAt(i).setCustomView(tab);
        }

        setHasOptionsMenu(true);

        return view;
    }

    /**
     * 생리현시화면, 생리추가화면들을 viewPager의 adapter에 추가하는 함수
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        new ViewPagerAdapter(null);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(PeriodFragment.newInstance(this), getString(R.string.cycle));
        adapter.addFragment(RecordFragment.newInstance(), getString(R.string.records));
        viewPager.setAdapter(adapter);
    }

    /**
     * 읽기 UI취소 함수
     */
    public void cancelLoading() {
        if (mLoading.getVisibility() == View.VISIBLE)
            mLoading.setVisibility(View.GONE);
    }

    @Override
    public void changeFragment() {
        viewPager.setCurrentItem(1, true);
    }
}