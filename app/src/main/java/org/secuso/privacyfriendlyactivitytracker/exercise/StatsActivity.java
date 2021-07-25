package org.secuso.privacyfriendlyactivitytracker.exercise;

import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;

/**
 * 운동상태화면
 */
public class StatsActivity extends ToolbarActivity {
    private WeekOnTypeSelectedListener weekTypeListener;
    private MonthOnTypeSelectedListener monthTypeListener;
    private YearOnTypeSelectedListener yearTypeListener;
    private TotalOnTypeSelectedListener totalTypeListener;

    int type; // 운동형태 0: 달리기 1: 걷기 2: 자전거타기 3: 수영

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_stats);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.workout_choose_view);
        Spinner spinner = (Spinner) findViewById(R.id.workout_choose_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.workout_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (weekTypeListener != null)
                    weekTypeListener.onTypeClick(position);
                if (monthTypeListener != null)
                    monthTypeListener.onTypeClick(position);
                if (yearTypeListener != null)
                    yearTypeListener.onTypeClick(position);
                if (totalTypeListener != null)
                    totalTypeListener.onTypeClick(position);
                type = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_area, new StatsFragment(this), "StatsFragment");
        fragmentTransaction.commit();
    }

    /**
     * 운동종목 얻는 함수
     * @return 운동종목
     */
    public int getType() {
        return type;
    }

    /**
     * 주별 종목보기에 대한 listener를 설정하는 함수
     * @param weekTypeListener
     */
    public void setWeekTypeListener(WeekOnTypeSelectedListener weekTypeListener) {
        this.weekTypeListener = weekTypeListener;
    }

    /**
     * 월별 종목보기에 대한 listener를 설정하는 함수
     * @param monthTypeListener
     */
    public void setMonthTypeListener(MonthOnTypeSelectedListener monthTypeListener) {
        this.monthTypeListener = monthTypeListener;
    }

    /**
     * 년별 종목보기에 대한 listener를 설정하는 함수
     * @param yearTypeListener
     */
    public void setYearTypeListener(YearOnTypeSelectedListener yearTypeListener) {
        this.yearTypeListener = yearTypeListener;
    }

    /**
     * 전체 종목보기에 대한 listener를 설정하는 함수
     * @param totalTypeListener
     */
    public void setTotalTypeListener(TotalOnTypeSelectedListener totalTypeListener) {
        this.totalTypeListener = totalTypeListener;
    }

    public interface WeekOnTypeSelectedListener {
        void onTypeClick(int type);
    }

    public interface MonthOnTypeSelectedListener {
        void onTypeClick(int type);
    }

    public interface YearOnTypeSelectedListener {
        void onTypeClick(int type);
    }

    public interface TotalOnTypeSelectedListener {
        void onTypeClick(int type);
    }
}